<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Start a new game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dropdownStyle.css" type="text/css">
</head>
<body class="page-center">
<div class="card">
    <h1>Start a new game</h1>
    <div class="dropdown">
        <button class="dropdown-trigger">
            Game type
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" xmlns="http://www.w3.org/2000/svg"> <!-- The V arrow -->
                <path d="M2 4L6 8L10 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
            </svg>
        </button>
        <div class="dropdown-menu">
            <a href="${pageContext.request.contextPath}/game?botsCount=3" class="dropdown-item">Play against bots</a>
            <a href="${pageContext.request.contextPath}/game?botsCount=0" class="dropdown-item">Play against friends</a>
            <a href="${pageContext.request.contextPath}/game?botsCount=4" class="dropdown-item">Simulate (Only bots)</a>
        </div>
    </div>
    <div class="small-links">
        <a href="${pageContext.request.contextPath}/homepage">Back to homepage</a>
    </div>
</div>
</body>
</html>