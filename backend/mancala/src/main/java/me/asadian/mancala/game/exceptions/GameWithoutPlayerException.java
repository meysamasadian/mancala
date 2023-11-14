package me.asadian.mancala.game.exceptions;

public class GameWithoutPlayerException extends RuntimeException {
    public GameWithoutPlayerException(String message) {
        super(message);
    }
}
