(function () {
    "use strict";

    function sendAction(url, data) {
        const formData = new URLSearchParams();
        for (const key in data) {
            formData.append(key, data[key]);
        }

        const baseUrl = (window.GAME_CONFIG && window.GAME_CONFIG.contextPath) ? window.GAME_CONFIG.contextPath : "";
        return fetch(baseUrl + url, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData
        }).then(async (response) => {
            if (response.ok) {
                location.reload();
                return;
            }
            const errorText = await response.text();
            alert("Action failed: " + errorText);
        });
    }

    function discardTile(buttonElement) {
        const tileId = buttonElement.getAttribute("data-tile-id");
        return sendAction("/game/discard", { tileId: tileId });
    }

    function sendPlayerAction(playerId, actionType) {
        const url = actionType === "pung" ? "/game/pung" : "/game/chow";
        return sendAction(url, { playerId: playerId, bot: false});
    }

    function triggerBotTurn() {
        const baseUrl = (window.GAME_CONFIG && window.GAME_CONFIG.contextPath) ? window.GAME_CONFIG.contextPath : "";
        return fetch(baseUrl + "/game/botDiscard", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        }).then(async (response) => {
            if (response.ok) {
                location.reload();
                return;
            }
            console.error("Bot discard failed");
        });
    }

    function triggerBotAction() {
        const cfg = window.GAME_CONFIG || {};
        if (!cfg.playerWithActionToTakeExists) return;
        if (!cfg.isPlayerWithActionToTakeBot) return;

        const actionToTake = cfg.actionToTake; // "P" or "C"
        const playerId = cfg.actionPlayerId;

        const actionUrl = actionToTake === "P" ? "/game/pung" : "/game/chow";
        return sendAction(actionUrl, { playerId: playerId, bot: true });
    }

    window.addEventListener("DOMContentLoaded", function () {
        const cfg = window.GAME_CONFIG || {};

        const isCurrentPlayerBot = !!cfg.isCurrentPlayerBot;
        const playerWithActionExists = !!cfg.playerWithActionToTakeExists;
        const isActionPlayerBot = !!cfg.isPlayerWithActionToTakeBot;

        // Case 1: Someone has a reaction action, and that someone is a bot -> bot should act NOW
        if (playerWithActionExists && isActionPlayerBot) {
            setTimeout(triggerBotAction, 3000);
            return;
        }

        // Case 2: No reaction action pending; if it's bot's turn -> bot discards
        if (isCurrentPlayerBot && !playerWithActionExists) {
            setTimeout(triggerBotTurn, 3000);
        }
    });

    // Expose functions for inline onclick handlers in JSP
    window.discardTile = discardTile;
    window.sendPlayerAction = sendPlayerAction;
})();