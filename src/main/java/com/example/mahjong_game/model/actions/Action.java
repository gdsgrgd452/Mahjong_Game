package com.example.mahjong_game.model.actions;

import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // All actions are in 1 table
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto generates the ID
    int id;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    String actionType; //Just for visibility right now
    public String getActionType() {
        return actionType;
    }
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @OneToMany(mappedBy = "action") //Links the action to the Tiles it has
    private List<Tile> tiles = new ArrayList<>();
    public List<Tile> getTiles() { return tiles; }
    public void setTiles(List<Tile> tiles) { this.tiles = tiles; }
    public void addTile(Tile tile) {
        this.tiles.add(tile);
        tile.setAction(this);
    }
    public void removeTile(Tile tile) {
        this.tiles.removeIf(t -> t.getTileId() == tile.getTileId()); //Replace with just == ?
        tile.setAction(null);
    }

    @ManyToOne @JoinColumn(name = "player_id") //Links the action to the player that has it
    private Player player;
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
}
