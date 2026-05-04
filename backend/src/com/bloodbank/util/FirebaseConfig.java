package com.bloodbank.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

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
                byte[] jsonBytes;
                try {
                    // Try decoding as Base64 first (Railway-Safe)
                    jsonBytes = java.util.Base64.getDecoder().decode(config);
                    System.out.println("✅ Firebase: Decoded Base64 config.");
                } catch (Exception e) {
                    // Fallback to raw bytes
                    jsonBytes = config.getBytes();
                    System.out.println("⚠️ Firebase: Using raw JSON.");
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
