package com.example.mahjong_game.model;

import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.Tile;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // All players (incl bots) are in 1 table
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto generates the ID
    int playerId;
    public int getPlayerId() {
        return playerId;
    }
    public void setPlayerId(int userId) {
        this.playerId = userId;
    }

    private String username;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    private String password;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    private boolean isBot;
    public boolean isBot() {
        return isBot;
    }
    public void setBot(boolean isBot) {
        this.isBot = isBot;
    }

    private String role;
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    String actionToTake;
    public String getActionToTake() {
        return actionToTake;
    }
    public void setActionToTake(String actionToTake) {
        this.actionToTake = actionToTake;
    }

    List<Integer> tilesInActionToTake;
    public List<Integer> getTilesInActionToTake() { return tilesInActionToTake; }
    public void setTilesInActionToTake(List<Integer> tilesInActionToTake) {
        this.tilesInActionToTake = tilesInActionToTake;
    }

    Integer points;
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL) //Links the game to the pungs it has
    private List<Pung> pungs = new ArrayList<>();
    public List<Pung> getPungs() {return pungs;}
    public void addPung(Pung pung) {
        this.pungs.add(pung);
        pung.setPlayer(this);
    }
    public void removePung(Pung pung) {
        this.pungs.removeIf(p -> p.getPungId() == pung.getPungId());
    }

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL) //Links the player to the chows it has
    private List<Chow> chows = new ArrayList<>();
    public List<Chow> getChows() {return chows;}
    public void addChow(Chow chow) {
        this.chows.add(chow);
        chow.setPlayer(this);
    }

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL) //Links the player to the tiles it has
    private List<Tile> currentHand = new ArrayList<>();
    public List<Tile> getCurrentHand() {
        return currentHand;
    }
    public List<Tile> getCurrentHandNoPlaced() {
        List<Tile> currentHandNoPlaced = currentHand;
        return currentHandNoPlaced.stream().filter(t -> !t.isPlaced()).toList();
    }
    public List<Tile> getCurrentHandPlaced() {
        List<Tile> currentHandPlaced = currentHand;
        return currentHandPlaced.stream().filter(Tile::isPlaced).toList();
    }
    public void setCurrentHand(List<Tile> tiles) { this.currentHand = tiles; }
    public void addTile(Tile tile) {
        this.currentHand.add(tile);
        tile.setPlayer(this);
    }
    public void removeTile(Tile tile) { // Remove the tile from the list matching the ID
        this.currentHand.removeIf(t -> t.getTileId() == tile.getTileId());
    }

    @ManyToOne @JoinColumn(name = "game_id") //Links to the game the player is in
    private Game game;
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }



}
