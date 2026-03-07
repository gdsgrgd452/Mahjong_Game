<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Game Over!</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
</head>

<body class="page-center">
<div class="card">
    <c:forEach items="${players}" var="player">
        <p>Player: ${player.username} - ${player.points} Points</p>
    </c:forEach>
</div>
</body>
</html>