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
                
                // Use Google's own JSON parser (already in your dependencies)
                com.google.api.client.json.JsonFactory jsonFactory = com.google.api.client.json.gson.GsonFactory.getDefaultInstance();
                com.google.api.client.json.JsonParser parser = jsonFactory.createJsonParser(config);
                
                // Load it directly as a Map
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map = parser.parse(map.getClass());
                
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(config.getBytes())))
                        .setProjectId((String) map.get("project_id"))
                        .build();
                
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
                firestore = FirestoreClient.getFirestore();
                System.out.println("✅ Firebase initialized successfully using Official Parser.");
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
