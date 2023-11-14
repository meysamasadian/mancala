package me.asadian.mancala.game.core;

import me.asadian.mancala.game.exceptions.EmptySpotWasTouchedException;
import me.asadian.mancala.game.exceptions.GameOverException;
import me.asadian.mancala.game.exceptions.InvalidSpotWasTouchedException;
import me.asadian.mancala.shared.dto.game.Board;

public interface BoardManager {

    /**
     * This method lets player move the stone in one of their own specific spot
     * Each Player only can move the stones in one of their own small spots
     * Primary player is who initiates the game
     * @throws EmptySpotWasTouchedException, This exception should occur if the selected spot is Empty
     * @throws InvalidSpotWasTouchedException, InvalidSpotWasTouchedException should occur if the index of spot index
     * is out of range
     * @throws GameOverException, GameOverException occurs if the game is over
     */
    void moveStonesOf(int spotIndex) throws EmptySpotWasTouchedException, InvalidSpotWasTouchedException, GameOverException;


    /**
     * This function returns the last state of board which are encapsulated in a Board instance
     * @return Board
     */
    Board getBoard();


}
