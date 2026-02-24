package com.example.mahjong_game.model.tiles;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Honor")
public class HonorTile extends Tile  {

}