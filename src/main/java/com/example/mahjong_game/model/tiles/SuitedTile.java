package com.example.mahjong_game.model.tiles;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("Suited")
public class SuitedTile extends Tile  {
    Integer number;
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }



}