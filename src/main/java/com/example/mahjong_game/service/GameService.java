package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.GameCreationFailedException;
import com.example.mahjong_game.exception.GetFromDatabaseFailedException;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository gameRepository; //The game repository for interacting with the database
    public GameService(GameRepository gameRepository) { //Initialises the user Repository
        this.gameRepository = gameRepository;
    }

    public Game saveGame(Game game) {
        try {
            game = gameRepository.save(game);
            return game;
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to save an existing game", e);
        }
    }

    public Game createGame() { //Creates a user object and saves it to the database through the repository
        try {
            Game game = new Game();
            gameRepository.save(game);
            return game;
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to initialize a new Mahjong game", e);
        }
    }

    public void addTile(Game game, Tile tile) {
        try {
            game.addTile(tile);
            gameRepository.save(game);
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to add a new tile to the game"); //Update exception
        }
    }

    public void addPlayer(Game game, Player player) {
        try {
            game.addPlayer(player);
            gameRepository.save(game);
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to add a new player to the game"); //Update exception
        }
    }

    public Game findFirstGame() {
        try {
            return gameRepository.findFirstByGameId(1);
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to add a new tile");
        }
    }

}