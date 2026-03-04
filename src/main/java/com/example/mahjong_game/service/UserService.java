package com.example.mahjong_game.service;

import com.example.mahjong_game.model.User;
import com.example.mahjong_game.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlayerService playerService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PlayerService playerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.playerService = playerService;
    }

    public void registerUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setPlayer(playerService.createPlayer(username, false));
        userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
