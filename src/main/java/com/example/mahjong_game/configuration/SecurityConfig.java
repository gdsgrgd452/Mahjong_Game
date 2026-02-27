package com.example.mahjong_game.configuration;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService; //The user details service
    public SecurityConfig(UserDetailsService customUserDetailsService) { //Initialises a custom user details service
        this.userDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //Disables CSRF
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/home", "/game", "/game/pung", "/game/discard", "/game/chow", "/tilesDisplay", "/update", "/images/**", "/startGame", "/*.css").permitAll() //Sets which URLs are public
                        //.requestMatchers("/admin").hasAnyRole("ADMIN") //Set which URLs can be accessed only by users with a role matching these
                        //.anyRequest().authenticated() //Any other URL requires the user to be logged in
                )
                .exceptionHandling(e -> e.accessDeniedPage("/denied")); //Handles when users try to access a page they are not allowed t
        return http.build(); //Returns this information to spring

    }

    @Bean
    public PasswordEncoder passwordEncoder() { //Initialises a new password encoder
        return new BCryptPasswordEncoder();
    }
}