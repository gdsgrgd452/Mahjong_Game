package com.example.mahjong_game.model.actions;

import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.tiles.Tile;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pung {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto generates the ID
    int pungId;
    public int getPungId() {
        return pungId;
    }
    public void setPungId(int pungId) {
        this.pungId = pungId;
    }


    @OneToMany(mappedBy = "pung") //Links the pung to the Tiles it has
    private List<Tile> tiles = new ArrayList<>();
    public List<Tile> getTiles() { return tiles; }
    public void setTiles(List<Tile> tiles) { this.tiles = tiles; }
    public void addTile(Tile tile) {
        this.tiles.add(tile);
        tile.setPung(this);
    }
    public void removeTile(Tile tile) {
        this.tiles.removeIf(t -> t.getTileId() == tile.getTileId());
        tile.setPung(null);
    }

    @ManyToOne @JoinColumn(name = "player_id") //Links the pung to the player that has it
    private Player player;
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

}
