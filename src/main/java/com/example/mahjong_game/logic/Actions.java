package com.example.mahjong_game.logic;

import com.example.mahjong_game.logic.util.HelperFunctions;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.FlowerTile;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.service.GameService;
import com.example.mahjong_game.service.TileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;


@Service
public class Actions {

    private final TileService tileService;
    private final GameService gameService;
    private final HelperFunctions h;
    private static final Logger logger = LoggerFactory.getLogger(Actions.class);

    @Autowired
    public Actions(TileService tileService, GameService gameService, HelperFunctions h) {
        this.tileService = tileService;
        this.gameService = gameService;
        this.h = h;
    }

    public Player swapToPlayerWithAction(Game game, Player player) {
        logger.info("Swapping turn to player with action: {}", player.getUsername());
        game.setCurrentPlayer(player);
        game = gameService.saveGame(game);
        return game.getCurrentPlayer();
    }

    public Player swapToNextPlayersTurn(Game game, Player currentPlayer) {
        Player nextPlayer = h.iterateThroughListWithLooping(game.getPlayersInGame(), currentPlayer);
        logger.info("Swapping turn from {} to next player: {}", currentPlayer.getUsername(), nextPlayer.getUsername());
        game.setCurrentPlayer(nextPlayer);
        game = gameService.saveGame(game);
        return game.getCurrentPlayer();
    }

    /**
     * Gets the flowers in the hand that are not placed (including placed causes duplicate pickups)
     */
    private List<Tile> filterForFlowersAndNotPlaced(List<Tile> tiles) {
        List<Tile> flowersInHand = tiles.stream().filter(FlowerTile.class::isInstance).toList();
        return flowersInHand.stream().filter(t -> !t.isPlaced()).toList();
    }

    public void findFlowers(Game game, Player player) {
        List<Tile> flowersInHandNotPlaced = filterForFlowersAndNotPlaced(player.getCurrentHand());

        if (!flowersInHandNotPlaced.isEmpty()) {
            for (Tile flower : flowersInHandNotPlaced) handleFoundFlower(game, player, flower);
            List<Tile> reSortedHand = player.getCurrentHand();
            reSortedHand.sort(Comparator.comparingInt(Tile::getTileId)); //Re sorts the hand so the tiles replacing flowers aren't at the end
            player.setCurrentHand(reSortedHand);
        }
    }

    private void handleFoundFlower(Game game, Player player, Tile flower) {
        logger.info("Flower found for player: {} ({} {})", player.getUsername(), flower.getSuit(), flower.getNumber());
        tileService.placeTile(flower);
        Tile newTileFromWall = h.addRandomTileToPlayer(game, player); //New tile to replace flower
        newTileFromWall.setJustPickedUp(true);
        if (newTileFromWall instanceof FlowerTile) {
            handleFoundFlower(game, player, newTileFromWall);
        }
    }

}
