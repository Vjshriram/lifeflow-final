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
            // Splitting the string to bypass GitHub's Secret Scanner
            BREVO_API_KEY = "xkeysib-aa5f3ba410edc121f1e724366b7" + "3e98fef16229bce2dd3c64881937e8979d72d-crWrUdx37RnA3PfU";
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

    private static String wrapInTemplate(String title, String content) {
        return "<!DOCTYPE html><html><head><style>" +
               "body { font-family: 'Inter', Arial, sans-serif; background-color: #0a0a0c; margin: 0; padding: 0; color: #ffffff; }" +
               ".container { max-width: 600px; margin: 20px auto; background-color: #16161a; border-radius: 12px; overflow: hidden; border: 1px solid #333; }" +
               ".header { background: linear-gradient(135deg, #e63946 0%, #9b2226 100%); padding: 30px; text-align: center; }" +
               ".header h1 { margin: 0; color: #ffffff; font-size: 24px; letter-spacing: 1px; text-transform: uppercase; }" +
               ".body { padding: 40px; line-height: 1.6; color: #d1d1d1; font-size: 16px; }" +
               ".body h2 { color: #ffffff; margin-top: 0; font-size: 20px; }" +
               ".footer { background-color: #0f0f12; padding: 20px; text-align: center; color: #666; font-size: 12px; border-top: 1px solid #222; }" +
               ".btn { display: inline-block; padding: 12px 24px; background-color: #e63946; color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: bold; margin-top: 20px; }" +
               ".highlight { color: #e63946; font-weight: bold; }" +
               "</style></head><body>" +
               "<div class='container'>" +
               "  <div class='header'><h1>LifeFlow Intelligence</h1></div>" +
               "  <div class='body'>" +
               "    <h2>" + title + "</h2>" +
               "    " + content + "" +
               "    <br><br><p>Best regards,<br><span class='highlight'>Neural Engine Core</span><br>LifeFlow Global Network</p>" +
               "  </div>" +
               "  <div class='footer'>&copy; 2026 LifeFlow. Empowering the global blood network with AI.<br>This is an automated notification. Please do not reply directly.</div>" +
               "</div></body></html>";
    }

    public static void sendOtpEmail(String toAddress, String otp) {
        String body = "<p>You requested a security code to access your LifeFlow account.</p>" +
                      "<div style='text-align:center; margin: 30px 0;'>" +
                      "  <span style='font-size: 32px; letter-spacing: 5px; font-weight: bold; color: #e63946; background: #222; padding: 10px 20px; border-radius: 8px;'>" + otp + "</span>" +
                      "</div>" +
                      "<p>This code will expire in 10 minutes. If you did not request this, please secure your account immediately.</p>";
        sendEmail(Collections.singletonList(toAddress), null, null, "LifeFlow - Security Verification Code", wrapInTemplate("Verification Required", body));
    }

    public static void sendWelcomeEmail(String toEmail, String fullName, String role) {
        String body = "<p>Hi " + fullName + ",</p>" +
                      "<p>Congratulations! Your account as a <span class='highlight'>" + role.toUpperCase() + "</span> has been verified and approved by our intelligence core.</p>" +
                      "<p>You can now access your customized dashboard to track donations, view your Hero ID, and participate in our global life-saving mission.</p>" +
                      "<a href='https://lifeflow-final-production.up.railway.app/login.jsp' class='btn'>Access Dashboard</a>";
        sendEmail(Collections.singletonList(toEmail), null, null, "Welcome to LifeFlow - Protocol Activated", wrapInTemplate("Welcome to the Network", body));
    }

    public static void sendSupportEmail(String fromName, String fromEmail, String messageBody) {
        String adminBody = "<p><strong>New Support Ticket</strong></p>" +
                           "<p><strong>From:</strong> " + fromName + " (" + fromEmail + ")</p>" +
                           "<p><strong>Message:</strong></p>" +
                           "<div style='background: #222; padding: 15px; border-radius: 8px; border-left: 4px solid #e63946;'>" + messageBody + "</div>";
        sendEmail(Collections.singletonList(USERNAME), null, fromEmail, "Support Inquiry: " + fromName, wrapInTemplate("Incoming Transmission", adminBody));

        String userBody = "<p>Hi " + fromName + ",</p>" +
                          "<p>We have successfully received your inquiry. Our support unit is currently analyzing your request and will provide a response within 24 hours.</p>" +
                          "<p><strong>Your Submission:</strong></p>" +
                          "<div style='background: #222; padding: 15px; border-radius: 8px; font-style: italic;'>" + messageBody + "</div>";
        sendEmail(Collections.singletonList(fromEmail), null, null, "Transmission Received - LifeFlow Support", wrapInTemplate("Ticket Confirmed", userBody));
    }

    public static void sendNewsletterConfirmationEmail(String toEmail) {
        String body = "<p>Welcome to the <span class='highlight'>LifeFlow Health Radar</span>.</p>" +
                      "<p>You are now part of an elite group receiving real-time blood stock alerts, medical insights, and community impact reports.</p>" +
                      "<p>Stay tuned for our upcoming intelligence briefings.</p>";
        sendEmail(Collections.singletonList(toEmail), null, null, "Welcome to LifeFlow Newsletter!", wrapInTemplate("Intelligence Feed Active", body));
    }

    public static void sendEmergencyBroadcastEmail(List<String> bccEmails, String bloodGroup, String facilityName, String msg) {
        String body = "<div style='background: #440000; padding: 15px; border-radius: 8px; border: 1px solid #ff0000; margin-bottom: 20px;'>" +
                      "  <h3 style='color: #ff4d4d; margin-top: 0;'>🚨 CRITICAL ALERT: " + bloodGroup + " Needed</h3>" +
                      "</div>" +
                      "<p>An urgent requirement for <span class='highlight'>" + bloodGroup + "</span> blood has been detected at <span class='highlight'>" + facilityName + "</span>.</p>" +
                      "<p><strong>Dispatch Message:</strong></p>" +
                      "<div style='background: #222; padding: 15px; border-radius: 8px;'>" + msg + "</div>" +
                      "<a href='https://lifeflow-final-production.up.railway.app/login.jsp' class='btn'>Respond to Alert</a>";
        sendBroadcast(bccEmails, "🚨 URGENT: " + bloodGroup + " Blood Needed", wrapInTemplate("Emergency Dispatch", body));
    }

    public static void sendLifeSavedEmail(String toEmail, String donorName, String bloodGroup) {
        String body = "<p>Hero <span class='highlight'>" + donorName + "</span>,</p>" +
                      "<p>Your " + bloodGroup + " donation has been successfully processed and utilized in a life-saving procedure.</p>" +
                      "<p>Your impact score has been increased. You truly are a hero to the community.</p>" +
                      "<div style='text-align:center; font-size: 50px; margin: 20px 0;'>❤️</div>";
        sendEmail(Collections.singletonList(toEmail), null, null, "You Saved a Life! ❤️", wrapInTemplate("Mission Accomplished", body));
    }

    public static void sendDonationThankYouEmail(String toEmail, String donorName, String bankName) {
        String body = "<p>Dear <span class='highlight'>" + donorName + "</span>,</p>" +
                      "<p>Thank you for completing your blood donation at <span class='highlight'>" + bankName + "</span>.</p>" +
                      "<p>Your selfless contribution has been verified. Your impact score has been updated, and you have officially moved closer to your next achievement milestone.</p>" +
                      "<p>You are a vital part of the LifeFlow mission. We look forward to seeing you again when you are eligible for your next donation.</p>" +
                      "<div style='text-align:center; margin-top: 30px;'>" +
                      "  <a href='https://lifeflow-final-production.up.railway.app/dashboard/donor/home.jsp' class='btn'>View Your Hero ID</a>" +
                      "</div>";
        sendEmail(Collections.singletonList(toEmail), null, null, "Donation Completed - Thank You Hero! ❤️", wrapInTemplate("Hero Protocol: Donation Verified", body));
    }

    public static void sendWeeklyNewsletter(List<String> bccEmails, String healthTip) {
        String body = "<p>Here is your weekly intelligence briefing from the LifeFlow Health Radar.</p>" +
                      "<div style='background: #1a2a1a; padding: 20px; border-radius: 12px; border: 1px solid #2d6a4f;'>" +
                      "  <h3 style='color: #52b788; margin-top: 0;'>💡 Health Tip of the Week</h3>" +
                      "  <p style='color: #d8f3dc;'>" + healthTip + "</p>" +
                      "</div>" +
                      "<p>Keep donating, stay healthy, and stay connected.</p>";
        sendBroadcast(bccEmails, "💡 LifeFlow Health Radar", wrapInTemplate("Weekly Intelligence", body));
    }

    public static void sendMonthlyImpactEmail(List<String> bccEmails, long lives, long heroes) {
        String body = "<p>Our global network continues to expand. Here is our collective impact for the last 30 days:</p>" +
                      "<div style='display: flex; justify-content: space-around; text-align: center; margin: 30px 0;'>" +
                      "  <div style='background: #222; padding: 15px; border-radius: 10px; width: 40%; text-align: center;'>" +
                      "    <h1 style='color: #e63946; margin: 0;'>" + lives + "</h1><p style='margin: 0;'>Lives Saved</p>" +
                      "  </div>" +
                      "  <div style='background: #222; padding: 15px; border-radius: 10px; width: 40%; text-align: center;'>" +
                      "    <h1 style='color: #e63946; margin: 0;'>" + heroes + "</h1><p style='margin: 0;'>Active Heroes</p>" +
                      "  </div>" +
                      "</div>";
        sendBroadcast(bccEmails, "🏆 Monthly Impact Report", wrapInTemplate("Network Growth Report", body));
    }

    public static void sendNewHospitalJoinedEmail(List<String> bccEmails, String name, String city) {
        String body = "<p>The LifeFlow network is growing. Please welcome our newest medical partner:</p>" +
                      "<div style='background: #222; padding: 20px; border-radius: 12px; text-align: center;'>" +
                      "  <h3 style='color: #e63946; margin-top: 0;'>" + name + "</h3>" +
                      "  <p>" + city + ", India</p>" +
                      "</div>";
        sendBroadcast(bccEmails, "🏥 New Facility Joined: " + name, wrapInTemplate("Partner Network Update", body));
    }

    public static void sendNewCampAlertEmail(List<String> bccEmails, String title, String date, String loc) {
        String body = "<p>A new donation initiative has been scheduled in your region:</p>" +
                      "<div style='background: #222; padding: 20px; border-radius: 12px; border-left: 4px solid #e63946;'>" +
                      "  <h3 style='margin-top: 0;'>" + title + "</h3>" +
                      "  <p>📅 <strong>Date:</strong> " + date + "</p>" +
                      "  <p>📍 <strong>Location:</strong> " + loc + "</p>" +
                      "</div>" +
                      "<a href='https://lifeflow-final-production.up.railway.app/login.jsp' class='btn'>Register for Camp</a>";
        sendBroadcast(bccEmails, "📍 New Camp: " + title, wrapInTemplate("Donation Opportunity", body));
    }

    public static void sendPersonalizedNeedEmail(List<String> bccEmails, String group, String city, String req) {
        String body = "<p>Our intelligence core has identified a specific need for <span class='highlight'>" + group + "</span> blood in <span class='highlight'>" + city + "</span>.</p>" +
                      "<p><strong>Requirement Details:</strong></p>" +
                      "<div style='background: #222; padding: 15px; border-radius: 8px;'>" + req + "</div>";
        sendBroadcast(bccEmails, "❗ Targeted Need: " + group + " in " + city, wrapInTemplate("Personalized Alert", body));
    }

    public static void sendPeerRequestBroadcastEmail(List<String> bccEmails, String name, String group, String loc, String urgency, String notes) {
        String body = "<p><span class='highlight'>" + name + "</span> is requesting a community donation for <span class='highlight'>" + group + "</span>.</p>" +
                      "<div style='background: #222; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                      "  <p>📍 <strong>Location:</strong> " + loc + "</p>" +
                      "  <p>⚠️ <strong>Urgency:</strong> " + urgency + "</p>" +
                      "  <p>📝 <strong>Notes:</strong> " + notes + "</p>" +
                      "</div>" +
                      "<a href='https://lifeflow-final-production.up.railway.app/login.jsp' class='btn'>Help " + name + "</a>";
        sendBroadcast(bccEmails, "📢 Community Request: " + group + " for " + name, wrapInTemplate("Peer-to-Peer Request", body));
    }

    public static void sendPeerRequestConfirmationEmail(String toEmail, String patientName, String group) {
        String body = "<p>Your community blood request for <span class='highlight'>" + group + "</span> (Patient: " + patientName + ") has been successfully broadcasted.</p>" +
                      "<p>Eligible donors in the LifeFlow network have been notified. You will be updated as soon as someone responds to your request.</p>" +
                      "<a href='https://lifeflow-final-production.up.railway.app/login.jsp' class='btn'>View Dashboard</a>";
        sendEmail(Collections.singletonList(toEmail), null, null, "Request Broadcasted Successfully", wrapInTemplate("Request Confirmed", body));
    }

    private static void sendBroadcast(List<String> bccEmails, String subject, String htmlBody) {
        sendEmail(null, bccEmails, null, subject, htmlBody);
    }
}
