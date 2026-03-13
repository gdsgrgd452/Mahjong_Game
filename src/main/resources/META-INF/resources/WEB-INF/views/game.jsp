<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tilesDisplay.css" type="text/css"> <!-- Linking the css in -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tableDisplay.css" type="text/css">
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

                        <%-- Not placed tiles --%>
                    <c:forEach items="${player.getCurrentHandNoPlaced()}" var="tile">

                            <c:choose>
                                <c:when test="${tile.action.actionType == 'Pung'}">
                                    <c:set var="tileClass" value="tile-in-pung" />
                                </c:when>

                                <c:when test="${tile.action.actionType == 'Chow'}">
                                    <c:set var="tileClass" value="tile-in-chow" />
                                </c:when>

                                <c:when test="${tile.action.actionType == 'Kong'}">
                                    <c:set var="tileClass" value="tile-in-kong" />
                                </c:when>

                                <c:otherwise>
                                    <c:set var="tileClass" value="tile-active" />
                                    <%-- If it was just picked up--%>
                                    <c:set var="pickedClass" value="${tile.justPickedUp ? ' tile-just-picked' : ''}" />
                                </c:otherwise>
                            </c:choose>

                            <button type="button" class="tile ${tileClass} ${pickedClass}"
                                        data-tile-id="${tile.tileId}"
                                        data-amount-remaining="${tile.amountRemaining}"
                                        onclick="discardTile(this)">
                                <span class="suit-label">${tile.suit}</span>
                                <c:if test="${tile.number != null && tile.number != 0}">
                                    <span class="number-label">${tile.number}</span>
                                </c:if>
                            </button>
                        </c:forEach>

                        <%-- Placed tiles --%>
                    <c:forEach items="${player.getCurrentHandPlaced()}" var="tile">
                            <c:if test="${tile.action != null}">
                                <%-- Gives the tiles a different colour if they are in a placed action --%>
                                <c:set var="inPungClass" value="${tile.action.actionType == 'Pung' ? 'tile-in-pung' : ''}" />
                                <c:set var="inChowClass" value="${tile.action.actionType == 'Chow' ? 'tile-in-chow' : ''}" />
                            </c:if>

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
                        <c:set var="wActive" value="${player.actionToTake == 'W' ? 'btn-active' : ''}" />
                        <button class="action-btn ${wActive}" onclick="sendPlayerAction(${player.playerId}, 'win')">Win</button>
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