package com.example.mahjong_game.seeder;

import com.example.mahjong_game.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserSeeder { //Class to add test users into the database when the application builds

    @Bean
    @ConditionalOnProperty(name = "mahjong.seed.users", havingValue = "true", matchIfMissing = false)
    CommandLineRunner seedUsers(UserService userService) {
        return args -> userService.registerUser("e", "e");
    }
}
