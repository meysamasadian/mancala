package me.asadian.mancala.game.exceptions;

public class GameMovementException extends RuntimeException {
    public GameMovementException() {
    }

    public GameMovementException(String message, Throwable cause) {
        super(message, cause);
    }
}
