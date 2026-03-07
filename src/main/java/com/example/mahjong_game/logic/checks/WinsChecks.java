package com.example.mahjong_game.logic.checks;

import com.example.mahjong_game.logic.util.ComparisonHelperFunctions;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.SuitedTile;
import com.example.mahjong_game.model.tiles.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WinsChecks {

    private final HelperFunctions h;
    private final ComparisonHelperFunctions c;
    private static final Logger logger = LoggerFactory.getLogger(WinsChecks.class);

    @Autowired
    public WinsChecks(HelperFunctions h, ComparisonHelperFunctions c) {
        this.h = h;
        this.c = c;
    }


    //Broken because you need to check for new pungs and chows before checking for win
    public Player lookForWin(Player player, List<Tile> playerTiles) {

        Integer pungsCount = player.getPungs().size();
        Integer chowsCount = player.getChows().size();
        if (pungsCount + chowsCount != 4) {
            return null; //Quick check for speed
        }
        logger.info("4 pungs/chows: {}", player.getUsername());

        List<Tile> tilesNotInPungOrChow = playerTiles.stream().filter(this::tileIsNotInPungOrChow).toList();

        if (tilesNotInPungOrChow.size() != 2) {
            logger.warn("There isn't 2 tiles not in pung or chow: {}", tilesNotInPungOrChow);
            return null;
        }

        if (checkIfSnakesAreTheSame(tilesNotInPungOrChow)) {
            logger.warn("WIN FOUND for player: {}", player.getUsername());
            return player;
        } else {
            logger.info("No win found for player (Tiles not the same): {}", player.getUsername());
            logger.info("Tiles: {}", tilesNotInPungOrChow);
        }
        return null;
    }

    private boolean checkIfSnakesAreTheSame(List<Tile> tilesNotInPungOrChow) {
        Tile snake1 = tilesNotInPungOrChow.getFirst();
        Tile snake2 = tilesNotInPungOrChow.getLast();

        if (snake1 instanceof SuitedTile) {
            return c.sameTypeNumberAndSuitCheck(snake1, snake2); //If they are suited (Have number) then compare with that
        } else {
            return c.sameTypeAndSuitCheck(snake1, snake2);
        }
    }

    //Needs to be moved to being called after
    public Player lookForWinAfterDiscard(Player player, Tile tile) {
        List<Tile> playerTiles = new ArrayList<>(h.simulateHandWithNewTileForChecks(player, tile));
        if (lookForWin(player, playerTiles) != null) return player;
        return null;
    }

    private boolean tileIsNotInPungOrChow(Tile tile) {
        return (tile.getChow() == null) && (tile.getPung() == null);
    }

}

