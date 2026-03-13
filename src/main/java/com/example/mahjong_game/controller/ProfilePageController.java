package com.example.mahjong_game.controller;

import com.example.mahjong_game.service.UserService;
import com.example.mahjong_game.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilePageController {

    private final UserService userService;
    @Autowired
    public ProfilePageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String serveProfilePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) return "redirect:/login";
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("player", user.getPlayer());
        return "profile";
    }
}
