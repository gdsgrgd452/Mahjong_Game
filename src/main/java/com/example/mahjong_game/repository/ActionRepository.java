package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {
    Action findFirstById(Integer actionId);
    List<Action> findAllByPlayer(Player player);
}
