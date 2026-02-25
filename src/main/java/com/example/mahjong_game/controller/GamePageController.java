package com.example.mahjong_game.controller;


import com.example.mahjong_game.logic.Discard;
import com.example.mahjong_game.logic.GameStarter;
import com.example.mahjong_game.logic.checks.Checks;
import com.example.mahjong_game.logic.checks.PungsChecks;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.GameService;
import com.example.mahjong_game.service.PlayerService;
import com.example.mahjong_game.service.TileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class GamePageController {

    @Autowired
    public GameStarter gameStarter;
    @Autowired
    public TileService tileService;
    @Autowired
    public GameService gameService;
    @Autowired
    public PlayerService playerService;
    @Autowired
    private Discard discard;
    @Autowired
    private Checks checks;
    @Autowired
    private PungsChecks pungsChecks;

    @GetMapping("/game")
    public String serveGamePage(Model model) {
        Game currentGame = gameService.findFirstGame();

        if (currentGame == null) {
            currentGame = gameStarter.initialiseGame();
            System.out.println("Game initialized");
        }

        List<Player> players = currentGame.getPlayersInGame();
        List<Tile> liveTiles = tileService.getLiveTiles(currentGame);
        List<Tile> discardedTiles = tileService.getDiscardedTiles(currentGame);
        Tile justDiscardedTile = tileService.getJustDiscardedTile(currentGame);
        Player playerWithActionToTake = playerService.findPlayerWithActionToTake();
        if (playerWithActionToTake != null) System.out.println(playerWithActionToTake.getUsername());
        model.addAttribute("players", players);
        model.addAttribute("liveTiles", liveTiles);
        model.addAttribute("discardedTiles", discardedTiles);
        model.addAttribute("justDiscardedTile", justDiscardedTile);
        model.addAttribute("playerWithActionToTake", playerWithActionToTake);
        model.addAttribute("currentPlayerId", currentGame.getCurrentPlayer().getPlayerId());
        return "game";
    }

    @PostMapping("/game/discard") // Changed from just "/game"
    @ResponseBody
    public ResponseEntity<String> handleDiscard(@RequestParam("tileId") Integer tileId, Model model) {
        try {
            Game currentGame = gameService.findFirstGame();
            Tile tile = tileService.findTileById(tileId);

            if (tile.getPlayer() == null) {
                System.out.println("Failed, no person");
                return ResponseEntity.status(403).body("Unauthorized: It is not your turn or you do not own this tile.");
            }
            if (tile.getPlayer().getPlayerId() != (currentGame.getCurrentPlayer().getPlayerId())) {
                System.out.println("Failed, wrong person");
                return ResponseEntity.status(403).body("Unauthorized: It is not your turn or you do not own this tile.");
            }

            discard.discardTile(tileId);

            Map<Player, String> playerAndActionMap = checks.lookForActionsAfterDiscard(currentGame.getPlayersInGame(), currentGame.getCurrentPlayer(), tile);

            if (playerAndActionMap != null) {
                Map.Entry<Player, String> playerAndAction = playerAndActionMap.entrySet().iterator().next(); //Get an element from the map (Should only be 1)

                Player playerWithAction = playerAndAction.getKey();
                String action = playerAndAction.getValue();

                System.out.println(playerAndAction);

                playerWithAction.setActionToTake(action); //Sets action to P or C depending on action
                System.out.println("Just before save: " + playerWithAction.getTilesInActionToTake());
                playerService.savePlayer(playerWithAction);
            } else {
                discard.whenNoActionsFound(currentGame);
            }

            return ResponseEntity.ok("Tile discarded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing discard: " + e.getMessage());
        }
    }

    @PostMapping("/game/pung")
    @ResponseBody
    public ResponseEntity<String> handlePung(@RequestParam("playerId") Integer playerId) {
        System.out.println("Pung request received");
        try {
            Game game = gameService.findFirstGame();
            Player player = playerService.findPlayerById(playerId);
            System.out.println("After pung request: " + player.getTilesInActionToTake());
            discard.whenSomeActionsFound(game, player, "P");

            return ResponseEntity.ok("Pung successful");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid Pung: " + e.getMessage());
        }
    }

    @PostMapping("/game/chow")
    @ResponseBody
    public ResponseEntity<String> handleChow(@RequestParam("playerId") Integer playerId) {
        System.out.println("Chow request received");
        try {
            Game game = gameService.findFirstGame();
            Player player = playerService.findPlayerById(playerId);

            discard.whenSomeActionsFound(game, player, "C");

            return ResponseEntity.ok("Chow successful");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid Chow: " + e.getMessage());
        }
    }

    @GetMapping("/tilesDisplay")
    public String serveTilesDisplayPage(Model model) {
        Game game = gameService.findFirstGame();
        List<Tile> tiles = tileService.findAllTilesByGame(game);
        model.addAttribute("tiles", tiles);
        return "tilesDisplay";
    }
}
