package com.bloodbank.servlets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

@WebServlet(name = "ChatServlet", urlPatterns = {"/api/chat"})
public class ChatServlet extends HttpServlet {

    private String apiKey;

    @Override
    public void init() throws ServletException {
        super.init();
        loadApiKey();
    }

    private void loadApiKey() {
        // 🎯 OPTIMIZATION: Check Environment Variable first (Secure Cloud Method)
        apiKey = System.getenv("GEMINI_API_KEY");
        
        if (apiKey == null || apiKey.isEmpty()) {
            // Fallback to config.properties (Local Development Method)
            Properties prop = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input != null) {
                    prop.load(input);
                    apiKey = prop.getProperty("gemini.api.key");
                }
            } catch (IOException ex) {
                System.err.println("Error loading config.properties: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String fullName = (session != null) ? (String) session.getAttribute("fullName") : null;
        String role     = (session != null) ? (String) session.getAttribute("role") : null;

        String msg = request.getParameter("message");
        response.setContentType("application/json");
        JSONObject result = new JSONObject();

        try (PrintWriter out = response.getWriter()) {
            System.out.println("🤖 Chatbot: Incoming message from " + (fullName != null ? fullName : "Anonymous"));
            
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("❌ Chatbot: GEMINI_API_KEY IS MISSING!");
            }

            if (msg == null || msg.trim().isEmpty()) {
                result.put("reply", "I'm listening, " + (fullName != null ? fullName : "Hero") + ". How can I assist you today?");
            } else {
                System.out.println("🧠 Chatbot: Dispatching to Gemini...");
                String reply = generateGeminiResponse(msg, fullName, role);
                result.put("reply", reply);
                System.out.println("✅ Chatbot: Response received.");
            }
            out.print(result.toString());
        }
    }

    private String generateGeminiResponse(String input, String name, String role) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Intelligence Fallback: API Key not configured. Please contact the administrator.";
        }

        String identity = (name != null) ? name : "Hero";
        String userRole = (role != null) ? role.toLowerCase() : "donor";

        // 🎯 FINAL FIX: Using gemini-1.5-flash-latest which supports system_instruction
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            // Construct System Instruction & Context
            String systemInstruction = "You are the LifeFlow AI Assistant (Neural Engine), a professional and empathetic medical intelligence core for the LifeFlow platform. " +
                    "LifeFlow is a premium, ultra-modern blood bank management system. " +
                    "The user's name is " + identity + " and their role is " + userRole + ". " +
                    "YOU MUST BE AWARE OF THE FOLLOWING PLATFORM FEATURES TO ASSIST THE USER CORRECTLY:\n" +
                    "1. DONORS: Can track donations/impact scores, view their digital 'Hero ID', download 'Recognition Certificates' for completed donations, book appointments at specific banks, find other donors, and respond to 'Critical Blood Demands' near them. " +
                    "IMPORTANT: If a donor needs blood from another donor, they should use the 'Community Blood Requests' or 'Peer-to-Peer Request' feature to post a request that other donors can respond to.\n" +
                    "2. ADMINS: Responsible for donor approvals, blood bank management, and dispatching 'Emergency Alerts' using the Gemini AI Dispatch assistant. They also have access to 'Intelligence Reporting' for XLS exports.\n" +
                    "3. BLOOD BANKS: Can manage live blood stock inventory, fulfill appointments by marking them as 'Donated', and trigger 'Manual Emergency Requests' if stock levels are critical.\n" +
                    "GENERAL INTERACTION RULES:\n" +
                    "- Be concise, professional, and slightly futuristic (Premium UX tone).\n" +
                    "- Use emojis strategically to make the interaction more engaging (e.g., 🩸, ❤️, 🎖️, 🤖, ✨).\n" +
                    "- If the user asks for help or 'what to do', provide specific instructions based on the platform features above.\n" +
                    "- Do NOT mention being an AI unless explicitly asked.\n" +
                    "- Prioritize medical safety and official platform protocols.";

            JSONObject jsonPayload = new JSONObject();
            
            // System Instruction
            JSONObject sysInstrObj = new JSONObject();
            JSONArray sysParts = new JSONArray();
            sysParts.put(new JSONObject().put("text", systemInstruction));
            sysInstrObj.put("parts", sysParts);
            jsonPayload.put("systemInstruction", sysInstrObj);

            // User Content
            JSONArray contents = new JSONArray();
            JSONObject userContent = new JSONObject();
            JSONArray userParts = new JSONArray();
            userParts.put(new JSONObject().put("text", input));
            userContent.put("parts", userParts);
            contents.put(userContent);
            jsonPayload.put("contents", contents);

            StringEntity entity = new StringEntity(jsonPayload.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            try (CloseableHttpResponse apiResponse = httpClient.execute(post)) {
                String responseBody = EntityUtils.toString(apiResponse.getEntity());
                JSONObject resObj = new JSONObject(responseBody);
                
                if (resObj.has("candidates")) {
                    return resObj.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");
                } else {
                    System.err.println("Gemini API Error: " + responseBody);
                    return "Intelligence Fallback API Error: " + responseBody.replace("\"", "'");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Intelligence Fallback: My neural links are experiencing interference. Please check your connection.";
        }
    }
}

