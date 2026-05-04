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
            System.out.println("🔍 Firebase: Ghost-writing credentials...");
            
            // Build the JSON manually to ensure PERFECT character integrity
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"type\": \"service_account\",\n");
            json.append("  \"project_id\": \"lifeflow-30d1a\",\n");
            json.append("  \"private_key_id\": \"387a43696d22420f5417c9abdd7467d520e3fc05\",\n");
            json.append("  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\n");
            json.append("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCj3DTN5xyEiaYv\\n");
            json.append("FDok2v6SffvnNiMnZnK0AFGtZ2TrXlVaJQuGGVV68fnJuBJ1cmN2XxR7pEmxedVO\\n");
            json.append("qeKd1fUxOBPIAvN11xPL/F0BkQ+0hEg7xSxDIJy+kLVhzHsw1YzzYndNRSB6CLir\\n");
            json.append("7mT3MyE9J4tXPSe0ObECPjX+LSdcwYfNjpFoKJPfnYIYNHu7tBmKdZJWMlDJH29y\\n");
            json.append("1kFzAEbGtux4XNybbbvxwltvdigEcnA9RIajxr7ukah+NyKpA+wC7Ldw5W0KBdCt\\n");
            json.append("WNjvAzvQIfLEiTiUyRSNQisVB1l8Bcu4ESjKDV1qbZeHzcoyc1vwonfvfkrrM9AI\\n");
            json.append("5F7Fq/HXAgMBAAECggEADfIy1P12ZLoPGKhY5IR2W0MkD+3Vzcd/PAkKP+nHYe2W\\n");
            json.append("8m5UY0GVEBA33sPEX8DwbIWIyZcreyfv5YXkVuOmvogpItehdKZCten7g850BDyn\\n");
            json.append("+zJRDeuLe6qhorppF7htNSpsa06sBhw86iExmwUmhJJw3mgYO34XgyXb/uSamjRu\\n");
            json.append("/HHRhB0f+iOZxUEgTjSN4+dJt2OndCyIYsqNPJG7Gbw+YPXJA6g1sjl8UwQ6ZTeq\\n");
            json.append("Kgl2VNx/JHGNJpLVtMpHovAhqxx3EY9qAkWAAtYDdfpYhXgwBZMGw9GjpEar8TdU\\n");
            json.append("HoeNejvpd/tWfWOUlqD8V2MhBiwsqSAp30xvXQ2guQKBgQDN8pIgf8Pk16Dn4fCp\\n");
            json.append("T2ZyYlX/dKFinNW076JM8/npQ1cE3hFtCBVSqEmi1DSkoQI0+spS6cuaJta/JF/W\\n");
            json.append("81IJ1kvievDRQRqPnqVuU36yIiKbScEYGUZ7Bj401G2hy18RYF4o2ELX+IAqThBh\\n");
            json.append("Q6nGytm5yZt5fqU7VvQctLbhiQKBgQDLrxaDAls1rYWwq12VB5dRuS1OcyOFh8wp\\n");
            json.append("pTGsnhkRV74MIGUnb/rZ+OSTCmARlZG/pRwPaG6KjqZMx19Tf6kg3ajb2WzPN75B\\n");
            json.append("85dad6A+zvedraXXzzrVXhac37UVnwufVt190PkQ1f9GGqhOcuc3q8dcwCYEdEh/\\n");
            json.append("cHgYUwxAXwKBgB7LASxYzip+TzG8p6Y5GAFMUL10a9j5yD5YgjTtWdWV2wIATiy2\\n");
            json.append("Q7HrNa9h+UkQRes0AGJrUKUI350OzEGwefi8kPYZGb6/9D+7IdMgKtZpojED0xpO\\n");
            json.append("VSp54X02sfm6FcncVdfXlg8Cue8ZYvuYCV+O3wUXbua4l+4Kb0+Heby5AoGBAMow\\n");
            json.append("ZAhormH6fluBwMPPZUaaq37UjM5gnyoUNVtFFV1B3EXtYnxjjIATsdLE2diawLOd\\n");
            json.append("Et24rQKd2DcfMmGQuDMH1jdm/bw1eYe+ZuBHH6s5iFPdrGMuMxja7VeMOhXca40g\\n");
            json.append("jX33k0ZDJ8RPcgNtzFhXDO/lTdfeFplq68w5E/BAoGAeEEU/dfP4MkqHryz/VNi\\n");
            json.append("1F7hC6HI867+mc1tAQlrc7/Kr+BUH3u7d8I8UW1P/l8XL0FS4S49WcC42BJ+kpIC\\n");
            json.append("ABw3oULJ/cTqcyv0Nc92XdQqUnlGpIwk54TbAXSemjCgJqb8u95LXTfPvx0M4BZc\\n");
            json.append("Fn+gbHN6oR97CBlW2ZZ3gUw=\\n");
            json.append("-----END PRIVATE KEY-----\\n\",\n");
            json.append("  \"client_email\": \"firebase-adminsdk-fbsvc@lifeflow-30d1a.iam.gserviceaccount.com\",\n");
            json.append("  \"client_id\": \"112978634399343365975\",\n");
            json.append("  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n");
            json.append("  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n");
            json.append("  \"auth_provider_x509_cert_uri\": \"https://www.googleapis.com/oauth2/v1/certs\",\n");
            json.append("  \"client_x509_cert_uri\": \"https://www.googleapis.com/web/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40lifeflow-30d1a.iam.gserviceaccount.com\",\n");
            json.append("  \"universe_domain\": \"googleapis.com\"\n");
            json.append("}");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new java.io.ByteArrayInputStream(json.toString().getBytes())))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
            System.out.println("🚀 Firebase: Ghost-writing SUCCESS!");
        } catch (Exception e) {
            System.err.println("❌ Firebase: Ghost-writing FAILED: " + e.getMessage());
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
