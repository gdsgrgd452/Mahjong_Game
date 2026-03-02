<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Homepage</title>
</head>

<body>
<h1>Welcome</h1>
<hr>
<div class="navBar">
    <ul>
        <li>New Game</li>
        <li>Account</li>
    </ul>
</div>
<p><a href="${pageContext.request.contextPath}/tilesDisplay">View tiles</a></p>
</body>
</html>