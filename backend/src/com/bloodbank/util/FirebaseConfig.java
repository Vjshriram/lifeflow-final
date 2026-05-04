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
                // Manual extraction to avoid JSON parser escape issues
                String config = jsonConfig.trim();
                
                // Extract private_key manually to clean it
                String privateKey = "";
                if (config.contains("\"private_key\": \"")) {
                    int start = config.indexOf("\"private_key\": \"") + 16;
                    int end = config.indexOf("\"", start);
                    privateKey = config.substring(start, end)
                            .replace("\\n", "\n")
                            .replace("\\\\n", "\n");
                }
                
                String clientEmail = "";
                if (config.contains("\"client_email\": \"")) {
                    int start = config.indexOf("\"client_email\": \"") + 17;
                    int end = config.indexOf("\"", start);
                    clientEmail = config.substring(start, end);
                }
                
                String projectId = "";
                if (config.contains("\"project_id\": \"")) {
                    int start = config.indexOf("\"project_id\": \"") + 15;
                    int end = config.indexOf("\"", start);
                    projectId = config.substring(start, end);
                }

                if (!privateKey.isEmpty() && !clientEmail.isEmpty()) {
                    options = FirebaseOptions.builder()
                            .setCredentials(com.google.auth.oauth2.ServiceAccountCredentials.fromPkcs8(
                                    null, // keyId (optional)
                                    clientEmail,
                                    privateKey,
                                    null, // privateKeyId (optional)
                                    null  // scopes (optional)
                            ))
                            .setProjectId(projectId)
                            .build();
                    System.out.println("Firebase: Manual initialization successful for: " + clientEmail);
                }
            } else {
                System.err.println("Firebase: FIREBASE_CONFIG variable is MISSING!");
            }

            if (options != null) {
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
                firestore = FirestoreClient.getFirestore();
                System.out.println("✅ Firebase initialized successfully.");
            }
        } catch (Exception e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
        }
    }

    public static Firestore getFirestore() {
        if (firestore == null) {
            initialize();
        }
        return firestore;
    }
}
