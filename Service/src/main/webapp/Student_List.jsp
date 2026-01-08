<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="client" class="org.example.service.GroupServiceClient" scope="page"/>

<!DOCTYPE html>
<html>
<head>
    <title>Student List</title>
</head>
<body>
<h1>Student List</h1>
<pre><%= client.getStudentsXml() %></pre>

<h2>Next Number Test</h2>
<p>Next after 10: <%= client.getNext(10) %></p>
</body>
</html>