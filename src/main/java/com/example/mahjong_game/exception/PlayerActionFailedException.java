package com.example.mahjong_game.exception;

public class PlayerActionFailedException extends RuntimeException {

    public PlayerActionFailedException(String message) {
        super(message);
    }

    public PlayerActionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
