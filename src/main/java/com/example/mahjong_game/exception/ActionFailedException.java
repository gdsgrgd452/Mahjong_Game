package com.example.mahjong_game.exception;

public class ActionFailedException extends RuntimeException {

    public ActionFailedException(String message) {
        super(message);
    }

    public ActionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
