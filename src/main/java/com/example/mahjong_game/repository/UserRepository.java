package com.example.mahjong_game.repository;

import com.example.mahjong_game.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username); //Returns a user with a matching username
}