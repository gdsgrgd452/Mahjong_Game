package com.example.mahjong_game.controller;

import com.example.mahjong_game.model.User;
import com.example.mahjong_game.service.UserService;
import com.example.mahjong_game.validator.RegisterValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
public class AccountController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder("user")
    void registerBinder(WebDataBinder binder) {
        binder.addValidators(new RegisterValidator(userService));
    }

    @GetMapping("/register")
    public String registrationPage(Model model) {
        model.addAttribute("user", new User()); //For the form
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }
        userService.registerUser(user.getUsername(), user.getPassword());
        logger.info("User registered successfully: {}", user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", defaultValue = "false") boolean error) {
        if (error) model.addAttribute("loginError", "Invalid username or password."); //Data to display to user if login fails
        model.addAttribute("user", new User());
        return "login";
    }
}
