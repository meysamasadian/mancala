package me.asadian.mancala.game.exceptions;

public class UnauthorizedGameAccessException extends Exception {
    public UnauthorizedGameAccessException(String message) {
        super(message);
    }
}
