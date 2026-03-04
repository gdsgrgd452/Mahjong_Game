<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
</head>
<body class="auth-page">
<div class="auth-card">
    <h2>Register</h2>

    <form:form action="/register" modelAttribute="user" method="post">
        <div class="form-row">
            <form:label path="username">Username</form:label>
            <form:input path="username" cssClass="button"/>
            <form:errors path="username" cssClass="error"/>
        </div>

        <div class="form-row">
            <form:label path="password">Password</form:label>
            <form:input path="password" cssClass="button"/>
            <form:errors path="password" cssClass="error"/>
        </div>

        <div class="form-actions">
            <input type="submit" value="Create account"/>
        </div>
    </form:form>

    <div class="small-links">
        <a href="${pageContext.request.contextPath}/login">Login</a>
    </div>
</div>
</body>
</html>