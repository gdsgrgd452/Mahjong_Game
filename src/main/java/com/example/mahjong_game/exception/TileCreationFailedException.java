package com.example.mahjong_game.exception;

public class TileCreationFailedException extends RuntimeException {

    public TileCreationFailedException(String message) {
        super(message);
    }

    public TileCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
