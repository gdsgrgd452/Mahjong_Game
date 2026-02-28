package com.example.mahjong_game.model.tiles;

import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.actions.Pung;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // All tiles are in 1 table
@DiscriminatorColumn(name = "type") // The column that selects for the subclass
public class Tile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto generates the ID
    int tileId;
    public int getTileId() {
        return tileId;
    }
    public void setTileId(int tileId) {
        this.tileId = tileId;
    }

    String suit;
    public String getSuit() {
        return suit;
    }
    public void setSuit(String suit) {
        this.suit = suit;
    }

    boolean justPickedUp;
    public boolean isJustPickedUp() {
        return justPickedUp;
    }
    public void setJustPickedUp(boolean justPickedUp) {
        this.justPickedUp = justPickedUp;
    }

    boolean discarded;
    public boolean isDiscarded() {
        return discarded;
    }
    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    boolean justDiscarded;
    public boolean isJustDiscarded() {
        return justDiscarded;
    }
    public void setJustDiscarded(boolean justDiscarded) {
        this.justDiscarded = justDiscarded;
    }

    int amountRemaining = 4; //Amount of that tile that are not thrown out or displayed as part of a pung or chow in a players hand
    public int getAmountRemaining() {return amountRemaining;}
    public void setAmountRemaining(int amountRemaining) {this.amountRemaining = amountRemaining;}

    String imagePath; //In the format bamboo-6 for example to get the image
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    boolean placed;
    public boolean isPlaced() {
        return placed;
    }
    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    @ManyToOne @JoinColumn(name = "game_id")
    private Game game;
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    @ManyToOne @JoinColumn(name = "pung_id")
    private Pung pung;
    public Pung getPung() {
        return pung;
    }
    public void setPung(Pung pung) {
        this.pung = pung;
    }

    @ManyToOne @JoinColumn(name = "chow_id")
    private Chow chow;
    public Chow getChow() {
        return chow;
    }
    public void setChow(Chow chow) {
        this.chow = chow;
    }

    @ManyToOne @JoinColumn(name = "player_id")
    private Player player;
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Transient //To prevent errors because honor tiles have no number
    public Integer getNumber() {
        return null;
    }

}
