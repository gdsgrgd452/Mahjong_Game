package com.example.mahjong_game.controller;

import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class LeaderboardController {

    private final PlayerService playerService;
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    @Autowired
    public LeaderboardController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) { //Adds top 10 users by wins
        List<Player> players = playerService.findAllPlayers();
        logger.info("Players: {}", players);
        players = players.stream().sorted(Comparator.comparing(Player::getWins).reversed()).toList();
        List<Player> topTenPlayers = players.subList(0, Math.min(10, players.size()));
        model.addAttribute("players", topTenPlayers);
        return "leaderboard";
    }
}

