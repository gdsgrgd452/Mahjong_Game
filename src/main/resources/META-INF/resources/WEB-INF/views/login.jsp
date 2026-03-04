<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
</head>
<body class="auth-page">
<div class="auth-card">
    <h2>Login</h2>

    <form:form action="/login" modelAttribute="user" method="post">
        <div class="form-row">
            <form:label path="username">Username</form:label>
            <form:input path="username" cssClass="button"/>
        </div>

        <div class="form-row">
            <form:label path="password">Password</form:label>
            <form:input path="password" cssClass="button"/>
        </div>

        <div class="form-actions">
            <input type="submit" value="Login"/>
        </div>
    </form:form>

    <h3 class="error" th:if="${loginError}">${loginError}</h3>

    <div class="small-links">
        <a href="${pageContext.request.contextPath}/register">Register</a>
    </div>
</div>
</body>
</html>