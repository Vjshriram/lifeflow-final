package com.bloodbank.servlets;

import com.bloodbank.util.FirebaseConfig;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "LeaderboardServlet", urlPatterns = {"/api/leaderboard"})
public class LeaderboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🚀 Leaderboard: GET Request received at /api/leaderboard");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Firestore db = FirebaseConfig.getFirestore();

            System.out.println("🔍 Leaderboard: Querying users (Mega-Safe Mode)...");
            // 🎯 MEGA-FIX: Query all users and filter role in Java to handle case-sensitivity (DONOR vs donor)
            Query query = db.collection("users").limit(200);

            QuerySnapshot querySnapshot = query.get().get();
            System.out.println("📊 Leaderboard: Analyzing " + querySnapshot.size() + " total records.");
            
            List<JSONObject> donorList = new ArrayList<>();

            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                String userRole = document.getString("role");
                
                // Only process Donors
                if (userRole == null || !userRole.equalsIgnoreCase("DONOR")) {
                    continue; 
                }

                Long countObj = document.getLong("donation_count");
                long count = (countObj != null) ? countObj : 0;
                
                String name = document.getString("full_name");
                System.out.println("👤 Leaderboard: Processing " + name + " (Donations: " + count + ")");
                
                JSONObject donor = new JSONObject();
                donor.put("id", document.getId());
                donor.put("name", name != null ? name : "Anonymous Donor");
                donor.put("count", count);
                
                // Add badge logic based on thresholds
                if (count >= 20) {
                    donor.put("badge", "Life Saver");
                    donor.put("badgeIcon", "fa-crown");
                } else if (count >= 10) {
                    donor.put("badge", "Top Donor");
                    donor.put("badgeIcon", "fa-award");
                } else if (count >= 5) {
                    donor.put("badge", "Regular Donor");
                    donor.put("badgeIcon", "fa-star");
                }

                donorList.add(donor);
            }

            // 🏆 Sort the list by count (Descending) in Java
            donorList.sort((a, b) -> Long.compare(b.getLong("count"), a.getLong("count")));

            JSONArray leaderboardArr = new JSONArray();
            for (JSONObject d : donorList) {
                leaderboardArr.put(d);
            }

            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("leaderboard", leaderboardArr);
            out.print(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                JSONObject error = new JSONObject();
                error.put("success", false);
                error.put("message", "Error fetching leaderboard: " + e.getMessage());
                response.getWriter().print(error.toString());
            } catch (IOException ignored) {}
        }
    }
}
