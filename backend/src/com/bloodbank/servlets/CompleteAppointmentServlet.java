package com.bloodbank.servlets;

import com.bloodbank.util.FirebaseConfig;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/CompleteAppointmentServlet")
public class CompleteAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔐 Ensure only BANK can do this
        HttpSession session = request.getSession(false);
        if (session == null || !"BANK".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String appointmentId = request.getParameter("appointmentId");
        if (appointmentId == null || appointmentId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String bankUserId = (String) session.getAttribute("userId");

        try {
            Firestore db = FirebaseConfig.getFirestore();

            // ✅ Step 1: Get correct bank user email from users table
            DocumentSnapshot userDoc = db.collection("users").document(bankUserId).get().get();
            if (!userDoc.exists()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            String email = userDoc.getString("email");

            // ✅ Step 2: Get correct bank_id from blood_banks table
            QuerySnapshot bankSnapshot = db.collection("blood_banks")
                    .whereEqualTo("email", email)
                    .get().get();

            if (bankSnapshot.isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            String bankId = bankSnapshot.getDocuments().get(0).getId();

            // ✅ Step 3: Check and Update that bank's appointment
            DocumentSnapshot apptDoc = db.collection("appointments").document(appointmentId).get().get();
            if (apptDoc.exists() && bankId.equals(apptDoc.getString("bank_id"))) {
                // Update appointment status
                db.collection("appointments").document(appointmentId).update("status", "COMPLETED").get();
                
                // 🏆 Leaderboard Integration: Increment donor's donation count
                String donorId = apptDoc.getString("donor_id");
                if (donorId != null && !donorId.isEmpty()) {
                    try {
                        db.collection("users").document(donorId).update("donation_count", com.google.cloud.firestore.FieldValue.increment(1)).get();
                    } catch (Exception ignored) {}
                    
                    // 🤖 AUTOMATION: Trigger Milestones and Stock Guards
                    String bloodGroup = apptDoc.getString("blood_group");
                    com.bloodbank.util.AutomationService.processDonationImpact(donorId, bankId, bloodGroup);
                    
                    try {
                        String alertId = apptDoc.getString("alert_id");
                        if (alertId != null && !alertId.isEmpty()) {
                            db.collection("emergency_alerts").document(alertId).update("status", "RESOLVED").get();
                        }
                    } catch (Exception ignored) {}
                    
                    try {
                        String peerRequestId = apptDoc.getString("peer_request_id");
                        if (peerRequestId != null && !peerRequestId.isEmpty()) {
                            db.collection("peer_requests").document(peerRequestId).update("status", "COMPLETED").get();
                            
                            // Send email to the donor who raised the request
                            DocumentSnapshot p2pDoc = db.collection("peer_requests").document(peerRequestId).get().get();
                            if(p2pDoc.exists()) {
                                String requesterId = p2pDoc.getString("donor_id");
                                if(requesterId != null) {
                                    DocumentSnapshot reqDoc = db.collection("users").document(requesterId).get().get();
                                    if(reqDoc.exists() && reqDoc.getString("email") != null) {
                                        String msg = "Your blood request for " + p2pDoc.getString("requester_name") + " has been successfully fulfilled by a donor! Thank you for using LifeFlow.";
                                        com.bloodbank.util.EmailService.sendWeeklyNewsletter(java.util.Collections.singletonList(reqDoc.getString("email")), msg);
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                    
                    try {
                        // Send email to the donor who finished the donation
                        DocumentSnapshot dDoc = db.collection("users").document(donorId).get().get();
                        if(dDoc.exists() && dDoc.getString("email") != null) {
                            String msg = "Thank you for completing your blood donation! Your selfless act has saved a life today. You are a true hero.";
                            com.bloodbank.util.EmailService.sendWeeklyNewsletter(java.util.Collections.singletonList(dDoc.getString("email")), msg);
                        }
                    } catch (Exception ignored) {}
                }
            }

        } catch (Exception e) {
            throw new ServletException("Failed to complete appointment", e);
        }

        // 🔁 Back to bank dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard/bank/home.jsp");
    }
}
