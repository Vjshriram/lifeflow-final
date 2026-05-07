package com.bloodbank.servlets;

import com.bloodbank.util.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/bank-dashboard")
public class BankDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject result = new JSONObject();
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"BANK".equalsIgnoreCase(role)) {
            result.put("error", "Unauthorized access");
            response.getWriter().print(result.toString());
            return;
        }

        try {
            Firestore db = FirebaseConfig.getFirestore();
            
            // 1. Get Bank ID from User Email
            DocumentSnapshot userDoc = db.collection("users").document(userId).get().get();
            String bankId = null;
            if (userDoc.exists()) {
                String email = userDoc.getString("email");
                QuerySnapshot bankQs = db.collection("blood_banks").whereEqualTo("email", email).limit(1).get().get();
                if (!bankQs.isEmpty()) {
                    bankId = bankQs.getDocuments().get(0).getId();
                }
            }

            if (bankId == null) {
                result.put("error", "Bank profile not linked");
                response.getWriter().print(result.toString());
                return;
            }

            result.put("bankId", bankId);

            // 2. Parallel Fetching
            ApiFuture<QuerySnapshot> fAlerts = db.collection("emergency_alerts").whereEqualTo("bank_id", bankId).get();
            ApiFuture<QuerySnapshot> fStock = db.collection("blood_stock").whereEqualTo("blood_bank_id", bankId).get();
            ApiFuture<QuerySnapshot> fAppts = db.collection("appointments").whereEqualTo("bank_id", bankId).get();

            // --- PROCESS ALERTS ---
            JSONArray alertsArr = new JSONArray();
            List<QueryDocumentSnapshot> alerts = new ArrayList<>(fAlerts.get().getDocuments());
            alerts.sort((a, b) -> (b.getString("created_at") != null ? b.getString("created_at") : "")
                    .compareTo(a.getString("created_at") != null ? a.getString("created_at") : ""));
            
            for (QueryDocumentSnapshot doc : alerts) {
                JSONObject obj = new JSONObject();
                obj.put("blood_group", doc.getString("blood_group"));
                obj.put("radius_km", doc.getDouble("radius_km"));
                obj.put("message", doc.getString("message"));
                obj.put("created_at", doc.getString("created_at"));
                alertsArr.put(obj);
            }
            result.put("alerts", alertsArr);

            // --- PROCESS STOCK ---
            JSONObject stockObj = new JSONObject();
            for (QueryDocumentSnapshot doc : fStock.get().getDocuments()) {
                stockObj.put(doc.getString("blood_group"), doc.getLong("units"));
            }
            result.put("inventory", stockObj);

            // --- PROCESS APPOINTMENTS ---
            JSONArray apptsArr = new JSONArray();
            List<QueryDocumentSnapshot> apptDocs = new ArrayList<>(fAppts.get().getDocuments());
            apptDocs.sort((a, b) -> (b.getString("appointment_time") != null ? b.getString("appointment_time") : "")
                    .compareTo(a.getString("appointment_time") != null ? a.getString("appointment_time") : ""));

            // Collecting donor IDs for batch fetch
            Set<String> donorIds = new HashSet<>();
            for (QueryDocumentSnapshot doc : apptDocs) {
                String dId = doc.getString("donor_id");
                if (dId != null) donorIds.add(dId);
            }

            Map<String, JSONObject> donorMap = new HashMap<>();
            if (!donorIds.isEmpty()) {
                List<DocumentReference> refs = new ArrayList<>();
                for (String id : donorIds) refs.add(db.collection("users").document(id));
                List<DocumentSnapshot> donorDocs = db.getAll(refs.toArray(new DocumentReference[0])).get();
                for (DocumentSnapshot dDoc : donorDocs) {
                    if (dDoc.exists()) {
                        JSONObject dObj = new JSONObject();
                        dObj.put("name", dDoc.getString("full_name"));
                        dObj.put("phone", dDoc.getString("phone"));
                        dObj.put("blood_group", dDoc.getString("blood_group"));
                        donorMap.put(dDoc.getId(), dObj);
                    }
                }
            }

            for (QueryDocumentSnapshot doc : apptDocs) {
                JSONObject obj = new JSONObject();
                String dId = doc.getString("donor_id");
                obj.put("id", doc.getId());
                obj.put("status", doc.getString("status"));
                obj.put("appointment_time", doc.getString("appointment_time"));
                
                JSONObject donorInfo = donorMap.getOrDefault(dId, new JSONObject());
                obj.put("donorName", donorInfo.optString("name", "Unknown"));
                obj.put("donorPhone", donorInfo.optString("phone", "N/A"));
                obj.put("donorBloodGroup", donorInfo.optString("blood_group", "N/A"));
                
                apptsArr.put(obj);
            }
            result.put("appointments", apptsArr);

            response.getWriter().print(result.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("error", "Database failure: " + e.getMessage());
            response.getWriter().print(result.toString());
        }
    }
}
