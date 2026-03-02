<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tiles Display</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/tilesDisplay.css" type="text/css"> <!-- Linking the css in -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/tableDisplay.css" type="text/css">

</head>
<body>
<div class="game-container">
    <div class="mahjong-board">
        <c:forEach items="${tiles}" var="tile">
            <div class="tile">
                <span class="suit-label">${tile.suit}</span>

                    <%-- Only shows number if it exists --%>
                <c:if test="${not empty tile.number}">
                    <span class="number-label">${tile.number}</span>
                </c:if>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>