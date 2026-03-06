<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Homepage</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dark-theme.css" type="text/css">
</head>

<body>
<div class="navbar">
    <form action="${pageContext.request.contextPath}/startGame" method="get" class="button_padding">
        <button type="submit" class="button">Start a new game</button>
    </form>
    <form action="${pageContext.request.contextPath}/logout" method="post" class="button_padding">
        <button type="submit" class="button">Logout</button>
    </form>

    <form action="${pageContext.request.contextPath}/login" method="post" class="button_padding"> <!-- change to profile page -->
        <button type="submit" class="button">Profile</button>
    </form>
</div>
<h1>Welcome to mahjong ${user.username}</h1>
</body>
</html>