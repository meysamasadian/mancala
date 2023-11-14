package me.asadian.mancala.game.controller;

import me.asadian.mancala.game.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GameExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<String> handleGameNotFoundException(GameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The request game is not longer available");
    }

    @ExceptionHandler(EmptySpotWasTouchedException.class)
    public ResponseEntity<String> handleEmptySpotWasTouchedException(EmptySpotWasTouchedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The spot was touched is already empty");
    }

    @ExceptionHandler(InvalidSpotWasTouchedException.class)
    public ResponseEntity<String> handleInvalidSpotWasTouchedException(InvalidSpotWasTouchedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The spot was touched is invalid");
    }

    @ExceptionHandler(GameOverException.class)
    public ResponseEntity<String> handleGameOverException(GameOverException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The game is already over");
    }

    @ExceptionHandler(GameWithoutPlayerException.class)
    public ResponseEntity<String> handleGameWithoutPlayerException(GameWithoutPlayerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The game doesn't have primary player");
    }

    @ExceptionHandler(UnauthorizedGameAccessException.class)
    public ResponseEntity<String> handleUnauthorizedGameAccessException(UnauthorizedGameAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not allowed to access it");
    }

    @ExceptionHandler(WrongTurnException.class)
    public ResponseEntity<String> handleWrongTurnException(WrongTurnException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not allowed to do anything in the other's turn");
    }

}