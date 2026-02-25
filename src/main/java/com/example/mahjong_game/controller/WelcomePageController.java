package com.example.mahjong_game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomePageController {

    @GetMapping("/home")
    public String serveHomepage(Model model) {
        return "homepage";
    }
}
