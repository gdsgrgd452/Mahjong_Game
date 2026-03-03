package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.GameCreationFailedException;
import com.example.mahjong_game.exception.GetFromDatabaseFailedException;
import com.example.mahjong_game.exception.PlayerActionFailedException;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository; //The player repository for interacting with the database
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public PlayerService(PlayerRepository playerRepository) { //Initialises the user Repository
        this.playerRepository = playerRepository;
    }

    //Should have something to signify temp so it gets rid of ones without accounts after a game
    public Player createPlayer(String username, boolean isBot) {
        try {
            Player player = new Player();
            player.setUsername(username);
            player.setBot(isBot);
            player.setGame(null);
            player.setWins(0);
            player = playerRepository.save(player);
            logger.info("Player created: {}, is bot: {}", player.getUsername(), player.isBot());
            return player;
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to initialize a new player", e);
        }
    }

    public void savePlayer(Player player) {
        try {
            playerRepository.save(player);
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to save an existing player", e);
        }
    }

    public List<Player> findAllPlayersByGame(Game game) {
        try {
            return playerRepository.findAllByGame(game); //Needs to be updated at some point
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to get players from database for game id: ", e);
        }
    }

    public Player findPlayerById(Integer playerId) {
        try {
            return playerRepository.findById(playerId).orElseThrow(() -> new GetFromDatabaseFailedException("Player not found with ID: " + playerId));
        } catch (GetFromDatabaseFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to find player with ID: " + playerId, e);
        }
    }

    public Player findPlayerByUsername(String username) {
        try {
            return playerRepository.findByUsername(username);
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to find player with username: " + username, e);
        }
    }

    public Player findPlayerWithActionToTake() {
        try {
            return playerRepository.findPlayerByActionToTakeIsNotNull();
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to find player with action to take", e);
        }
    }

    public Tile addTileToHand(Integer playerId, Tile tile) {
        try {
            Player player = findPlayerById(playerId);
            player.addTile(tile);
            playerRepository.save(player);
            return tile;
        } catch (Exception e) {
            throw new PlayerActionFailedException("Failed to add tile to player's hand", e);
        }
    }

    public void removeTileFromHand(Integer playerId, Tile tile) {
        try {
            Player player = findPlayerById(playerId);
            player.removeTile(tile);
            playerRepository.save(player);
        } catch (Exception e) {
            throw new PlayerActionFailedException("Failed to remove tile from player's hand", e);
        }
    }

    public void addPung(Integer playerId, Pung newPung) {
        try {
            Player player = findPlayerById(playerId);
            player.addPung(newPung);
            playerRepository.save(player);
        } catch (Exception e) {
            throw new PlayerActionFailedException("Failed to add pung to player", e);
        }
    }

    //Consistent
    public void removePungFromPlayer(Integer playerId, Pung newPung) {
        try {
            Player player = findPlayerById(playerId);
            player.removePung(newPung);
            playerRepository.save(player);
        } catch (Exception e) {
            throw new PlayerActionFailedException("Failed to remove pung from player", e);
        }
    }

    public void addChow(Integer playerId, Chow newChow) {
        try {
            Player player = findPlayerById(playerId);
            player.addChow(newChow);
            playerRepository.save(player);
        } catch (Exception e) {
            throw new PlayerActionFailedException("Failed to add chow to player", e);
        }
    }
}
