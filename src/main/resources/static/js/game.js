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
                const contentType = response.headers.get("content-type") || "";

                if (contentType.includes("application/json")) {
                    const data = await response.json();

                    if (data && data.status === "GAME_OVER" && data.redirectUrl) {
                        window.location.href = baseUrl + data.redirectUrl;
                        return;
                    }
                } else {
                    await response.text().catch(() => {});
                }

                location.reload();
                return;
            }
            const errorText = await response.text();
            if (!errorText.includes("Game is not ongoing")) {
                alert("Action failed: " + errorText);
                location.reload();
            }
        });
    }

    function discardTile(buttonElement) {
        const tileId = buttonElement.getAttribute("data-tile-id");
        return sendAction("/game/discard", { tileId: tileId });
    }

    function sendPlayerAction(playerId, actionType) {
        let url
        if (actionType === "pung") {
            url = "/game/pung"
        } else if (actionType === "chow") {
            url = "/game/chow"
        } else if (actionType === "win") {
            url = "/game/win"
        }
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

        // If a bot has an action it acts in the set timeframe
        if (playerWithActionExists && isActionPlayerBot) {
            setTimeout(triggerBotAction, 250);
            return;
        }

        // No action but is bot so discards tile in set timeframe
        if (isCurrentPlayerBot && !playerWithActionExists) {
            setTimeout(triggerBotTurn, 250);
        }
    });

    // Expose functions for inline onclick handlers in JSP
    window.discardTile = discardTile;
    window.sendPlayerAction = sendPlayerAction;
})();