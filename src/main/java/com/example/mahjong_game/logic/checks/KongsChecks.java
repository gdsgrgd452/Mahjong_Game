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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class KongsChecks {

    private final HelperFunctions h;
    private final ComparisonHelperFunctions c;
    private final ActionService actionService;
    private final TileService tileService;
    private final PlayerService playerService;
    private static final Logger logger = LoggerFactory.getLogger(KongsChecks.class);

    @Autowired
    public KongsChecks(HelperFunctions h, ComparisonHelperFunctions c, ActionService actionService, TileService tileService, PlayerService playerService) {
        this.h = h;
        this.c = c;
        this.actionService = actionService;
        this.tileService = tileService;
        this.playerService = playerService;
    }

    public Player lookForKongs(Player player, List<Tile> currentHand) {
        List<Action> pungs = actionService.findPlayersActionsByType(player, "Pung");
        logger.info("Found {} pungs when trying to find kongs (Start/Pickup)", pungs.size());

        for (Action pung : pungs) {
            Tile pungFirstTile = pung.getTiles().getFirst();

            if (pungFirstTile instanceof SuitedTile) {
                String pungInfo = pungFirstTile.getSuit() + "-" + pungFirstTile.getNumber(); //Gets e.g. Bamboo-7
                Map<String, List<Tile>> tilesGroupedBySuit = c.groupTilesBySuitAndNumber(currentHand); // Groups all tiles in the format e.g. <Bamboo-7: [t1, t2, t3]>

                if (tilesGroupedBySuit.containsKey(pungInfo)) {
                    List<Integer> tileIds = new ArrayList<>(pung.getTiles().stream().map(Tile::getTileId).toList());
                    tileIds.add(tilesGroupedBySuit.get(pungInfo).getFirst().getTileId());
                    handleKong(player, tileIds);
                }


            } else {
                String pungInfo = pungFirstTile.getSuit();
                Map<String, List<Tile>> tilesGroupedBySuit = c.groupTilesBySuit(currentHand);

                if (tilesGroupedBySuit.containsKey(pungInfo)) {

                }
            }
        }
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
