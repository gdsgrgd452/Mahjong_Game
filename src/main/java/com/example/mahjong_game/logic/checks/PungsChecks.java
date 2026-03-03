package com.example.mahjong_game.logic.checks;

import com.example.mahjong_game.logic.util.ComparisonHelperFunctions;
import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.PlayerService;
import com.example.mahjong_game.service.PungService;
import com.example.mahjong_game.service.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PungsChecks {

    private final HelperFunctions h;
    private final PlayerService playerService;
    private final PungService pungService;
    private final TileService tileService;
    private final ComparisonHelperFunctions c;
    private static final Logger logger = LoggerFactory.getLogger(PungsChecks.class);

    @Autowired
    public PungsChecks(HelperFunctions h, PlayerService playerService, PungService pungService, TileService tileService, ComparisonHelperFunctions c) {
        this.h = h;
        this.playerService = playerService;
        this.pungService = pungService;
        this.tileService = tileService;
        this.c = c;
    }

    public Player lookForPungs(Player player, List<Tile> playerTiles) {
        playerTiles = h.filterListOfTilesForNoPungOrChow(playerTiles); //Prevents overlaps with chows or other pungs
        Map<String, List<Tile>> grouped = c.groupTilesBySuitAndNumber(playerTiles);
        boolean validPungSaved = false;

        for (List<Tile> group : grouped.values()) {
            if (group.size() >= 3) {
                List<Tile> pungTiles = group.subList(0, 3);
                List<Integer> tileIds = pungTiles.stream().map(Tile::getTileId).toList();
                if (handlePung(player, tileIds)) {
                    group.removeAll(pungTiles); //Prevents overlaps
                    logger.info("Pung found for player: {}", player.getUsername());
                    validPungSaved = true;
                }
            }
        }
        return validPungSaved ? player : null;
    }

    /**
     * Just finds a pung and returns the player with it, creates/saves nothing
     */
    public List<Tile> lookForPungsAfterDiscard(Player player, Tile tile) {
        List<Tile> playerTiles = h.simulateHandWithNewTileForChecks(player, tile);
        playerTiles = h.filterListOfTilesForNoPungOrChow(playerTiles);
        List<Tile> matchingTiles = playerTiles.stream().filter(t -> c.sameTypeNumberAndSuitCheck(t, tile)).toList();

        if (matchingTiles.size() >= 3) {
            List<Tile> pungTiles = new ArrayList<>();
            pungTiles.add(matchingTiles.get(0));
            pungTiles.add(matchingTiles.get(1));
            pungTiles.add(tile);
            if (!pungService.lookInPlayerForPungWithSameTiles(player, pungTiles)) {
                logger.info("Pung found for player after discard: {}", player.getUsername());
                return pungTiles;
            }
        }
        return Collections.emptyList();
    }

    public boolean handlePung(Player player, List<Integer> pungTilesIds) {
        List<Tile> pungTiles = pungTilesIds.stream().map(tileService::findTileById).toList();
        if (pungService.lookInPlayerForPungWithSameTiles(player, pungTiles)) {
            return false;
        }
        Pung newPung = pungService.createPung();
        for (Tile tile : pungTiles) newPung = pungService.addTileToPung(newPung.getPungId(), tile);
        playerService.addPung(player.getPlayerId(), newPung);
        return true;
    }
}

