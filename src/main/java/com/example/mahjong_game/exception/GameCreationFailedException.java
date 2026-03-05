package com.example.mahjong_game.exception;

public class GameCreationFailedException extends RuntimeException {

    public GameCreationFailedException(String message) {
        super(message);
    }

    public GameCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}