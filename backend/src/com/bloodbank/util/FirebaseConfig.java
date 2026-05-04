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
            System.out.println("🔍 Firebase: Attempting to load firebase-key.json...");
            java.io.InputStream serviceAccount = null;
            
            // Strategy 1: Class resource
            serviceAccount = FirebaseConfig.class.getResourceAsStream("/firebase-key.json");
            if (serviceAccount != null) {
                System.out.println("✅ Firebase: Found key via getResourceAsStream('/')");
            } else {
                // Strategy 2: Classloader resource
                serviceAccount = FirebaseConfig.class.getClassLoader().getResourceAsStream("firebase-key.json");
                if (serviceAccount != null) {
                    System.out.println("✅ Firebase: Found key via getClassLoader().getResourceAsStream()");
                } else {
                    // Strategy 3: Absolute path on Railway/Tomcat
                    java.io.File absoluteFile = new java.io.File("/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/firebase-key.json");
                    if (absoluteFile.exists()) {
                        serviceAccount = new java.io.FileInputStream(absoluteFile);
                        System.out.println("✅ Firebase: Found key via absolute path");
                    }
                }
            }

            if (serviceAccount == null) {
                System.err.println("❌ Firebase: FAILED to find firebase-key.json in ANY location.");
                // Debug: List files in common locations
                try {
                    java.io.File classesDir = new java.io.File("/usr/local/tomcat/webapps/ROOT/WEB-INF/classes");
                    if (classesDir.exists()) {
                        System.out.println("📁 Files in classes dir: " + java.util.Arrays.toString(classesDir.list()));
                    }
                } catch (Exception ignored) {}
                throw new java.io.FileNotFoundException("Firebase key file not found!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("🚀 Firebase: Successfully initialized Firestore!");
        } catch (Exception e) {
            System.err.println("❌ Firebase: Initialization Error: " + e.getMessage());
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
