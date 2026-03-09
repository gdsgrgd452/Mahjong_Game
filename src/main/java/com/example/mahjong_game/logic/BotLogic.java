package com.example.mahjong_game.logic;

import com.example.mahjong_game.logic.util.ComparisonHelperFunctions;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BotLogic {

    private final HelperFunctions h;
    private final ComparisonHelperFunctions c;
    private static final Logger logger = LoggerFactory.getLogger(BotLogic.class);
    private final TileService tileService;

    @Autowired
    public BotLogic(HelperFunctions h, ComparisonHelperFunctions c, TileService tileService) {
        this.c = c;
        this.h = h;
        this.tileService = tileService;
    }

    /**
     * As of present returns a tile in the hand (not in a pung or chow) with the lowest amount remaining
     */
    public Tile discardATileWithLogic(Player player) {
        List<Tile> currentHand = player.getCurrentHandNoPlaced();
        List<Tile> currentHandFiltered = h.filterListOfTilesForNoPungOrChow(currentHand);

        Map<Tile, Integer> priorityMap = new HashMap<>();
        for (Tile t : currentHandFiltered) {
            priorityMap.put(t, 10);
        }

        //logger.info("Bot priority map: {}", priorityMap);

        List<Tile> currentHandSortedByAmountRemaining = currentHandFiltered.stream().sorted(Comparator.comparingInt(Tile::getAmountRemaining)).toList();
        int leastAmountRemaining = currentHandSortedByAmountRemaining.getFirst().getAmountRemaining();
//
//        for (Tile t : currentHandFiltered) {
//
//        }
        
        //For checking for potential pungs
        if (leastAmountRemaining == 0) { //If there is no other tiles remaining of the same then get rid of it
            return currentHandSortedByAmountRemaining.getFirst();
        }

        List<Tile> tilesWithMinimumRemaining = currentHandSortedByAmountRemaining.stream().filter(t -> t.getAmountRemaining() == leastAmountRemaining).toList();

        //logger.info("Bot tiles with minimum remaining: {}", tilesWithMinimumRemaining);

        logBotChoices(currentHand, currentHandSortedByAmountRemaining);
        return currentHandSortedByAmountRemaining.getFirst();
    }
    
    private void lookForPotentialPungs(List<Tile> tiles) {
        Map<String, List<Tile>> tilesGroupedBySuit = c.groupTilesBySuitAndNumber(tiles);
        for (List<Tile> group : tilesGroupedBySuit.values()) {
            if (group.size() == 2 && group.getFirst().getAmountRemaining() == 0) {
                //There cant be a pung so increase priority of discarding
            }
        }
    }

    // Should -> Go through tile in list and check if there is one with a correct offset e.g. (-2, -1)

    //If there is one in the hand then add to the map
    // If there is then look for amount remaining of the other tile
    // If there isnt then dont change the priority of the tile (Or - ?)

    // Start at 0 value -> +1 if there is 3 remaining, +0.5 if there is 2, + 0.25 if there is 1, -1 if there is 0
    // Should end up as between -4 and 4

    /**
     * Gets only suited tiles and groups them by suit e.g. bamboo: [t1, t2]
     */
    private void lookForPotentialChows(List<Tile> tiles) {
        tiles = c.getOnlyHonorSuitedOrFlowerTiles(tiles, "Suited");
        Map<String, List<Tile>> tilesGroupedBySuit = c.groupTilesBySuit(tiles);
        for (List<Tile> group : tilesGroupedBySuit.values()) {
            lookForTileWithNumberNeighbour(group);
        }
    }

    /**
     * @param tiles A group of suited tiles e.g. bamboo: [t1, t2]
     */
    private void lookForTileWithNumberNeighbour(List<Tile> tiles) {

        Map<Tile, Integer> tilesAndScore = new HashMap<>();
        for (Tile tile : tiles) {
            tilesAndScore.put(tile, 0);
        }

        HashMap<Integer, List<Integer>> offsets = new HashMap<>(Map.of(0, List.of(-2, -1), 1, List.of(-1, 1), 2, List.of(1, 2)));

        HashMap<Integer, Float> scoreBasedOnAmountRemaining = new HashMap<>(Map.of(0, -1f, 1, 0.5f, 2, 0.75f, 3, 1f));

        for (Tile tile : tiles) {
            int tileNumber = tile.getNumber();
            for (int count = 0; count <= 2; count++) {
                int offsetInHand = offsets.get(count).getFirst(); //Should be updated later to do both ways

                Tile neighbour = checkIfTileWithNumberIsInList(tiles, tileNumber + offsetInHand);
                if (neighbour != null) {

                    int otherOffsetNotInHand = offsets.get(count).getFirst();

                    //Get the first tile with the suit and number of the missing one -> get the amount remaining
                    // Then score based on amount remaining
                    //int newTileNumber = tileService.findTileBySuitAndNumber()


                    tilesAndScore.put(tile, tilesAndScore.get(tile) + 1);
                }

            }
        }
    }

    private Tile checkIfTileWithNumberIsInList(List<Tile> tiles, int tileNumber) {
        return tiles.stream().filter(t -> t.getNumber() == tileNumber).findFirst().orElse(null);
    }
//
//    private void lookForTileWithNN() {
//        HashMap<Integer, List<Integer>> offsets = new HashMap<>(Map.of(0, List.of(-2, -1), 1, List.of(-1, 1), 2, List.of(1, 2)));
//        Integer offsetInHand;
//        int possibleChowCount = 4;
//        for (int count = 0; count <= 2; count++) {
//            for (int firstLast = 0; firstLast <= 1; firstLast++)
//                offsetInHand = offsets.get(count).get(firstLast); //E.g with 0 gets -2 then -1
//
//        }
//    }
//


    //For logging only (No functionality)
    public void logBotChoices(List<Tile> currentHand, List<Tile> currentHandSortedByAmountRemaining) {
        Map<String, List<Tile>> currentHandForOut = c.groupTilesBySuitAndNumber(currentHand);
        List<Integer> currentHandFilteredForOut = currentHandSortedByAmountRemaining.stream().map(Tile::getAmountRemaining).toList();
        logger.info("Bot hand: {}", currentHandForOut);
        logger.info("Bot hand filtered: {}", currentHandFilteredForOut);
    }
}
