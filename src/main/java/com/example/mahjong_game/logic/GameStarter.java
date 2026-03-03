package com.example.mahjong_game.logic;

import com.example.mahjong_game.logic.checks.Checks;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.GameService;
import com.example.mahjong_game.service.PlayerService;
import com.example.mahjong_game.service.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameStarter {
    private final TileService tileService;
    private final GameService gameService;
    private final PlayerService playerService;
    private final HelperFunctions h;
    private final Actions actions;
    private final Checks checks;
    Random r = new Random();
    private static final Logger logger = LoggerFactory.getLogger(GameStarter.class);

    @Autowired
    public GameStarter(TileService tileService, GameService gameService, PlayerService playerService, HelperFunctions h, Actions actions, Checks checks) {
        this.tileService = tileService;
        this.gameService = gameService;
        this.playerService = playerService;
        this.h = h;
        this.actions = actions;
        this.checks = checks;
    }
    Game game;
    Player dealer;

    public Game initialiseGame(Player playerToStartGame, boolean allBots, int botsCount) {
        logger.info("Initialising a new game...");
        game = gameService.createGame();
        List<Player> players = createPlayersForGame(botsCount);
        if (!allBots) players.add(playerToStartGame);
        addPlayersToGame(players);
        createTiles();
        pickDealer(players);
        setFirstPlayer();
        dealHandToEachPlayer(players);
        giveDealerExtraTile();
        for (Player player : players) actions.findFlowers(game, player);
        checks.lookForSetsToDisplayFirstGo(game.getPlayersInGame());
        game.setOngoing(true);
        game = gameService.saveGame(game);
        logger.info("Game initialised successfully");
        return game;
    }

    /**
     * For creating pass and play/ Bot players for a game
     */
    private List<Player> createPlayersForGame(int botsCount) {
        List<Player> players = new ArrayList<>();

        for (int i = 1; i <= botsCount; i++) {
            players.add(playerService.createPlayer("BOT" + i, true));
        }
        for (int i = 1; i < 4 - botsCount; i++) {
            players.add(playerService.createPlayer("PASSANDPLAY" + i, false));
        }
        return players;
    }

    private void addPlayersToGame(List<Player> players) {
        for (Player player : players) {
            logger.info("Adding player {} to game", player.getUsername());
            gameService.addPlayer(game, player);
        }
    }

    private void createTiles() {
        // Suited (108 total > 3 suits * 9 numbers * 4 copies)
        List<String> suits = List.of("Bamboo", "Dots", "Characters");
        createSubsetOfTiles("Suited", suits, 3, 12);

        // Honor (28 total > 7 types * 4 copies)
        List<String> honors = List.of("North", "South", "East", "West", "Red", "Green", "White");
        createSubsetOfTiles("Honor", honors, 1, 4);

        // Flower (8 total > 2 sets of 4)
        List<String> flowers = List.of("Flower");
        createSubsetOfTiles("Flower", flowers, 4, 1);
    }

    private void createSubsetOfTiles(String type, List<String> suits, Integer totalCount, Integer totalCopies) {
        for (String suit : suits) {
            for (int num = 1; num <= totalCount; num++) {
                for (int copy = 0; copy < totalCopies; copy++) {
                    //if (Objects.equals(type, "Flower")) System.out.println(suits);
                    Tile t = tileService.createTile(type, suit, num, game);
                    gameService.addTile(game, t);
                }
            }
        }
    }

    private void pickDealer(List<Player> players) {
        dealer = players.get(r.nextInt(players.size()));
        game.setDealer(dealer);
    }

    private void setFirstPlayer() {
        game.setCurrentPlayer(dealer);
    }

    private void dealHandToEachPlayer(List<Player> players) {
        for (Player player : players) {
            dealHand(player);
        }
    }

    private void dealHand(Player player) {
        for (int num = 1; num <= 13; num++) {
            h.addRandomTileToPlayer(game, player);
        }
    }

    private void giveDealerExtraTile() {
        playerService.addTileToHand(dealer.getPlayerId(), h.getRandomTile(game));
    }

}
