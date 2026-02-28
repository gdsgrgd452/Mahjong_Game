package com.example.mahjong_game.logic.util;

import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.PlayerService;
import com.example.mahjong_game.service.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class HelperFunctions {

    private static final Logger logger = LoggerFactory.getLogger(HelperFunctions.class);

    private final Random r = new Random();

    private final TileService tileService;
    private final PlayerService playerService;
    private final ComparisonHelperFunctions c;

    @Autowired
    public HelperFunctions(TileService tileService, PlayerService playerService, ComparisonHelperFunctions c) {
        this.tileService = tileService;
        this.playerService = playerService;
        this.c = c;
    }

    /**
     * @return A random tile from the tiles still in the wall (In the specified game)
     */
    public Tile getRandomTile(Game game) {
        List<Tile> allTiles = tileService.getLiveTiles(game); //Only unused ones
        return allTiles.get(r.nextInt(allTiles.size()));
    }

    /**
     * Adds a specific tile to a player's hand
     * @return The tile that was added
     */
    public Tile addTileToPlayer(Player player, Tile tile) {
        logger.info("Adding specific tile to player: {}", player.getUsername());
        tile.setJustPickedUp(true);
        return playerService.addTileToHand(player.getPlayerId(), tile);
    }

    /**
     * Adds a random tile to a player's hand (Uses the getRandomTile function)
     */
    public Tile addRandomTileToPlayer(Game game, Player player) {
        logger.info("Adding random tile to player: {}", player.getUsername());
        return playerService.addTileToHand(player.getPlayerId(), getRandomTile(game));
    }

    /**
     * Adds the tile to the players current hand no placed
     * @return The new hand with the tile
     */
    public List<Tile> simulateHandWithNewTileForChecks(Player player, Tile tile) {
        List<Tile> playerTiles = new ArrayList<>(player.getCurrentHandNoPlaced());
        playerTiles.add(tile);
        playerTiles.sort(c.getTileLogicComparator());
        return playerTiles;
    }

    /**
     * Iterates through the players in the game to get the next player in the order
     * @return The next player in the order
     */
    public Player iterateThroughListWithLooping(List<Player> players, Player playerTurn) {
        if (players == null || players.isEmpty()) {
            return null;
        }

        int currentIndex = players.indexOf(playerTurn);
        if (currentIndex == -1) currentIndex = 0;
        int nextIndex = (currentIndex + 1) % players.size(); //Ensures looping
        return players.get(nextIndex);
    }

    public List<Tile> filterListOfTilesForNoPungOrChow(List<Tile> currentHand) {
        return currentHand.stream().filter(t -> t.getChow() == null && t.getPung() == null).toList();
    }

    /**
     * Decreases the amount remaining for all tiles with the same suit and number
     * @param amountRevealed Always 1? because you call this every time you reveal a tile (3 times for a pung)
     */
    public void decreaseAmountRemainingForAllSameTiles(Game game, Integer amountRevealed, Tile tile) {
        String tileInfo = tile.getSuit() + "-" + tile.getNumber();
        Map<String, List<Tile>> groupedBySAndN = c.groupTilesBySuitAndNumber(tileService.findAllTilesByGame(game)); // Groups all tiles in the format e.g. <Bamboo-3: [t1, t2, t3]>
        for (Tile t : groupedBySAndN.get(tileInfo)) {
            t.setAmountRemaining(t.getAmountRemaining() - amountRevealed);
            tileService.saveTile(t);
        }
        logger.info("Decreased amount remaining for tiles: {} to: {}", tileInfo, tile.getAmountRemaining());
    }

}
