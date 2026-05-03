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

    static {
        try {
            FirebaseOptions options;
            String jsonConfig = System.getenv("FIREBASE_CONFIG");

            if (jsonConfig != null && !jsonConfig.isEmpty()) {
                // Initialize from environment variable (Secure Production Method)
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(jsonConfig.getBytes())))
                        .build();
                System.out.println("Firebase initialized from environment variable.");
            } else {
                // Fallback to local file (Local Development Method)
                String serviceAccountPath = "lifeflow-30d1a-firebase-adminsdk-fbsvc-387a43696d.json";
                java.net.URL resource = FirebaseConfig.class.getClassLoader().getResource(serviceAccountPath);
                
                java.io.InputStream serviceAccount;
                if (resource != null) {
                    serviceAccount = resource.openStream();
                } else {
                    serviceAccount = new FileInputStream(serviceAccountPath);
                }

                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                System.out.println("Firebase initialized from local file.");
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            firestore = FirestoreClient.getFirestore();
        } catch (IOException e) {
            System.err.println("Firebase initialization error: " + e.getMessage());
        }
    }

    public static Firestore getFirestore() {
        return firestore;
    }
}
