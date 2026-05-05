package com.bloodbank.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static String USERNAME; 
    private static String PASSWORD;
    private static String FROM_NAME;

    static {
        USERNAME = System.getenv("GMAIL_USERNAME");
        PASSWORD = System.getenv("GMAIL_PASSWORD");
        FROM_NAME = System.getenv("GMAIL_FROM_NAME");

        if (USERNAME == null || PASSWORD == null) {
            try {
                Properties props = new Properties();
                java.io.InputStream propStream = EmailService.class.getClassLoader().getResourceAsStream("config.properties");
                if (propStream != null) {
                    props.load(propStream);
                }
                if (USERNAME == null) USERNAME = props.getProperty("gmail.username", "lifeflowad@gmail.com");
                if (PASSWORD == null) PASSWORD = props.getProperty("gmail.password", "jelmkdzvswkpszpt");
                if (FROM_NAME == null) FROM_NAME = props.getProperty("gmail.from.name", "LifeFlow Emergency Center");
            } catch (java.io.IOException e) {
                System.err.println("📧 EmailService: Using fallback credentials.");
            }
        }
        System.out.println("📧 EmailService: Initialized with " + USERNAME);
        if (PASSWORD != null) {
            System.out.println("📧 EmailService: Password loaded (Length: " + PASSWORD.length() + ")");
        } else {
            System.out.println("⚠️ EmailService: PASSWORD IS NULL!");
        }
    }

    private static Properties getSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", USERNAME);
        props.put("mail.smtp.connectiontimeout", "15000"); // 15 seconds
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.debug", "true");
        return props;
    }

    private static Session getSession() {
        return Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    public static void sendOtpEmail(String toAddress, String otp) {
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject("LifeFlow - Your Password Reset OTP");
            message.setContent("<h2>OTP: " + otp + "</h2>", "text/html");
            Transport.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void sendWelcomeEmail(String toEmail, String fullName, String role) {
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to LifeFlow - Account Approved");
            message.setContent("<h2>Dear " + fullName + ", your account is approved.</h2>", "text/html");
            Transport.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void sendSupportEmail(String fromName, String fromEmail, String messageBody) {
        try {
            // 1. Send inquiry to Admin
            Message adminMessage = new MimeMessage(getSession());
            adminMessage.setFrom(new InternetAddress(USERNAME, "LifeFlow Support System"));
            adminMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));
            adminMessage.setReplyTo(new InternetAddress[]{new InternetAddress(fromEmail)});
            adminMessage.setSubject("Support Inquiry from " + fromName);
            adminMessage.setContent("<p><strong>From:</strong> " + fromName + " (" + fromEmail + ")</p><p><strong>Message:</strong></p><p>" + messageBody + "</p>", "text/html");
            Transport.send(adminMessage);

            // 2. Send auto-reply confirmation to the User
            Message userMessage = new MimeMessage(getSession());
            userMessage.setFrom(new InternetAddress(USERNAME, "LifeFlow Support"));
            userMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(fromEmail));
            userMessage.setSubject("We received your message, " + fromName + "!");
            userMessage.setContent("<h3>Hi " + fromName + ",</h3><p>Thank you for reaching out to LifeFlow. We have received your message and our support team will get back to you shortly.</p><br><p><strong>Your Message:</strong><br>" + messageBody + "</p><br><p>Best,<br>LifeFlow Team</p>", "text/html");
            Transport.send(userMessage);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void sendNewsletterConfirmationEmail(String toEmail) {
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to LifeFlow Newsletter!");
            message.setContent("<h1>Welcome Hero!</h1>", "text/html");
            Transport.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void sendEmergencyBroadcastEmail(java.util.List<String> bccEmails, String bloodGroup, String facilityName, String msg) {
        sendBroadcast(bccEmails, "🚨 URGENT: " + bloodGroup + " Blood Needed", "<h1>Critical Need at " + facilityName + "</h1><p>" + msg + "</p>");
    }

    public static void sendLifeSavedEmail(String toEmail, String donorName, String bloodGroup) {
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("You Saved a Life! ❤️");
            message.setContent("<h1>Hero " + donorName + "!</h1><p>Your " + bloodGroup + " donation was used.</p>", "text/html");
            Transport.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void sendWeeklyNewsletter(java.util.List<String> bccEmails, String healthTip) {
        sendBroadcast(bccEmails, "💡 LifeFlow Health Radar", "<h3>Health Tip:</h3><p>" + healthTip + "</p>");
    }

    public static void sendMonthlyImpactEmail(java.util.List<String> bccEmails, long lives, long heroes) {
        sendBroadcast(bccEmails, "🏆 Monthly Impact Report", "<h1>" + lives + " Lives Saved!</h1>");
    }

    public static void sendNewHospitalJoinedEmail(java.util.List<String> bccEmails, String name, String city) {
        sendBroadcast(bccEmails, "🏥 New Facility Joined: " + name, "<h1>Welcome " + name + "!</h1>");
    }

    public static void sendNewCampAlertEmail(java.util.List<String> bccEmails, String title, String date, String loc) {
        sendBroadcast(bccEmails, "📍 New Camp: " + title, "<h1>" + title + "</h1><p>Date: " + date + "</p>");
    }

    public static void sendPersonalizedNeedEmail(java.util.List<String> bccEmails, String group, String city, String req) {
        sendBroadcast(bccEmails, "❗ Need: " + group + " in " + city, "<h1>Need: " + group + "</h1>");
    }

    public static void sendPeerRequestBroadcastEmail(java.util.List<String> bccEmails, String name, String group, String loc, String urgency, String notes) {
        sendBroadcast(bccEmails, "📢 Request: " + group + " for " + name, "<h1>Request from " + name + "</h1>");
    }

    private static void sendBroadcast(java.util.List<String> bccEmails, String subject, String htmlBody) {
        if (bccEmails == null || bccEmails.isEmpty()) return;
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            InternetAddress[] bcc = new InternetAddress[bccEmails.size()];
            for (int i = 0; i < bccEmails.size(); i++) bcc[i] = new InternetAddress(bccEmails.get(i));
            message.setRecipients(Message.RecipientType.BCC, bcc);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
