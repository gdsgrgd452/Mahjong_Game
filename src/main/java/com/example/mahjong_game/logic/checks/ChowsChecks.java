package com.example.mahjong_game.logic.checks;

import com.example.mahjong_game.logic.util.ComparisonHelperFunctions;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Action;
import com.example.mahjong_game.model.tiles.SuitedTile;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.ActionService;
import com.example.mahjong_game.service.PlayerService;
import com.example.mahjong_game.service.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChowsChecks {

    private final TileService tileService;
    private final PlayerService playerService;
    private final ActionService actionService;
    private final HelperFunctions h;
    private final ComparisonHelperFunctions c;
    private static final Logger logger = LoggerFactory.getLogger(ChowsChecks.class);

    @Autowired
    public ChowsChecks(TileService tileService, PlayerService playerService, ActionService actionService, HelperFunctions h, ComparisonHelperFunctions c) {
        this.tileService = tileService;
        this.playerService = playerService;
        this.actionService = actionService;
        this.h = h;
        this.c = c;
    }

    /**
     * Start of game and after random pickup check - scan the entire hand for any valid Chows
     * @return Player - Returns the player if a valid chow is found and saved
     */
    public Player lookForChows(Player player, List<Tile> playerTiles) {
        playerTiles = h.filterListOfTilesForNoAction(playerTiles); //Prevents overlaps with pungs or other chows
        //Sort for only suited tiles here?
        List<Tile> sortedTiles = new ArrayList<>(playerTiles);
        sortedTiles.sort(c.getTileLogicComparator());

        List<List<Tile>> foundChows = new ArrayList<>();
        List<Tile> consumedTiles = new ArrayList<>();

        for (Tile t : sortedTiles) {
            if (consumedTiles.contains(t) || !(t instanceof SuitedTile)) continue; // Already used this tile in another chow

            Optional<Tile> t1 = findTileInList(playerTiles, t.getSuit(), t.getNumber() + 1, consumedTiles);
            Optional<Tile> t2 = findTileInList(playerTiles, t.getSuit(), t.getNumber() + 2, consumedTiles);

            if (t1.isPresent() && t2.isPresent()) {
                List<Tile> chow = new ArrayList<>(List.of(t, t1.get(), t2.get()));
                foundChows.add(chow);
                consumedTiles.add(t);
                consumedTiles.add(t1.get());
                consumedTiles.add(t2.get());
            }
        }
        boolean validChowSaved = false;
        for (List<Tile> chowTiles : foundChows) {
            List<Integer> tileIds = chowTiles.stream().map(Tile::getTileId).toList();
            if (handleChow(player, tileIds)) {
                logger.info("Chow found for player: {}", player.getUsername());
                validChowSaved = true;
            }
        }
        return validChowSaved ? player : null;
    }


    //Just tries to get tiles which would let the new tile fit into a chow instead of looking through the hand
    /**
     * Just finds a chow and returns the tiles inside it, creates/saves nothing
     */
    public List<Tile> lookForChowsAfterDiscard(Player player, Tile tile) {
        if (!(tile instanceof SuitedTile)) return Collections.emptyList();
        List<Tile> playerTiles = player.getCurrentHandNoPlaced();
        playerTiles = h.filterListOfTilesForNoAction(playerTiles);

        //2 before and 1 before, 1 before and 1 after, 1 after and 2 after
        HashMap<Integer, List<Integer>> offsets = new HashMap<>(Map.of(0, List.of(-2, -1), 1, List.of(-1, 1), 2, List.of(1, 2)));
        for (int count = 0; count <= 2; count++) {
            Integer of1 = offsets.get(count).getFirst();
            Integer of2 = offsets.get(count).getLast();
            List<Tile> pattern = findTilesByValue(playerTiles, tile.getSuit(), tile.getNumber() + of1, tile.getNumber() + of2);
            if (!pattern.isEmpty()) {
                pattern.add(tile); // Add the discard to complete it
                logger.info("Chow found for player after discard: {}", player.getUsername());
                return pattern;
            }
        }
        return Collections.emptyList();
    }

    public boolean handleChow(Player player, List<Integer> chowTileIds) {
        List<Tile> chowTiles = chowTileIds.stream().map(tileService::findTileById).toList();
        if (actionService.lookInPlayerForActionWithSameTiles(player, chowTiles, "Chow")) return false;

        Action newChow = actionService.createAction("Chow");
        for (Tile tile : chowTiles) {
            newChow = actionService.addTileToAction(newChow.getId(), tile);
            logger.info("Added tile to chow: {}", tile.getSuit());
        }
        playerService.addAction(player.getPlayerId(), newChow);
        return true;
    }

    //The 2 functions below should be moved to helper functions/ replaced with their overlapping more general functions
    @Deprecated
    private List<Tile> findTilesByValue(List<Tile> playerTiles, String suit, int n1, int n2) {
        Optional<Tile> t1 = findTileInList(playerTiles, suit, n1, new ArrayList<>());
        Optional<Tile> t2 = findTileInList(playerTiles, suit, n2, new ArrayList<>());

        if (t1.isPresent() && t2.isPresent()) {
            return new ArrayList<>(List.of(t1.get(), t2.get()));
        }
        return Collections.emptyList();
    }
    @Deprecated
    private Optional<Tile> findTileInList(List<Tile> tiles, String suit, int number, List<Tile> exclude) {
        return tiles.stream()
                .filter(t -> !exclude.contains(t))
                .filter(t -> Objects.equals(t.getSuit(), suit) && Objects.equals(t.getNumber(), number))
                .findFirst();
    }




}
