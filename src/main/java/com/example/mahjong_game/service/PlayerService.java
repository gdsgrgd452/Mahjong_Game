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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository; //The player repository for interacting with the database
    public PlayerService(PlayerRepository playerRepository) { //Initialises the user Repository
        this.playerRepository = playerRepository;
    }

    public Player savePlayer(Player player) {
        try {
            playerRepository.save(player);
            return player;
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to save an existing player", e);
        }
    }

    public List<Player> findAllPlayers() {
        try {
            return playerRepository.findAll(); //Needs to be updated at some point
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to get any players from database: ", e);
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

    public Player addPung(Integer playerId, Pung newPung) {
        try {
            Player player = findPlayerById(playerId);
            player.addPung(newPung);
            player = playerRepository.save(player);
            return player;
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

    public Player addChow(Integer playerId, Chow newChow) {
        try {
            Player player = findPlayerById(playerId);
            player.addChow(newChow);
            player = playerRepository.save(player);
            return player;
        } catch (Exception e) {
            throw new PlayerActionFailedException("Failed to add chow to player", e);
        }
    }
}
