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
            System.out.println("🔍 Firebase: Attempting manual initialization (Zero-JSON)...");
            
            String projectId = "lifeflow-30d1a";
            String clientEmail = "firebase-adminsdk-fbsvc@lifeflow-30d1a.iam.gserviceaccount.com";
            String part1 = "-----BEGIN PRIVATE KEY-----\n";
            String part2 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCj3DTN5xyEiaYv\n";
            String part3 = "FDok2v6SffvnNiMnZnK0AFGtZ2TrXlVaJQuGGVV68fnJuBJ1cmN2XxR7pEmxedVO\n";
            String part4 = "qeKd1fUxOBPIAvN11xPL/F0BkQ+0hEg7xSxDIJy+kLVhzHsw1YzzYndNRSB6CLir\n";
            String part5 = "7mT3MyE9J4tXPSe0ObECPjX+LSdcwYfNjpFoKJPfnYIYNHu7tBmKdZJWMlDJH29y\n";
            String part6 = "1kFzAEbGtux4XNybbbvxwltvdigEcnA9RIajxr7ukah+NyKpA+wC7Ldw5W0KBdCt\n";
            String part7 = "WNjvAzvQIfLEiTiUyRSNQisVB1l8Bcu4ESjKDV1qbZeHzcoyc1vwonfvfkrrM9AI\n";
            String part8 = "5F7Fq/HXAgMBAAECggEADfIy1P12ZLoPGKhY5IR2W0MkD+3Vzcd/PAkKP+nHYe2W\n";
            String part9 = "8m5UY0GVEBA33sPEX8DwbIWIyZcreyfv5YXkVuOmvogpItehdKZCten7g850BDyn\n";
            String part10 = "+zJRDeuLe6qhorppF7htNSpsa06sBhw86iExmwUmhJJw3mgYO34XgyXb/uSamjRu\n";
            String part11 = "/HHRhB0f+iOZxUEgTjSN4+dJt2OndCyIYsqNPJG7Gbw+YPXJA6g1sjl8UwQ6ZTeq\n";
            String part12 = "Kgl2VNx/JHGNJpLVtMpHovAhqxx3EY9qAkWAAtYDdfpYhXgwBZMGw9GjpEar8TdU\n";
            String part13 = "HoeNejvpd/tWfWOUlqD8V2MhBiwsqSAp30xvXQ2guQKBgQDN8pIgf8Pk16Dn4fCp\n";
            String part14 = "T2ZyYlX/dKFinNW076JM8/npQ1cE3hFtCBVSqEmi1DSkoQI0+spS6cuaJta/JF/W\n";
            String part15 = "81IJ1kvievDRQRqPnqVuU36yIiKbScEYGUZ7Bj401G2hy18RYF4o2ELX+IAqThBh\n";
            String part16 = "Q6nGytm5yZt5fqU7VvQctLbhiQKBgQDLrxaDAls1rYWwq12VB5dRuS1OcyOFh8wp\n";
            String part17 = "gTGsnhkRV74MIGUnb/rZ+OSTCmARlZG/pRwPaG6KjqZMx19Tf6kg3ajb2WzPN75B\n";
            String part18 = "85dad6A+zvedraXXzzrVXhac37UVnwufVt190PkQ1f9GGqhOcuc3q8dcwCYEdEh/\n";
            String part19 = "cHgYUwxAXwKBgB7LASxYzip+TzG8p6Y5GAFMUL10a9j5yD5YgjTtWdWV2wIATiy2\n";
            String part20 = "Q7HrNa9h+UkQRes0AGJrUKUI350OzEGwefi8kPYZGb6/9D+7IdMgKtZpojED0xpO\n";
            String part21 = "VSp54X02sfm6FcncVdfXlg8Cue8ZYvuYCV+O3wUXbua4l+4Kb0+Heby5AoGBAMow\n";
            String part22 = "ZAhormH6fluBwMPPZUaaq37UjM5gnyoUNVtFFV1B3EXtYnxjjIATsdLE2diawLOd\n";
            String part23 = "Et24rQKd2DcfMmGQuDMH1jdm/bw1eYe+ZuBHH6s5iFPdrGMuMxja7VeMOhXca40g\n";
            String part24 = "jX33k0ZDJ8RPcgNtzFhXDO/lTdfeFplq68w5E/BAoGAeEEU/dfP4MkqHryz/VNi\n";
            String part25 = "1F7hC6HI867+mc1tAQlrc7/Kr+BUH3u7d8I8UW1P/l8XL0FS4S49WcC42BJ+kpIC\n";
            String part26 = "ABw3oULJ/cTqcyv0Nc92XdQqUnlGpIwk54TbAXSemjCgJqb8u95LXTfPvx0M4BZc\n";
            String part27 = "Fn+gbHN6oR97CBlW2ZZ3gUw=\n";
            String part28 = "-----END PRIVATE KEY-----";

            String privateKey = part1 + part2 + part3 + part4 + part5 + part6 + part7 + part8 + part9 + part10 +
                               part11 + part12 + part13 + part14 + part15 + part16 + part17 + part18 + part19 + part20 +
                               part21 + part22 + part23 + part24 + part25 + part26 + part27 + part28;

            // Remove any potential double-escaping or literal \n text
            privateKey = privateKey.replace("\\n", "\n");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(com.google.auth.oauth2.ServiceAccountCredentials.fromPkcs8(
                            null, clientEmail, privateKey, null, null))
                    .setProjectId(projectId)
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("🚀 Firebase: Successfully initialized Firestore (Zero-JSON)!");
        } catch (Exception e) {
            System.err.println("❌ Firebase: Manual Initialization Error: " + e.getMessage());
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
