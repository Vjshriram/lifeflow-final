<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bloodbank.util.FirebaseConfig,com.google.cloud.firestore.*,java.util.List" %>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || !"ADMIN".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Database Cleanup | LifeFlow Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/assets/css/theme.css" rel="stylesheet">
</head>
<body class="bg-dark text-white p-5">
    <div class="container card card-modern p-5">
        <h2 class="text-danger mb-4">Database Cleanup: Stuck Users</h2>
        <div class="alert alert-warning">
            This tool will delete the following unverified accounts so they can re-register.
        </div>
        
        <ul class="list-group list-group-flush mb-4">
            <li class="list-group-item bg-transparent text-white border-secondary">vijay.shriram157@gmail.com</li>
            <li class="list-group-item bg-transparent text-white border-secondary">vijay.shriram1574@gmail.com</li>
        </ul>

        <%
            if ("true".equals(request.getParameter("confirm"))) {
                try {
                    Firestore db = FirebaseConfig.getFirestore();
                    String[] emails = {"vijay.shriram157@gmail.com", "vijay.shriram1574@gmail.com"};
                    int deletedCount = 0;
                    
                    for (String email : emails) {
                        QuerySnapshot snapshot = db.collection("users")
                            .whereEqualTo("email", email)
                            .get().get();
                        
                        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
                            db.collection("users").document(doc.getId()).delete().get();
                            deletedCount++;
                        }
                    }
        %>
                    <div class="alert alert-success">
                        Cleanup successful! <%= deletedCount %> records removed. You can now close this page.
                    </div>
                    <a href="home.jsp" class="btn btn-outline-light rounded-pill">Back to Dashboard</a>
        <%
                } catch (Exception e) {
        %>
                    <div class="alert alert-danger">Error: <%= e.getMessage() %></div>
        <%
                }
            } else {
        %>
                <form method="post" action="?confirm=true">
                    <button type="submit" class="btn btn-danger rounded-pill px-5">Confirm and Delete</button>
                    <a href="home.jsp" class="btn btn-outline-secondary rounded-pill px-5 ms-2">Cancel</a>
                </form>
        <%
            }
        %>
    </div>
</body>
</html>
