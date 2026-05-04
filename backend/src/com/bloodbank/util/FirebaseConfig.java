package com.bloodbank.util;

import com.google.auth.oauth2.GoogleCredentials;
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
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(jsonConfig.trim().getBytes())))
                        .build();
                System.out.println("Firebase: Initializing from environment variable...");
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
