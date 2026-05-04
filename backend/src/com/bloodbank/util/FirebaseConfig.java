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
            // Loading from local file to guarantee zero character corruption
            java.io.InputStream serviceAccount = FirebaseConfig.class.getClassLoader().getResourceAsStream("firebase-key.json");
            
            if (serviceAccount == null) {
                // Fallback for some container configurations
                serviceAccount = new java.io.FileInputStream(new java.io.File("/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/firebase-key.json"));
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("✅ Firebase initialized successfully from local file.");
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
