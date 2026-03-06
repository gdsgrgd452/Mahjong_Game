package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    Game findFirstByGameId(Integer gameId);
}