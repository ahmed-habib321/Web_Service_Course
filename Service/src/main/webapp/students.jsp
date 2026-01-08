<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.client.GroupService" %>
<%@ page import="org.example.client.Group" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
        }
        pre {
            background-color: #f4f4f4;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
        }
        .error {
            color: red;
            background-color: #fee;
            padding: 10px;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Student List from Web Service</h1>

    <%
        try {
            // Create service and port
            GroupService service = new GroupService();
            Group port = service.getGroupPort();

            // Call the web service method
            String studentsXml = port.getStudents();

            // Test the getNext method
            int nextNumber = port.getnext(5);
    %>
    <h2>Students (XML):</h2>
    <pre><%= studentsXml %></pre>

    <h2>Test getNext method:</h2>
    <p>Next number after 5 is: <strong><%= nextNumber %></strong></p>
    <%
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<h2>Error calling web service:</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
            e.printStackTrace();
        }
    %>
</div>
</body>
</html>