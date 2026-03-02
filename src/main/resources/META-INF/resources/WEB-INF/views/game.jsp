<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/tilesDisplay.css" type="text/css"> <!-- Linking the css in -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/tableDisplay.css" type="text/css">
</head>
<body>
    <div class="game-container">
        <c:if test="${justDiscardedTile != null}">
            <div class="center-stage">
                <div class="tile tile-large">
                    <span class="suit-label">${justDiscardedTile.suit}</span>
                    <c:if test="${justDiscardedTile.number != null && justDiscardedTile.number != 0}">
                        <span class="number-label">${justDiscardedTile.number}</span>
                    </c:if>
                </div>
            </div>
        </c:if>
        <div class="mahjong-board">
            <div class="live-section">
                <c:forEach items="${liveTiles}" var="tile">
                    <div class="tile">
                        <span class="suit-label">${tile.suit}</span>
                        <c:if test="${tile.number != null && tile.number != 0}">
                            <span class="number-label">${tile.number}</span>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
            <div class="discarded-section">
                <c:forEach items="${discardedTiles}" var="tile">
                    <div class="tile ${tile.justDiscarded == true ? 'tile-just-discarded' : 'tile'}">
                        <span class="suit-label">${tile.suit}</span>
                        <c:if test="${tile.number != null && tile.number != 0}">
                            <span class="number-label">${tile.number}</span>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div class="hands-section">
            <c:forEach items="${players}" var="player">
                <c:set var="actionToTake" value="${player.actionToTake == true ? 'player-has-action' : ''}" />
                <c:set var="playerTurn" value="${player.playerId == currentPlayerId ? 'player-hand-turn' : ''}" />
                <div class="player-hand ${playerTurn} ${actionToTake}">
                    <p class="playerId">${player.playerId}</p>
                        <c:forEach items="${player.getCurrentHandNoPlaced()}" var="tile">
                            <%-- Determine the base status class --%>
                            <c:set var="inPungClass" value="${tile.pung != null ? 'tile-in-pung' : 'tile-active'}" />
                            <%-- Appends tile-in-chow if it is in a chow --%>
                            <c:set var="inChowClass" value="${tile.chow != null ? 'tile-in-chow' : ''}" />
                            <%-- Appends 'tile-just-picked' if it was just picked --%>
                            <c:set var="pickedClass" value="${tile.justPickedUp ? ' tile-just-picked' : ''}" />
                            <button type="button" class="tile ${inPungClass} ${inChowClass} ${pickedClass}"
                                        data-tile-id="${tile.tileId}"
                                        data-amount-remaining="${tile.amountRemaining}"
                                        onclick="discardTile(this)">
                                <span class="suit-label">${tile.suit}</span>
                                <c:if test="${tile.number != null && tile.number != 0}">
                                    <span class="number-label">${tile.number}</span>
                                </c:if>
                            </button>
                        </c:forEach>
                        <c:forEach items="${player.getCurrentHandPlaced()}" var="tile">
                            <c:set var="inPungClass" value="${tile.pung != null ? 'tile-in-pung' : ''}" />
                            <c:set var="inChowClass" value="${tile.chow != null ? 'tile-in-chow' : ''}" />
                            <div class="tile tile-placed ${inPungClass} ${inChowClass}">
                                <span class="suit-label">${tile.suit}</span>
                                <c:if test="${tile.number != null && tile.number != 0}">
                                    <span class="number-label">${tile.number}</span>
                                </c:if>
                            </div>
                        </c:forEach>
                    <p>${fn:length(player.getCurrentHandPlaced())}, ${fn:length(player.getCurrentHandNoPlaced())}</p>
                    <div class="action-buttons">
                        <c:set var="pActive" value="${player.actionToTake == 'P' ? 'btn-active' : ''}" />
                        <button class="action-btn ${pActive}" onclick="sendPlayerAction(${player.playerId}, 'pung')">Pung</button>
                        <c:set var="cActive" value="${player.actionToTake == 'C' ? 'btn-active' : ''}" />
                        <button class="action-btn ${cActive}" onclick="sendPlayerAction(${player.playerId}, 'chow')">Chow</button>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    <script>
        // Exposes the model values to the game.js
        window.GAME_CONFIG = {
            contextPath: "${pageContext.request.contextPath}",
            isCurrentPlayerBot: ${isCurrentPlayerBot},
            playerWithActionToTakeExists: ${playerWithActionToTake != null},
            isPlayerWithActionToTakeBot: ${isPlayerWithActionToTakeBot},
            actionToTake: "${playerWithActionToTake != null ? playerWithActionToTake.actionToTake : ''}",
            actionPlayerId: ${playerWithActionToTake != null ? playerWithActionToTake.playerId : -1}
        };
    </script>

    <script src="${pageContext.request.contextPath}/js/game.js"></script>
</body>
</html>