<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/leaderboardStyle.css" type="text/css">
    <title>Leaderboard</title>
</head>
<body>
<div class="leaderboard-container">
    <div class="leaderboard">
        <h2>Top Players</h2>
        <c:forEach var="player" items="${players}" varStatus="status">
            <p>
                <span>${status.count}. ${player.username}</span>
                <span>Wins: ${player.wins}</span>
            </p>
        </c:forEach>
        <div class="small-links">
            <a href="${pageContext.request.contextPath}/homepage" method="get">Back to the homepage</a>
        </div>
    </div>
</div>
</body>
</html>