package com.example.mahjong_game.logic.checks;

import com.example.mahjong_game.logic.util.ComparisonHelperFunctions;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KongsChecks {

    private final ComparisonHelperFunctions c;
    private final ActionService actionService;
    private final TileService tileService;
    private final PlayerService playerService;
    private static final Logger logger = LoggerFactory.getLogger(KongsChecks.class);

    @Autowired
    public KongsChecks(ComparisonHelperFunctions c, ActionService actionService, TileService tileService, PlayerService playerService) {
        this.c = c;
        this.actionService = actionService;
        this.tileService = tileService;
        this.playerService = playerService;
    }

    public Player lookForKongs(Player player, List<Tile> currentHand) {
        boolean validKongSaved = false;
        List<Action> pungs = actionService.findPlayersActionsByType(player, "Pung");
        logger.info("Found {} pungs when trying to find kongs (Start/Pickup)", pungs.size());

        for (Action pung : pungs) {
            Tile pungFirstTile = pung.getTiles().getFirst();

            if (pungFirstTile instanceof SuitedTile) {
                String pungInfo = pungFirstTile.getSuit() + "-" + pungFirstTile.getNumber(); //Gets e.g. Bamboo-7
                Map<String, List<Tile>> tilesGrouped = c.groupTilesBySuitAndNumber(currentHand); // Groups all tiles in the format e.g. <Bamboo-7: [t1, t2, t3]
                List<Integer> kongIds = findTileToAddToKong(tilesGrouped, pung, pungInfo);
                if (tryHandleKong(player, kongIds)) {
                    tilesGrouped.remove(pungInfo); //Prevents overlaps
                    validKongSaved = true;
                }
            } else {
                String pungInfo = pungFirstTile.getSuit();
                Map<String, List<Tile>> tilesGrouped = c.groupTilesBySuit(currentHand);
                List<Integer> kongIds = findTileToAddToKong(tilesGrouped, pung, pungInfo);
                if (tryHandleKong(player, kongIds)) {
                    tilesGrouped.remove(pungInfo); //Prevents overlaps
                    validKongSaved = true;
                }
            }
        }
        return validKongSaved ? player : null;
    }

    public List<Integer> findTileToAddToKong(Map<String, List<Tile>> tilesGroupedBySuit, Action pung, String pungInfo) {
        if (tilesGroupedBySuit.containsKey(pungInfo)) { //If there is another tile with the same suit and number
            List<Integer> tileIds = new ArrayList<>(pung.getTiles().stream().map(Tile::getTileId).toList());
            tileIds.add(tilesGroupedBySuit.get(pungInfo).getFirst().getTileId());
            return tileIds;
        }
        return Collections.emptyList();
    }

    /**
     * Actually saves the kong
     */
    public boolean tryHandleKong(Player player, List<Integer> kongIds) {
        if (!kongIds.isEmpty() && handleKong(player, kongIds)) { //Actually saves
                logger.info("Kong found for player: {}", player.getUsername());
                return true;
            }
        return false;
    }

    public boolean handleKong(Player player, List<Integer> kongTilesIds ) {
        List<Tile> kongTiles = kongTilesIds.stream().map(tileService::findTileById).toList();

        if (actionService.lookInPlayerForActionWithSameTiles(player, kongTiles, "Kong")) {
            return false;
        }
        Action newKong = actionService.createAction("Kong");
        for (Tile tile : kongTiles) {
            logger.info("Added tile to Kong: {}", tile.getSuit());
            newKong = actionService.addTileToAction(newKong.getId(), tile);
        }
        playerService.addAction(player.getPlayerId(), newKong);
        return true;
    }

}
