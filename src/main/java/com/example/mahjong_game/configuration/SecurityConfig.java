package com.example.mahjong_game.configuration;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable) //Disables CSRF
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                            .requestMatchers("/login", "/register", "/css/**").permitAll() //Public
                            .requestMatchers("/game/**", "/home", "/leaderboard", "/profile", "/startGame", "/js/game.js", "/images/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/admin").hasAnyRole("ADMIN") //Can access if they have the role
                            .anyRequest().authenticated() //Any other URL requires the user to be logged in
                )
                .exceptionHandling(e -> e.accessDeniedPage("/denied")) //Handles when users try to access a page they are not allowed to
                    .formLogin(form -> form
                            .loginPage("/login") //Sets a custom login page
                            .loginProcessingUrl("/login") //Sets where the login data is posted to
                            .failureUrl("/login?error=true") //Takes you back to log in if it failed
                            .defaultSuccessUrl("/homepage", true) //Where the user is sent after completing a login
                            .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") //After logging out sends the user back to the login page after logging out
                        .permitAll()
                )
                .userDetailsService(userDetailsService); //Defines how it accesses the database
        return http.build(); //Returns this information to spring

    }

    @Bean
    public PasswordEncoder passwordEncoder() { //Initialises a new password encoder
        return new BCryptPasswordEncoder();
    }
}



