package com.example.mahjong_game.controller;


import com.example.mahjong_game.logic.BotLogic;
import com.example.mahjong_game.logic.Discard;
import com.example.mahjong_game.logic.GameFinisher;
import com.example.mahjong_game.logic.GameStarter;
import com.example.mahjong_game.logic.checks.Checks;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.GameService;
import com.example.mahjong_game.service.PlayerService;
import com.example.mahjong_game.service.TileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@Controller
public class GamePageController {

    private final GameStarter gameStarter;
    private final TileService tileService;
    private final GameService gameService;
    private final PlayerService playerService;
    private final Discard discard;
    private final Checks checks;
    private final BotLogic botLogic;
    private static final Logger logger = LoggerFactory.getLogger(GamePageController.class);
    private final GameFinisher gameFinisher;
    private static final ResponseEntity<String> gameFinishedMessage = ResponseEntity.status(403).body("Unauthorized: Game is not ongoing.");

    @Autowired
    public GamePageController(GameStarter gameStarter, TileService tileService, GameService gameService, PlayerService playerService, Discard discard, Checks checks, BotLogic botLogic, GameFinisher gameFinisher) {
        this.gameStarter = gameStarter;
        this.tileService = tileService;
        this.gameService = gameService;
        this.playerService = playerService;
        this.discard = discard;
        this.checks = checks;
        this.botLogic = botLogic;
        this.gameFinisher = gameFinisher;
    }

    @GetMapping("/startGame")
    public String serveStartGamePage() {
        logger.info("Starting a new game");
        return "startGame";
    }

    @GetMapping("/game")
    public String serveGamePage(@RequestParam("botsCount") Integer botsCount, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Game currentGame = gameService.findFirstGame(); //Replace with find by id
        boolean allBots = botsCount == 4;
        if (currentGame == null) {
            currentGame = gameStarter.initialiseGame(playerService.findPlayerByUsername(username), allBots, botsCount);
        }

        List<Player> players = currentGame.getPlayersInGame();
        List<Tile> liveTiles = tileService.getLiveTiles(currentGame);
        List<Tile> discardedTiles = tileService.getDiscardedTiles(currentGame);
        Tile justDiscardedTile = tileService.getJustDiscardedTile(currentGame);
        Player playerWithActionToTake = playerService.findPlayerWithActionToTake();
        model.addAttribute("players", players);
        model.addAttribute("liveTiles", liveTiles);
        model.addAttribute("discardedTiles", discardedTiles);
        model.addAttribute("justDiscardedTile", justDiscardedTile);
        model.addAttribute("playerWithActionToTake", playerWithActionToTake);
        model.addAttribute("isPlayerWithActionToTakeBot", playerWithActionToTake != null && playerWithActionToTake.isBot());
        model.addAttribute("currentPlayerId", currentGame.getCurrentPlayer().getPlayerId());
        model.addAttribute("isCurrentPlayerBot", currentGame.getCurrentPlayer().isBot());
        return "game";
    }

    @PostMapping("/game/discard")
    @ResponseBody
    public ResponseEntity<?> handlePlayerDiscard(@RequestParam("tileId") Integer tileId) {
        try {
            Game currentGame = gameService.findFirstGame();
            Tile tile = tileService.findTileById(tileId);

            if (!currentGame.isOngoing()) {
                return gameFinishedMessage;
            }
            if (tile.getPlayer() == null) {
                return ResponseEntity.status(403).body("Unauthorized: It is not your turn or you do not own this tile.");
            }
            if (tile.getPlayer().getPlayerId() != (currentGame.getCurrentPlayer().getPlayerId())) {
                return ResponseEntity.status(403).body("Unauthorized: It is not your turn or you do not own this tile.");
            }

            handleDiscard(currentGame, tile);

            return ResponseEntity.ok("Tile discarded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing discard: " + e.getMessage());
        }
    }


    @PostMapping("/game/botDiscard")
    @ResponseBody
    public ResponseEntity<?> handleBotDiscard() {
        try {
            Game currentGame = gameService.findFirstGame();
            if (!currentGame.isOngoing()) {
                return gameFinishedMessage;
            }
            if (currentGame.getCurrentPlayer().isBot()) {
                Tile discardedTile = botLogic.discardATileWithLogic(currentGame.getCurrentPlayer());
                logger.info("Bot discarding tile: {}, {}", discardedTile.getSuit(), discardedTile.getNumber());
                handleDiscard(currentGame, discardedTile);
                return ResponseEntity.ok(Map.of("status", "SUCCESS", "tileId", discardedTile.getTileId()));
            }
            return ResponseEntity.status(400).body("Current player is not a bot");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public void handleDiscard(Game currentGame, Tile tile) {
        discard.discardTile(tile.getTileId());

        Map<Player, String> playerAndActionMap = checks.lookForActionsAfterDiscard(currentGame.getPlayersInGame(), currentGame.getCurrentPlayer(), tile);

        if (!playerAndActionMap.isEmpty()) {
            Map.Entry<Player, String> playerAndAction = playerAndActionMap.entrySet().iterator().next(); //Get an element from the map (Should only be 1)

            Player playerWithAction = playerAndAction.getKey();
            String action = playerAndAction.getValue();

            if ("W".equals(action) && playerWithAction.isBot()) {
                logger.info("Bot win detected for {}.", playerWithAction.getUsername());
                discard.whenSomeActionsFound(currentGame, playerWithAction, "W");
                gameFinisher.finishGame(currentGame);
                return;
            }

            playerWithAction.setActionToTake(action); //Sets action to P or C depending on action
            playerService.savePlayer(playerWithAction);
        } else {
            discard.whenNoActionsFound(currentGame);
        }
    }

    @PostMapping("/game/pung")
    @ResponseBody
    public ResponseEntity<String> handlePung(@RequestParam("playerId") Integer playerId, @RequestParam("bot") boolean isBot) {
        try {

            Game currentGame = gameService.findFirstGame();
            if (!currentGame.isOngoing()) {
                return gameFinishedMessage;
            }
            Player player = playerService.findPlayerById(playerId);

            if (isBot) {
                logger.info("Bot acting on a pung it detected {}", player.getUsername());
            } else {
                logger.info("Player acting on a pung it detected {}", player.getUsername());
            }

            discard.whenSomeActionsFound(currentGame, player, "P");

            return ResponseEntity.ok("Pung successful");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid Pung: " + e.getMessage());
        }
    }

    @PostMapping("/game/chow")
    @ResponseBody
    public ResponseEntity<String> handleChow(@RequestParam("playerId") Integer playerId, @RequestParam("bot") boolean isBot) {

        try {
            Game currentGame = gameService.findFirstGame();
            if (!currentGame.isOngoing()) {
                return gameFinishedMessage;
            }
            Player player = playerService.findPlayerById(playerId);

            if (isBot) {
                logger.info("Bot acting on a chow it detected {}", player.getUsername());
            } else {
                logger.info("Player acting on a chow it detected {}", player.getUsername());
            }

            discard.whenSomeActionsFound(currentGame, player, "C");

            return ResponseEntity.ok("Chow successful");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid Chow: " + e.getMessage());
        }
    }

    @PostMapping("/game/win")
    @ResponseBody
    public ResponseEntity<String> handleWin(@RequestParam("playerId") Integer playerId) {
        Game currentGame = gameService.findFirstGame();
        Player player = playerService.findPlayerById(playerId);
        logger.info("win endpoint reached because player was not a bot {}" , player.isBot());
        discard.whenSomeActionsFound(currentGame, player, "W");
        gameFinisher.finishGame(currentGame);
        return ResponseEntity.ok("win");
    }


    @GetMapping("/tilesDisplay")
    public String serveTilesDisplayPage(Model model) {
        Game game = gameService.findFirstGame();
        List<Tile> tiles = tileService.findAllTilesByGame(game);
        model.addAttribute("tiles", tiles);
        return "tilesDisplay";
    }
}
