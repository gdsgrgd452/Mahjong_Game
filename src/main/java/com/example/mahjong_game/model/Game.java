package com.example.mahjong_game.model;

import com.example.mahjong_game.model.tiles.Tile;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto generates the ID
    private int gameId;
    public int getGameId() {
        return gameId;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @OneToOne
    @JoinColumn(name = "dealer_id")
    private Player dealer;
    public Player getDealer() {
        return dealer;
    }
    public void setDealer(Player dealer) {
        this.dealer = dealer;
    }

    @OneToOne
    @JoinColumn(name = "current_player_id")
    private Player currentPlayer;
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL) //Links the game to the Tiles it has
    private List<Tile> tilesInWholeGame = new ArrayList<>();
    public void addTile(Tile tile) {
        this.tilesInWholeGame.add(tile);
        tile.setGame(this);
    }

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL) //Links the game to the Players it has
    private List<Player> playersInGame = new ArrayList<>();
    public List<Player> getPlayersInGame() {
        return playersInGame;
    }
    public void setPlayersInGame(List<Player> playersInGame) {
        this.playersInGame = playersInGame;
    }
    public void addPlayer(Player player) {
        this.playersInGame.add(player);
        player.setGame(this);
    }
}
