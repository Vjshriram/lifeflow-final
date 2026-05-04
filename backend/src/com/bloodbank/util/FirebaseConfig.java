package com.bloodbank.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {

    private static Firestore firestore;

    private static synchronized void initialize() {
        if (firestore != null) return;

        try {
            FirebaseOptions options = null;
            String jsonConfig = System.getenv("FIREBASE_CONFIG");

            if (jsonConfig != null && !jsonConfig.trim().isEmpty()) {
                String config = jsonConfig.trim();
                
                // --- ULTIMATE STABILITY: BASE64 DECODER ---
                // We first try to decode as Base64. This is the only way to be 100% safe 
                // from Railway's character escaping issues.
                byte[] jsonBytes;
                try {
                    jsonBytes = java.util.Base64.getDecoder().decode(config);
                    System.out.println("✅ Firebase: Decoded Base64 configuration.");
                } catch (Exception e) {
                    // If not Base64, fallback to raw bytes (but this is where escapes usually fail)
                    jsonBytes = config.getBytes();
                    System.out.println("⚠️ Firebase: Using raw JSON (Base64 fallback).");
                }
                
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(jsonBytes)))
                        .build();
                
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
                firestore = FirestoreClient.getFirestore();
                System.out.println("✅ Firebase initialized successfully!");
            } else {
                System.err.println("Firebase: FIREBASE_CONFIG variable is MISSING!");
            }
        } catch (Exception e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) {
            initialize();
        }
        return firestore;
    }
}
