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

// Ultimate Scrambler Build - Triggered: 2026-05-04 20:13
public class FirebaseConfig {

    private static Firestore firestore;

    private static synchronized void initialize() {
        if (firestore != null) return;

        try {
            System.out.println("🔍 Firebase: Looking for environment variable 'FIREBASE_KEY_JSON'...");
            
            String jsonContent = System.getenv("FIREBASE_KEY_JSON");
            
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                throw new IllegalStateException("Environment variable 'FIREBASE_KEY_JSON' is missing or empty!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(jsonContent.getBytes())))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("🚀 Firebase: Connection established via Environment Variable!");
        } catch (Exception e) {
            System.err.println("❌ Firebase: Environment Variable Initialization FAILED: " + e.getMessage());
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
