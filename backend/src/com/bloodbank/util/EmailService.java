package com.bloodbank.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmailService {

    private static String BREVO_API_KEY;
    private static String USERNAME = "lifeflowad@gmail.com";
    private static String FROM_NAME = "LifeFlow Emergency Center";

    static {
        BREVO_API_KEY = System.getenv("BREVO_API_KEY");
        if (BREVO_API_KEY == null) {
            System.err.println("⚠️ CRITICAL: BREVO_API_KEY is missing from environment variables!");
        }
        
        String envUser = System.getenv("GMAIL_USERNAME");
        if (envUser != null) USERNAME = envUser;
        
        String envName = System.getenv("GMAIL_FROM_NAME");
        if (envName != null) FROM_NAME = envName;

        System.out.println("📧 EmailService: Initialized with Brevo HTTP API");
    }

    private static void sendEmail(List<String> toEmails, List<String> bccEmails, String replyTo, String subject, String htmlBody) {
        try {
            URL url = new URL("https://api.brevo.com/v3/smtp/email");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("api-key", BREVO_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            
            JSONObject sender = new JSONObject();
            sender.put("name", FROM_NAME);
            sender.put("email", USERNAME);
            payload.put("sender", sender);

            if (toEmails != null && !toEmails.isEmpty()) {
                JSONArray to = new JSONArray();
                for (String email : toEmails) {
                    JSONObject recipient = new JSONObject();
                    recipient.put("email", email);
                    to.put(recipient);
                }
                payload.put("to", to);
            } else if (bccEmails != null && !bccEmails.isEmpty()) {
                // Brevo requires at least one 'to' address
                JSONArray to = new JSONArray();
                JSONObject recipient = new JSONObject();
                recipient.put("email", USERNAME);
                to.put(recipient);
                payload.put("to", to);
            }

            if (bccEmails != null && !bccEmails.isEmpty()) {
                JSONArray bcc = new JSONArray();
                for (String email : bccEmails) {
                    JSONObject recipient = new JSONObject();
                    recipient.put("email", email);
                    bcc.put(recipient);
                }
                payload.put("bcc", bcc);
            }

            if (replyTo != null && !replyTo.isEmpty()) {
                JSONObject replyToObj = new JSONObject();
                replyToObj.put("email", replyTo);
                payload.put("replyTo", replyToObj);
            }

            payload.put("subject", subject);
            payload.put("htmlContent", htmlBody);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📧 Brevo API Response: " + responseCode);
            if (responseCode >= 400) {
                java.io.InputStream is = conn.getErrorStream();
                if (is != null) {
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    System.out.println("❌ Brevo Error: " + (s.hasNext() ? s.next() : ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendOtpEmail(String toAddress, String otp) {
        sendEmail(Collections.singletonList(toAddress), null, null, "LifeFlow - Your Password Reset OTP", "<h2>OTP: " + otp + "</h2>");
    }

    public static void sendWelcomeEmail(String toEmail, String fullName, String role) {
        sendEmail(Collections.singletonList(toEmail), null, null, "Welcome to LifeFlow - Account Approved", "<h2>Dear " + fullName + ", your account is approved.</h2>");
    }

    public static void sendSupportEmail(String fromName, String fromEmail, String messageBody) {
        sendEmail(Collections.singletonList(USERNAME), null, fromEmail, "Support Inquiry from " + fromName, "<p><strong>From:</strong> " + fromName + " (" + fromEmail + ")</p><p><strong>Message:</strong></p><p>" + messageBody + "</p>");
        sendEmail(Collections.singletonList(fromEmail), null, null, "We received your message, " + fromName + "!", "<h3>Hi " + fromName + ",</h3><p>Thank you for reaching out to LifeFlow. We have received your message and our support team will get back to you shortly.</p><br><p><strong>Your Message:</strong><br>" + messageBody + "</p><br><p>Best,<br>LifeFlow Team</p>");
    }

    public static void sendNewsletterConfirmationEmail(String toEmail) {
        sendEmail(Collections.singletonList(toEmail), null, null, "Welcome to LifeFlow Newsletter!", "<h1>Welcome Hero!</h1>");
    }

    public static void sendEmergencyBroadcastEmail(List<String> bccEmails, String bloodGroup, String facilityName, String msg) {
        sendBroadcast(bccEmails, "🚨 URGENT: " + bloodGroup + " Blood Needed", "<h1>Critical Need at " + facilityName + "</h1><p>" + msg + "</p>");
    }

    public static void sendLifeSavedEmail(String toEmail, String donorName, String bloodGroup) {
        sendEmail(Collections.singletonList(toEmail), null, null, "You Saved a Life! ❤️", "<h1>Hero " + donorName + "!</h1><p>Your " + bloodGroup + " donation was used.</p>");
    }

    public static void sendWeeklyNewsletter(List<String> bccEmails, String healthTip) {
        sendBroadcast(bccEmails, "💡 LifeFlow Health Radar", "<h3>Health Tip:</h3><p>" + healthTip + "</p>");
    }

    public static void sendMonthlyImpactEmail(List<String> bccEmails, long lives, long heroes) {
        sendBroadcast(bccEmails, "🏆 Monthly Impact Report", "<h1>" + lives + " Lives Saved!</h1>");
    }

    public static void sendNewHospitalJoinedEmail(List<String> bccEmails, String name, String city) {
        sendBroadcast(bccEmails, "🏥 New Facility Joined: " + name, "<h1>Welcome " + name + "!</h1>");
    }

    public static void sendNewCampAlertEmail(List<String> bccEmails, String title, String date, String loc) {
        sendBroadcast(bccEmails, "📍 New Camp: " + title, "<h1>" + title + "</h1><p>Date: " + date + "</p>");
    }

    public static void sendPersonalizedNeedEmail(List<String> bccEmails, String group, String city, String req) {
        sendBroadcast(bccEmails, "❗ Need: " + group + " in " + city, "<h1>Need: " + group + "</h1>");
    }

    public static void sendPeerRequestBroadcastEmail(List<String> bccEmails, String name, String group, String loc, String urgency, String notes) {
        sendBroadcast(bccEmails, "📢 Request: " + group + " for " + name, "<h1>Request from " + name + "</h1>");
    }

    private static void sendBroadcast(List<String> bccEmails, String subject, String htmlBody) {
        sendEmail(null, bccEmails, null, subject, htmlBody);
    }
}
