package me.asadian.mancala.game.exceptions;

public class WrongTurnException extends RuntimeException {
    public WrongTurnException(String message) {
        super(message);
    }
}
