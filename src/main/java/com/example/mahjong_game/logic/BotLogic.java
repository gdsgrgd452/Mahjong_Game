package com.example.mahjong_game.logic;

import com.example.mahjong_game.logic.util.ComparisonHelperFunctions;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
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

    @Autowired
    public BotLogic(HelperFunctions h, ComparisonHelperFunctions c) {
        this.c = c;
        this.h = h;
    }

    /**
     * As of present returns a tile in the hand (not in a pung or chow) with the lowest amount remaining
     */
    public Tile discardATileWithLogic(Game game) {
        Player player = game.getCurrentPlayer();
        List<Tile> currentHand = player.getCurrentHandNoPlaced();
        List<Tile> currentHandFiltered = h.filterListOfTilesForNoAction(currentHand);


        Map<Tile, Integer> priorityMap = new HashMap<>();
        for (Tile t : currentHandFiltered) {
            priorityMap.put(t, 5);
        }


        Map<Tile, Integer> priorityMapChanges = lookForPotentialPungs(currentHandFiltered);
        //logger.info("Priority map changes (P): {}", priorityMapChanges.values());
        for (Map.Entry<Tile, Integer> entry : priorityMapChanges.entrySet()) {
            priorityMap.put(entry.getKey(), priorityMap.get(entry.getKey()) + entry.getValue());
        }
        //logger.info("Bot priority map before potential chow checks: {}", priorityMap.values());

//        priorityMapChanges = lookForPotentialChows(game, currentHandFiltered);
//        logger.info("Priority map changes (C): {}", priorityMapChanges.values());
//        for (Map.Entry<Tile, Integer> entry : priorityMapChanges.entrySet()) {
//            priorityMap.put(entry.getKey(), priorityMap.get(entry.getKey()) + entry.getValue());
//        }
//        logger.info("Bot priority map after potential chow checks: {}", priorityMap.values());


        List<Tile> currentHandSortedByPriorityMap = currentHandFiltered.stream().sorted(Comparator.comparingInt(priorityMap::get)).toList();
        //logger.info("Bot hand sorted by priority map: {}", currentHandSortedByPriorityMap);

        return currentHandSortedByPriorityMap.getFirst();
    }
    
    private Map<Tile, Integer> lookForPotentialPungs(List<Tile> tiles) {
        Map<String, List<Tile>> tilesGroupedBySuit = c.groupTilesBySuitAndNumber(tiles);
        Map<Tile, Integer> priorityMapChanges = new HashMap<>();
        for (List<Tile> group : tilesGroupedBySuit.values()) {
            if (group.size() == 2) { // If larger: is a pung, smaller is not close to a pung
                int amountRemaining = group.getFirst().getAmountRemaining();
                for (Tile t : group) {
                    //logger.info("Amount remaining for tile: {}", t.getAmountRemaining());
                    priorityMapChanges.put(t,
                            switch (amountRemaining - 2) { // -2 because there is 2 in hand
                                case 0 -> -3; //Impossible to get a pung
                                case 1 -> 2; //Still possible but not too likely
                                case 2 -> 3; //Best odds of getting a pung
                                default -> 0; //Neutral (1)
                            }
                    );
                }
            }
        }
        return priorityMapChanges;
    }

    // Should -> Go through tile in list and check if there is one with a correct offset e.g. (-2, -1)

    //If there is one in the hand then add to the map
    // If there is then look for amount remaining of the other tile
    // If there isnt then dont change the priority of the tile (Or - ?)

    // Start at 0 value -> +1 if there is 3 remaining, +0.5 if there is 2, + 0.25 if there is 1, -1 if there is 0
    // Should end up as between -4 and 4


    private Map<Tile, Integer> lookForPotentialChows(Game game, List<Tile> tiles) {
        tiles = c.getOnlyHonorSuitedOrFlowerTiles(tiles, "Suited"); //Only suited as looking for chows (need number)
        Map<String, List<Tile>> tilesGrouped = c.groupTilesBySuit(tiles);
        Map<Tile, Integer> priorityMapChanges = new HashMap<>();

        for (List<Tile> group : tilesGrouped.values()) {
            Map<Tile, Integer> tilesAndScore = assignValueToPotentialChowTilesBasedOnAmountOfMissingOne(game, group);
            priorityMapChanges.putAll(tilesAndScore);
        }
        return priorityMapChanges;
    }

    /**
     * @param tiles A list of tiles which are all the same suit
     */
    private Map<Tile, Integer> assignValueToPotentialChowTilesBasedOnAmountOfMissingOne(Game game, List<Tile> tiles) {
        Map<Tile, Integer> tilesAndScore = new HashMap<>();
        String suit = tiles.getFirst().getSuit();
        Map<Integer, List<Integer>> offsets = new HashMap<>(Map.of(0, List.of(-2, -1), 1, List.of(-1, 1), 2, List.of(1, 2)));
        Map<Integer, Integer> scoreBasedOnAmountRemaining = new HashMap<>(Map.of(0, -3, 1, 2, 2, 3, 3, 5)); //The more "nearby" tiles there are the higher the score

        for (Tile tile : tiles) {
            int tileNumber = tile.getNumber();
            for (int count = 0; count <= 2; count++) {
                List<Integer> offsetsForCount = offsets.get(count);

                for (int firstLast = 0; firstLast <= 1; firstLast++) { // Ensures -1 -> 1 and 1 -> -1 are both checked
                    Tile neighbour = c.checkIfTileWithNumberIsInList(tiles, tileNumber + offsetsForCount.get(firstLast));
                    if (neighbour != null) { //If one of the tiles in the group has the same number as a tile in the hand
                        //logger.info("Found tile: {} in hand, started at {}", neighbour.getNumber(), tileNumber);
                        int firstLastOther = firstLast == 0 ? 1 : 0; //Get the other tile in the group
                        int amountOfOtherRemaining = h.howManyOfTileInPlay(game, suit, tileNumber + offsetsForCount.get(firstLastOther));
                        //logger.info("Amount of other remaining: {}", amountOfOtherRemaining);
                        int sameOfCurrentInHand = c.findTilesWithNumberInList(tiles, tileNumber).size() - 1; // Accounts for the ones in the bots hand (-1 to account for self)
                        int scoreFromMap = scoreBasedOnAmountRemaining.get(amountOfOtherRemaining - sameOfCurrentInHand);
                        //BREAKING ON LINE ABOVE UNKNOWN REASONS
                        //logger.info("Score from map: {}", scoreFromMap);
                        tilesAndScore.put(tile, scoreFromMap);
                        //logger.info("Score for tile: {} is: {}", tile.getSuit(), tilesAndScore.get(tile));
                    }
                }
            }
        }
        return tilesAndScore;
    }

}
