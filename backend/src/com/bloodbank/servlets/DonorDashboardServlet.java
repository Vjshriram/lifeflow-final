package com.bloodbank.servlets;

import com.bloodbank.util.FirebaseConfig;
import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "DonorDashboardServlet", urlPatterns = {"/api/donor-dashboard"})
public class DonorDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("userId");
        String myBloodGroup = (String) session.getAttribute("bloodGroup");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JSONObject result = new JSONObject();

        try {
            Firestore db = FirebaseConfig.getFirestore();

            // 1. Fetch Active Emergency Alerts for this blood group
            ApiFuture<QuerySnapshot> alertFuture = null;
            if (myBloodGroup != null && !myBloodGroup.isEmpty() && !"Unknown".equals(myBloodGroup)) {
                alertFuture = db.collection("emergency_alerts")
                        .whereEqualTo("blood_group", myBloodGroup)
                        .whereEqualTo("status", "ACTIVE").get();
            }

            // 2. Fetch Appointments for this donor
            ApiFuture<QuerySnapshot> apptHistFuture = db.collection("appointments")
                    .whereEqualTo("donor_id", userId).get();

            // 3. Fetch Latest Community Blood Requests
            ApiFuture<QuerySnapshot> p2pFuture = db.collection("peer_requests")
                    .orderBy("created_at", Query.Direction.DESCENDING)
                    .limit(5).get();

            // Wait for alerts if applicable
            JSONArray alertsArr = new JSONArray();
            Set<String> bankIdsToFetch = new HashSet<>();
            if (alertFuture != null) {
                for (QueryDocumentSnapshot doc : alertFuture.get().getDocuments()) {
                    JSONObject alert = new JSONObject();
                    alert.put("id", doc.getId());
                    alert.put("bank_id", doc.getString("bank_id"));
                    alert.put("message", doc.getString("message"));
                    alert.put("radius_km", doc.getDouble("radius_km"));
                    alert.put("created_at", doc.getString("created_at"));
                    alertsArr.put(alert);
                    if (doc.getString("bank_id") != null) bankIdsToFetch.add(doc.getString("bank_id"));
                }
            }
            result.put("alerts", alertsArr);

            // Wait for appointments
            JSONArray apptsArr = new JSONArray();
            for (QueryDocumentSnapshot doc : apptHistFuture.get().getDocuments()) {
                JSONObject appt = new JSONObject();
                appt.put("id", doc.getId());
                appt.put("status", doc.getString("status"));
                appt.put("appointment_time", doc.getString("appointment_time"));
                appt.put("bank_id", doc.getString("bank_id"));
                apptsArr.put(appt);
                if (doc.getString("bank_id") != null) bankIdsToFetch.add(doc.getString("bank_id"));
            }
            result.put("appointments", apptsArr);

            // Wait for P2P requests
            JSONArray p2pArr = new JSONArray();
            for (QueryDocumentSnapshot doc : p2pFuture.get().getDocuments()) {
                JSONObject p = new JSONObject();
                p.put("id", doc.getId());
                p.put("requester_name", doc.getString("requester_name"));
                p.put("blood_group", doc.getString("blood_group"));
                p.put("hospital_city", doc.getString("hospital_city"));
                p.put("urgency", doc.getString("urgency"));
                p.put("status", doc.getString("status"));
                p.put("created_at", doc.getString("created_at"));
                p.put("donor_id", doc.getString("donor_id")); // To check if it's "mine"
                p.put("bank_id", doc.getString("bank_id"));
                p2pArr.put(p);
            }
            result.put("communityRequests", p2pArr);

            // 4. Batch fetch bank names
            Map<String, String> bankNamesMap = new HashMap<>();
            if (!bankIdsToFetch.isEmpty()) {
                List<DocumentReference> refs = new ArrayList<>();
                for (String id : bankIdsToFetch) refs.add(db.collection("blood_banks").document(id));
                
                List<DocumentSnapshot> bankDocs = db.getAll(refs.toArray(new DocumentReference[0])).get();
                for (DocumentSnapshot bDoc : bankDocs) {
                    if (bDoc.exists()) {
                        String name = bDoc.getString("bank_name");
                        if (name == null) name = bDoc.getString("full_name");
                        bankNamesMap.put(bDoc.getId(), name != null ? name : "Unknown Facility");
                    }
                }
            }
            result.put("bankNames", bankNamesMap);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("error", e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(result.toString());
        }
    }
}
