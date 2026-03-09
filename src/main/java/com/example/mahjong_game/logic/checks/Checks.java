package com.example.mahjong_game.logic.checks;

import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Checks {

    private final PungsChecks pungsChecks;
    private final ChowsChecks chowsChecks;
    private final WinsChecks winsChecks;
    private final HelperFunctions h;
    private static final Logger logger = LoggerFactory.getLogger(Checks.class);

    @Autowired
    public Checks(PungsChecks pungsChecks, ChowsChecks chowsChecks, WinsChecks winsChecks, HelperFunctions h) {
        this.pungsChecks = pungsChecks;
        this.chowsChecks = chowsChecks;
        this.winsChecks = winsChecks;
        this.h = h;
    }

    public void lookForSetsToDisplayFirstGo(List<Player> players) {
        Player winnerAtStart;
        for (Player player : players) {
            winnerAtStart = lookForWinInHand(player);
            if (winnerAtStart != null) {
                return;
            }
            lookForPungsInHand(player);
            lookForChowsInHand(player);
        }
    }

    public void lookForSetsToDisplayAfterPickup(Player player) {
        if (lookForWinInHand(player) != null) logger.info("Win found after pickup: {}", player.getUsername());
        if (lookForPungsInHand(player) != null) logger.info("Pung found in hand after pickup: {}", player.getPungs());
        if (lookForChowsInHand(player) != null) logger.info("Chow found in hand after pickup: {}", player.getChows());
    }

    private Player lookForWinInHand(Player player) {
        return winsChecks.lookForWin(player, player.getCurrentHandNoPlaced());
    }

    private Player lookForPungsInHand(Player player) {
        return pungsChecks.lookForPungs(player, player.getCurrentHandNoPlaced());
    }

    private Player lookForChowsInHand(Player player) {
        return chowsChecks.lookForChows(player, player.getCurrentHandNoPlaced());
    }

    /**
     * Just finds and returns possible actions to show to the players that they can take <br>
     * Should not update anything
     */
    public Map<Player, String> lookForActionsAfterDiscard(List<Player> players, Player currentPlayer, Tile tile) {

        Map<Player, String> playerAndAction = new HashMap<>();


        List<Player> playersCopy = new ArrayList<>(players);
        for (Player player : playersCopy) {
            player.setActionToTake(null); //No actions to take as about to re calculate new ones
            player.setTilesInActionToTake(null);
        }
        Player nextPlayer = h.iterateThroughListWithLooping(playersCopy, currentPlayer);
        playersCopy.remove(currentPlayer);

        //Look for wins here (1st priority) - Currently not working
        for (Player player : playersCopy) {
            Player winningPlayer = winsChecks.lookForWinAfterDiscard(player, tile);
            if (winningPlayer != null) {
                playerAndAction.put(winningPlayer, "W");
                return playerAndAction;
            }
        }

        //Then check for pungs (2nd priority)
        for (Player player : playersCopy) {
            List<Tile> foundPung = pungsChecks.lookForPungsAfterDiscard(player, tile);
            if (!foundPung.isEmpty()) {
                List<Integer> tileIds = foundPung.stream().map(Tile::getTileId).toList();
                player.setTilesInActionToTake(tileIds);
                playerAndAction.put(player, "P");
                return playerAndAction;
            }
        }

        //Then check for chows (3rd priority)
        List<Tile> foundChow = chowsChecks.lookForChowsAfterDiscard(nextPlayer, tile);
        if (!foundChow.isEmpty()) {
            List<Integer> tileIds = foundChow.stream().map(Tile::getTileId).toList();
            nextPlayer.setTilesInActionToTake(tileIds);
            playerAndAction.put(nextPlayer, "C");
            return playerAndAction;
        }

        logger.info("No actions found after discard");
        return Collections.emptyMap();
    }
}
