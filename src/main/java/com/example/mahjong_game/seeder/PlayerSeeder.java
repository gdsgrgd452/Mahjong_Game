//package com.example.mahjong_game.seeder;
//
//import com.example.mahjong_game.model.Player;
//import com.example.mahjong_game.repository.PlayerRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class PlayerSeeder { //Class to add test users into the database when the application builds
//
//    @Bean
//    @ConditionalOnProperty(name = "mahjong.seed.users", havingValue = "true", matchIfMissing = false)
//    CommandLineRunner seedUsers(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            for (int i = 1; i < 5; i++) {
//                Player player = new Player();
//                String username = "PLAYER" + i; //Adds a number to the user's username for identification
//                String password = passwordEncoder.encode("password");
//                player.setUsername(username);
//                player.setPassword(password);
//                player.setRole("USER");
//                if (i != 0) player.setBot(true);
//                playerRepository.save(player);
//            }
//        };
//    }
//}
