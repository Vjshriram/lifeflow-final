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
            System.out.println("🔍 Firebase: Attempting to load stealth key (firebase-key.txt)...");
            java.io.InputStream encodedStream = FirebaseConfig.class.getResourceAsStream("/firebase-key.txt");
            
            if (encodedStream == null) {
                encodedStream = FirebaseConfig.class.getClassLoader().getResourceAsStream("firebase-key.txt");
            }

            if (encodedStream == null) {
                throw new java.io.FileNotFoundException("Stealth key file not found!");
            }

            // Read all bytes and decode from Base64
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = encodedStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            
            byte[] decodedJson = java.util.Base64.getDecoder().decode(buffer.toString().trim());
            System.out.println("✅ Firebase: Stealth key decoded successfully.");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(decodedJson)))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("🚀 Firebase: Successfully initialized Firestore via Stealth Key!");
        } catch (Exception e) {
            System.err.println("❌ Firebase: Stealth Initialization Error: " + e.getMessage());
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
