package com.example.mahjong_game.logic;

import com.example.mahjong_game.logic.checks.Checks;
import com.example.mahjong_game.logic.checks.ChowsChecks;
import com.example.mahjong_game.logic.checks.PungsChecks;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class Discard {

    private final TileService tileService;
    private final GameService gameService;
    private final Actions actions;
    private final HelperFunctions h;
    private final PungService pungService;
    private final ChowService chowService;
    private final Checks checks;
    private final PungsChecks pungsChecks;
    private final ChowsChecks chowsChecks;
    private static final Logger logger = LoggerFactory.getLogger(Discard.class);

    @Autowired
    public Discard(TileService tileService, GameService gameService, Actions actions, HelperFunctions h, PungService pungService, ChowService chowService, Checks checks, PungsChecks pungsChecks, ChowsChecks chowsChecks) {
        this.tileService = tileService;
        this.gameService = gameService;
        this.actions = actions;
        this.h = h;
        this.pungService = pungService;
        this.chowService = chowService;
        this.pungsChecks = pungsChecks;
        this.chowsChecks = chowsChecks;
        this.checks = checks;
    }

    /**
     * Called from the controller after a discard call is made <br>
     * Handles everything from when a player attempts a discard to just after when a new player is swapped to and a new tile is added to their hand
     */
    public void discardTile(Integer tileId) {   //Function called closest to the players interface
        Game game = gameService.findFirstGame();
        Tile tile = tileService.findTileById(tileId);
        removeJustDiscardedTileFromAction(tile);

        //Re get tile
        tile = tileService.findTileById(tileId);
        if (tile.getPung() != null || tile.getChow() != null) {
            return;
        }

        unSetNewJustDiscardedTile(game, tile); //Ensures the newly discarded one is the only marked one, calls the discard method

        //Before you swap turns un mark the newly picked up ones
        List<Tile> previousPlayerTiles = game.getCurrentPlayer().getCurrentHandNoPlaced();
        List<Tile> previousPlayerNewTiles = previousPlayerTiles.stream().filter(Tile::isJustPickedUp).toList();
        for (Tile previousPlayerNewTile : previousPlayerNewTiles) previousPlayerNewTile.setJustPickedUp(false);


        logger.info("Player: {} discarded tile: Suit: {}, Number: {}", game.getCurrentPlayer().getUsername(), tile.getSuit(), tile.getNumber());
        gameService.saveGame(game);
    }

    /**
     * Called when a valid action (Pung, chow ect) is NOT found <br>
     * Swaps to the next player's turn, gives them a tile from the wall then handles if it is a flower
     * Then re checks for new wins, pungs, chows due to picking up
     */
    public void whenNoActionsFound(Game game) {
        Player playerTurn = actions.swapToNextPlayersTurn(game, game.getCurrentPlayer());
        Tile newTileFromWall = h.addRandomTileToPlayer(game, playerTurn); //Here since they are just the next in the list they can pick from the wall
        newTileFromWall.setJustPickedUp(true);
        actions.findFlowers(game, playerTurn);
        checks.lookForSetsToDisplayAfterPickup(playerTurn);
        gameService.saveGame(game);
    }

    /**
     * Called when a valid action (Pung, chow ect) is found and the player has clicked the button <br>
     * Swaps to the player's turn who has a valid action and gives them the tile. <br>
     * Also handles stuff like displaying the set
     * @param playerWithAction Player who picked up the tile because they have a valid action
     * @param actionType The type of action "P"-Pung or "C"-Chow
     */
    public void whenSomeActionsFound(Game game, Player playerWithAction, String actionType) {

        Tile tile = tileService.getJustDiscardedTile(game);

        if (Objects.equals(actionType, "P")) {
            pungsChecks.handlePung(playerWithAction, playerWithAction.getTilesInActionToTake());
        } else if (Objects.equals(actionType, "C")) {
            chowsChecks.handleChow(playerWithAction, playerWithAction.getTilesInActionToTake());
        }
        playerWithAction.setActionToTake(null);
        Player playerTurn = actions.swapToPlayerWithAction(game, playerWithAction);
        tile.setJustDiscarded(false);
        Tile newTileFromOtherPlayer = h.addTileToPlayer(playerTurn, tile);
        if (Objects.equals(actionType, "W")) {
            displayAllAfterWin(playerWithAction.getCurrentHandNoPlaced());
            playerWithAction.setWins(playerWithAction.getWins() + 1);
        } else {
            displaySetAfterAction(newTileFromOtherPlayer, playerWithAction.getTilesInActionToTake());
        }
    }

    /**
     * When a player wins this displays all their tiles
     */
    private void displayAllAfterWin(List<Tile> tiles) {
        for (Integer tileId : tiles.stream().map(Tile::getTileId).toList()) {
            Tile tile = tileService.findTileById(tileId);
            tile.setPlaced(true);
            tileService.saveTile(tile);
        }
    }

    /**
     * After a player completes a pung or chow by picking up a discard, this sets the pung/chow tiles to placed (visible to other players)
     */
    private void displaySetAfterAction(Tile newTile, List<Integer> tileIdsInActin) {
        newTile.setPlaced(true);
        tileService.saveTile(newTile);
        for (Integer tId : tileIdsInActin) {
            Tile t = tileService.findTileById(tId);
            t.setPlaced(true);
            h.decreaseAmountRemainingForAllSameTiles(gameService.findFirstGame(), 1, t); //Other players/bots now know that one is not available
            tileService.saveTile(t);
        }
    }

    /**
     * Sets any tiles in the game where justDiscarded = true to false <br>
     * Sets the newly discarded tile's justDiscarded to true
     * Decrements the amount of the tile remaining hidden by 1
     * @param tile Tile that has just been discarded
     */
    private void unSetNewJustDiscardedTile(Game game, Tile tile) {

        //This is inefficient, find a better way without finding all tiles (Store somewhere maybe in game?)

        List<Tile> justDiscardedTiles = tileService.findAllTilesByGame(game).stream().filter(Tile::isJustDiscarded).toList();
        for (Tile justDiscardedTile : justDiscardedTiles) {
            h.decreaseAmountRemainingForAllSameTiles(game, 1, justDiscardedTile);
            justDiscardedTile.setJustDiscarded(false);
            tileService.saveTile(justDiscardedTile);
        }
        tileService.discardTile(tile); //Move this up a level?
    }


    /**
     * Gets rid of a pung or chow if a constituent tile is discarded
     */
    private void removeJustDiscardedTileFromAction(Tile tile) {
        Pung pung = tile.getPung();
        if (pung != null) {
            List<Tile> tilesInPung = new ArrayList<>(pungService.findPungById(pung.getPungId()).getTiles());
            for (Tile tileInPung : tilesInPung) {
                tileService.removeFromPung(tileInPung);
            }
            pungService.deletePung(pung.getPungId());
        }
        Chow chow = tile.getChow();
        if (chow != null) {
            List<Tile> tilesInChow = new ArrayList<>(chowService.findChowById(chow.getChowId()).getTiles());
            for (Tile tileInChow : tilesInChow) {
                tileService.removeFromChow(tileInChow);
            }
            chowService.deleteChow(chow.getChowId());
        }

        //Should re check for pungs/chows? e.g. [3,3,3],4,5 throw out 3 and the pung disappears does it find the new chow?
    }

}
