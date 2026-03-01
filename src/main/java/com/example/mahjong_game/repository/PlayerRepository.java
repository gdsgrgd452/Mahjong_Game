package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findByUsername(String username); //Returns a user with a matching username
    List<Player> findAllByGame(Game game); //All users in a certain game
    Player findPlayerByActionToTakeIsNotNull();
}
