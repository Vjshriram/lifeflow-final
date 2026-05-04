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
        // 🎯 PRIORITY 1: Environment Variables (Secure Production Method)
        USERNAME = System.getenv("GMAIL_USERNAME");
        PASSWORD = System.getenv("GMAIL_PASSWORD");
        FROM_NAME = System.getenv("GMAIL_FROM_NAME");

        // 🎯 PRIORITY 2: Config Properties (Local Fallback)
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
                System.err.println("📧 EmailService: Using default hardcoded credentials.");
            }
        }
        
        if (USERNAME != null) {
            System.out.println("📧 EmailService: Initialized with user: " + USERNAME + " (Source: Environment)");
        } else {
            System.out.println("⚠️ EmailService: WARNING - GMAIL_USERNAME not found in Environment Variables.");
        }
    }

    private static Properties getSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.debug", "true"); // Enable full SMTP logging for debugging
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    public static void sendOtpEmail(String toAddress, String otp) {
        System.out.println("Attempting to send OTP email to: " + toAddress);
        Session session = Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject("LifeFlow - Your Password Reset OTP");
            String htmlBody = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                    + "<h2 style='color: #e11d48;'>LifeFlow Password Reset</h2>"
                    + "<p>You recently requested to reset your password. Use the following OTP to complete the process:</p>"
                    + "<h1 style='background: #f1f5f9; padding: 15px; border-radius: 8px; letter-spacing: 5px; text-align: center; color: #0f172a;'>" + otp + "</h1>"
                    + "</div>";
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmergencyBroadcastEmail(java.util.List<String> bccEmails, String bloodGroup, String facilityName, String emergencyMessage) {
        if (bccEmails == null || bccEmails.isEmpty()) return;
        System.out.println("Attempting to send EMERGENCY email broadcast.");
        Session session = Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            InternetAddress[] bccAddresses = new InternetAddress[bccEmails.size()];
            for (int i = 0; i < bccEmails.size(); i++) {
                bccAddresses[i] = new InternetAddress(bccEmails.get(i));
            }
            message.setRecipients(Message.RecipientType.BCC, bccAddresses);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));
            message.setSubject("🚨 URGENT: " + bloodGroup + " Blood Needed Immediately at " + facilityName);
            String htmlBody = "<h1>CRITICAL DEMAND</h1><p>Emergency at " + facilityName + ". Need " + bloodGroup + " blood.</p>";
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSupportEmail(String fromName, String fromEmail, String messageBody) {
        System.out.println("Attempting to send Support Inquiry.");
        Session session = Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, "LifeFlow Support Bot"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));
            message.setReplyTo(new Address[]{new InternetAddress(fromEmail)});
            message.setSubject("LifeFlow Support Inquiry from " + fromName);
            String htmlBody = "<h2>New Support Request</h2><p>From: " + fromName + " (" + fromEmail + ")</p><p>" + messageBody + "</p>";
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendNewsletterConfirmationEmail(String toEmail) {
        System.out.println("Sending Newsletter Confirmation.");
        Session session = Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("\uD83D\uDCE2 Welcome to the LifeFlow Network!");
            String htmlBody = "<h1>WELCOME, HERO!</h1><p>You have successfully subscribed to the LifeFlow Newsletter.</p>";
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendWelcomeEmail(String toEmail, String fullName, String role) {
        System.out.println("Sending Welcome email.");
        Session session = Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to LifeFlow - Account Approved");
            String htmlBody = "<h2>Great news!</h2><p>Dear " + fullName + ", your account is approved.</p>";
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void sendBroadcast(java.util.List<String> bccEmails, String subject, String htmlBody) {
        Session session = Session.getInstance(getSmtpProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, FROM_NAME));
            InternetAddress[] bccAddresses = new InternetAddress[bccEmails.size()];
            for (int i = 0; i < bccEmails.size(); i++) {
                bccAddresses[i] = new InternetAddress(bccEmails.get(i));
            }
            message.setRecipients(Message.RecipientType.BCC, bccAddresses);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(USERNAME));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html");
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
