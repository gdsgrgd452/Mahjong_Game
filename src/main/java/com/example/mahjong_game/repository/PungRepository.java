package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Pung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PungRepository extends JpaRepository<Pung, Integer> {

    List<Pung> findAllByPlayer(Player player);
}