package com.bloodbank.servlets;

import com.bloodbank.util.FirebaseConfig;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Optimized JSON analytics API for dashboards.
 * Implements simple in-memory caching to prevent Firestore read spikes.
 */
@WebServlet(name = "AnalyticsServlet", urlPatterns = {"/api/analytics"})
public class AnalyticsServlet extends HttpServlet {

    // 🕒 Cache configuration: 1-minute expiry for responsiveness
    private static final long CACHE_EXPIRY_MS = 1 * 60 * 1000;
    private static final Map<String, CacheEntry> metricsCache = new ConcurrentHashMap<>();

    private static class CacheEntry {
        Object data;
        long timestamp;
        CacheEntry(Object data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_EXPIRY_MS;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String metric = request.getParameter("metric");
        if (metric == null || metric.isEmpty()) {
            metric = "donationsByMonth";
        }

        // Force refresh bypass if needed
        boolean forceRefresh = "true".equals(request.getParameter("refresh"));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check Cache
        if (!forceRefresh && metricsCache.containsKey(metric)) {
            CacheEntry entry = metricsCache.get(metric);
            if (!entry.isExpired()) {
                System.out.println("⚡ Analytics Cache Hit: " + metric);
                try (PrintWriter out = response.getWriter()) {
                    JSONObject cachedResult = new JSONObject();
                    cachedResult.put("metric", metric);
                    cachedResult.put("data", entry.data);
                    cachedResult.put("cached", true);
                    out.print(cachedResult.toString());
                    return;
                }
            }
        }

        JSONObject result = new JSONObject();
        try (PrintWriter out = response.getWriter()) {
            Firestore db = FirebaseConfig.getFirestore();
            Object data = null;

            if ("donationsByMonth".equalsIgnoreCase(metric)) {
                data = getDonationsByMonth(db);
            } else if ("heatmapDemand".equalsIgnoreCase(metric)) {
                data = getDemandHeatmap(db);
            } else if ("operationalFlux".equalsIgnoreCase(metric)) {
                data = getOperationalFlux(db);
            } else if ("dashboardHome".equalsIgnoreCase(metric)) {
                data = getDashboardHomeStats(db);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("error", "Unknown metric");
                out.print(result.toString());
                return;
            }

            // Update Cache
            metricsCache.put(metric, new CacheEntry(data));

            result.put("metric", metric);
            if ("heatmapDemand".equalsIgnoreCase(metric)) {
                result.put("points", data); // Map expects "points"
            } else if ("dashboardHome".equalsIgnoreCase(metric)) {
                // Return the whole object directly for dashboard home
                out.print(data.toString());
                return;
            } else {
                result.put("data", data);
            }
            
            out.print(result.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("error", "Database error: " + e.getMessage());
            try { response.getWriter().print(result.toString()); } catch (IOException ignored) {}
        }
    }

    private JSONObject getDashboardHomeStats(Firestore db) throws Exception {
        JSONObject stats = new JSONObject();
        
        // 1. Fire count queries in parallel
        com.google.api.core.ApiFuture<com.google.cloud.firestore.AggregateQuerySnapshot> fDonors = db.collection("users").whereEqualTo("role", "DONOR").whereEqualTo("status", "APPROVED").count().get();
        com.google.api.core.ApiFuture<com.google.cloud.firestore.AggregateQuerySnapshot> fBanks = db.collection("blood_banks").whereEqualTo("status", "APPROVED").count().get();
        com.google.api.core.ApiFuture<com.google.cloud.firestore.AggregateQuerySnapshot> fPending = db.collection("users").whereEqualTo("status", "PENDING").count().get();
        com.google.api.core.ApiFuture<com.google.cloud.firestore.AggregateQuerySnapshot> fAlerts = db.collection("emergency_alerts").count().get();

        // 2. Fire list queries in parallel
        com.google.api.core.ApiFuture<QuerySnapshot> fRecentUsers = db.collection("users")
                .orderBy("created_at", com.google.cloud.firestore.Query.Direction.DESCENDING)
                .limit(5).get();
        com.google.api.core.ApiFuture<QuerySnapshot> fP2P = db.collection("peer_requests")
                .orderBy("created_at", com.google.cloud.firestore.Query.Direction.DESCENDING)
                .limit(5).get();

        // 3. Collect Results
        stats.put("totalDonors", fDonors.get().getCount());
        stats.put("totalBanks", fBanks.get().getCount());
        stats.put("pendingApprovals", fPending.get().getCount());
        stats.put("activeAlerts", fAlerts.get().getCount());

        JSONArray usersArr = new JSONArray();
        for (QueryDocumentSnapshot doc : fRecentUsers.get().getDocuments()) {
            JSONObject u = new JSONObject();
            u.put("full_name", doc.getString("full_name"));
            u.put("role", doc.getString("role"));
            u.put("status", doc.getString("status"));
            
            Object createdObj = doc.get("created_at");
            if (createdObj instanceof com.google.cloud.Timestamp) {
                u.put("created_at", ((com.google.cloud.Timestamp) createdObj).toDate().toString());
            } else {
                u.put("created_at", createdObj != null ? createdObj.toString() : "N/A");
            }
            usersArr.put(u);
        }
        stats.put("recentUsers", usersArr);

        JSONArray p2pArr = new JSONArray();
        for (QueryDocumentSnapshot doc : fP2P.get().getDocuments()) {
            JSONObject p = new JSONObject();
            p.put("requester_name", doc.getString("requester_name"));
            p.put("blood_group", doc.getString("blood_group"));
            p.put("hospital_city", doc.getString("hospital_city"));
            p.put("urgency", doc.getString("urgency"));
            p.put("status", doc.getString("status"));
            p.put("created_at", doc.getString("created_at"));
            p2pArr.put(p);
        }
        stats.put("recentP2P", p2pArr);

        return stats;
    }

    private JSONArray getDonationsByMonth(Firestore db) throws InterruptedException, ExecutionException {
        // 🎯 OPTIMIZATION: Only fetch appointments from the last 12 months
        String oneYearAgo = LocalDateTime.now().minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        QuerySnapshot apptsSnapshot = db.collection("appointments")
                // Filter "status" in memory to avoid requiring a composite index in Firestore
                .whereGreaterThanOrEqualTo("appointment_time", oneYearAgo)
                .get().get();
                
        Map<String, Integer> counts = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (QueryDocumentSnapshot doc : apptsSnapshot.getDocuments()) {
            if (!"COMPLETED".equalsIgnoreCase(doc.getString("status"))) continue; // Filter in memory
            
            String bg = doc.getString("blood_group");
            
            // Skip appointments with no valid blood group
            if (bg == null || bg.isEmpty() || "Unknown".equalsIgnoreCase(bg)) continue;
            
            String timeStr = doc.getString("appointment_time");
            if (timeStr == null || timeStr.isEmpty()) continue;
            
            try {
                LocalDateTime dateTime = LocalDateTime.parse(timeStr, formatter);
                String key = dateTime.getYear() + "-" + String.format("%02d", dateTime.getMonthValue()) + "-" + bg;
                counts.put(key, counts.getOrDefault(key, 0) + 1);
            } catch (Exception ignored) {}
        }

        JSONArray arr = new JSONArray();
        counts.forEach((key, count) -> {
            String[] parts = key.split("-");
            JSONObject row = new JSONObject();
            row.put("year", Integer.parseInt(parts[0]));
            row.put("month", Integer.parseInt(parts[1]));
            row.put("bloodGroup", parts[2]);
            row.put("count", count);
            arr.put(row);
        });
        return arr;
    }

    private JSONArray getDemandHeatmap(Firestore db) throws InterruptedException, ExecutionException {
        // Fetch approved banks only
        QuerySnapshot banksSnapshot = db.collection("blood_banks").whereEqualTo("status", "APPROVED").get().get();
        
        Map<String, BankPoint> bankCoords = new HashMap<>();
        for (QueryDocumentSnapshot doc : banksSnapshot.getDocuments()) {
            String name = doc.getString("bank_name");
            Double lat = doc.getDouble("latitude");
            Double lng = doc.getDouble("longitude");
            if (lat != null && lng != null) {
                BankPoint bp = new BankPoint(lat, lng);
                bp.name = name != null ? name : "Unnamed Facility";
                bankCoords.put(doc.getId(), bp);
            }
        }
        
        // Fetch stock
        QuerySnapshot stockSnapshot = db.collection("blood_stock").get().get();
        for (QueryDocumentSnapshot doc : stockSnapshot.getDocuments()) {
            String bankId = doc.getString("blood_bank_id");
            if (bankId != null && bankCoords.containsKey(bankId)) {
                Long units = doc.getLong("units");
                bankCoords.get(bankId).shortage += Math.max(0, 5 - (units != null ? units : 0));
            }
        }
        
        JSONArray arr = new JSONArray();
        for (BankPoint bp : bankCoords.values()) {
            JSONObject point = new JSONObject();
            point.put("name", bp.name);
            point.put("lat", bp.lat);
            point.put("lng", bp.lng);
            point.put("weight", Math.max(0.2, bp.shortage));
            arr.put(point);
        }
        return arr;
    }

    private static class BankPoint {
        String name;
        Double lat, lng;
        double shortage = 0;
        BankPoint(Double lat, Double lng) { this.lat = lat; this.lng = lng; }
    }

    private JSONArray getOperationalFlux(Firestore db) throws InterruptedException, ExecutionException {
        JSONArray arr = new JSONArray();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String sevenDaysAgo = now.minusDays(7).format(dayFormatter);

        Map<String, Integer> fluxData = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            fluxData.put(now.minusDays(i).format(dayFormatter), 0);
        }

        // 🎯 OPTIMIZATION: Only fetch appointments from last 7 days
        QuerySnapshot appts = db.collection("appointments")
                .whereGreaterThanOrEqualTo("appointment_time", sevenDaysAgo)
                .get().get();
        
        for (QueryDocumentSnapshot doc : appts.getDocuments()) {
            String timeStr = doc.getString("appointment_time");
            if (timeStr != null && timeStr.length() >= 10) {
                String day = timeStr.substring(0, 10);
                if (fluxData.containsKey(day)) {
                    fluxData.put(day, fluxData.get(day) + 1);
                }
            }
        }

        // Removed user creation fetch to save Firestore reads

        fluxData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    JSONObject dayObj = new JSONObject();
                    dayObj.put("day", entry.getKey());
                    dayObj.put("volume", entry.getValue());
                    arr.put(dayObj);
                });

        return arr;
    }
}


