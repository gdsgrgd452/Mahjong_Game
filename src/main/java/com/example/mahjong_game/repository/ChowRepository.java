package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Chow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChowRepository extends JpaRepository<Chow, Integer> {

    List<Chow> findAllByPlayer(Player player);
}