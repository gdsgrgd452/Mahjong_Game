<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
</head>

<body class="page-center">
<div class="card">
    <h1>Username: ${player.username}</h1>
    <h2>Total Wins: ${player.wins}</h2>
</div>
</body>
</html>