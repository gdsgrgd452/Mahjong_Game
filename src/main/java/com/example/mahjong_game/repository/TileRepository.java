package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.tiles.Tile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// This repo works for Suited, FlowerTile and HonorTile
@Repository
public interface TileRepository extends JpaRepository<Tile, Integer> {
    List<Tile> getAllByGame(Game game);
    List<Tile> findAllByGameAndPlayerIsNullAndDiscardedAndJustDiscarded(Game game, boolean discarded, boolean justDiscarded);
    Tile findFirstByGameAndPlayerIsNullAndDiscardedAndJustDiscarded(Game game, boolean discarded, boolean justDiscarded);
}
