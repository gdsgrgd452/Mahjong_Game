package com.example.mahjong_game.exception;

public class GetFromDatabaseFailedException extends RuntimeException {

    public GetFromDatabaseFailedException(String message) {
        super(message);
    }

    public GetFromDatabaseFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}