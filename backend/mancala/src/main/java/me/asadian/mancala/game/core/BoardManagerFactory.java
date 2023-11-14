package me.asadian.mancala.game.core;

import me.asadian.mancala.shared.dto.game.Board;

public interface BoardManagerFactory {

    /**
     * This method is going to initiate an implementation of Board when the game is about to start
     * @return Board
     */
    BoardManager initiate();


    /**
     * This method is going to initiate an implementation of Board when the game has already started
     * And this method gets the state of the game and initiates the game according to the current state
     * @return Board
     */
    BoardManager initiate(Board board);
}
