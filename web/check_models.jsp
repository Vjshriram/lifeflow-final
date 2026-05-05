<%@ page import="java.io.*, java.net.*, org.json.*" %>
<%
    String apiKey = System.getenv("GEMINI_API_KEY");
    if (apiKey == null || apiKey.isEmpty()) {
        out.println("<h1>Error: GEMINI_API_KEY not found in environment.</h1>");
    } else {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBody = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) responseBody.append(line);
                in.close();
                
                JSONObject json = new JSONObject(responseBody.toString());
                JSONArray models = json.getJSONArray("models");
                
                out.println("<h1>Supported Models for your API Key:</h1><ul>");
                for (int i = 0; i < models.length(); i++) {
                    JSONObject m = models.getJSONObject(i);
                    String name = m.getString("name").replace("models/", "");
                    out.println("<li><strong>" + name + "</strong> (Supports: " + m.getJSONArray("supportedGenerationMethods").toString() + ")</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<h1>API Error: " + responseCode + "</h1>");
                // Read error stream
                InputStream es = conn.getErrorStream();
                if (es != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(es));
                    String line;
                    while ((line = reader.readLine()) != null) out.println(line);
                }
            }
        } catch (Exception e) {
            out.println("<h1>System Error: " + e.getMessage() + "</h1>");
            e.printStackTrace(new PrintWriter(out));
        }
    }
%>
