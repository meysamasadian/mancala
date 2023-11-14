package me.asadian.mancala.game.core.impl;


import lombok.Getter;
import me.asadian.mancala.game.core.BoardManager;
import me.asadian.mancala.game.dto.BoardDto;
import me.asadian.mancala.game.exceptions.EmptySpotWasTouchedException;
import me.asadian.mancala.game.exceptions.GameOverException;
import me.asadian.mancala.game.exceptions.InvalidSpotWasTouchedException;
import me.asadian.mancala.shared.constants.game.Side;
import me.asadian.mancala.shared.dto.game.Board;
import java.util.List;
import java.util.Map;

import static me.asadian.mancala.game.constants.GameConstants.*;
import static me.asadian.mancala.shared.constants.game.Side.PRIMARY;
import static me.asadian.mancala.shared.constants.game.Side.SECONDARY;
import static me.asadian.mancala.shared.constants.game.Side.NONE;


@Getter
public class IndexedLinkedBoardManager implements BoardManager {

    private final Map<Side, Map<Integer, BoardSpot>> boardSpots;
    private Side currentTurn;

    IndexedLinkedBoardManager(Map<Side, Map<Integer, BoardSpot>> boardSpots, Side currentTurn) {
        this.boardSpots = boardSpots;
        this.currentTurn = currentTurn;
    }
    

    @Override
    public void moveStonesOf(int spotIndex) throws EmptySpotWasTouchedException, InvalidSpotWasTouchedException, GameOverException {
        if (isGameOver()) {
            throw new GameOverException("Game is already over");
        }

        if (isSpotNumberInvalid(spotIndex)) {
            throw new InvalidSpotWasTouchedException("Invalid touched spot index range");
        }

        if (isSpotEmpty(boardSpots.get(getCurrentTurn()).get(spotIndex))) {
            throw new EmptySpotWasTouchedException("Touched spot is empty");
        }

        BoardSpot lastSpot = moveStonesAlongSideOf(getSelectedSpot(spotIndex));

        checkStoneBonusPossibility(lastSpot);

        if (isGameOver()) {
            moveLastStoneToMancala();
        }

        switchTurn(lastSpot);
    }

    private boolean isGameOver() {
        return isSideEmpty(PRIMARY) || isSideEmpty(SECONDARY);
    }

    private boolean isSideEmpty(Side side) {
        return boardSpots.get(side).values().stream().limit(NUMBER_OF_SMALL_SPOTS).allMatch(BoardSpot::isEmpty);
    }

    private void switchTurn(BoardSpot lastSpot) {
        if (isGameOver()) {
            currentTurn = NONE;
        } else {
            if (!hasLastStoneMovedToMancala(lastSpot)) {
                if (currentTurn.equals(PRIMARY)) {
                    currentTurn = SECONDARY;
                } else {
                    currentTurn = PRIMARY;
                }
            }
        }
    }

    private boolean hasLastStoneMovedToMancala(BoardSpot spot) {
        return spot.isMancalaSpot();
    }

    private void moveLastStoneToMancala() {
        boardSpots.get(PRIMARY).values().stream().limit(NUMBER_OF_SMALL_SPOTS).forEach(spot -> {
            int numberOfStones = spot.releaseStones();

            getMancalaSpot(PRIMARY).increaseStonesBy(numberOfStones);
        });

        boardSpots.get(SECONDARY).values().stream().limit(NUMBER_OF_SMALL_SPOTS).forEach(spot -> {
            int numberOfStones = spot.releaseStones();

            getMancalaSpot(SECONDARY).increaseStonesBy(numberOfStones);
        });
    }

    private BoardSpot getMancalaSpot(Side side) {
        return boardSpots.get(side).get(MANCALA_SPOT_INDEX);
    }

    private BoardSpot getSelectedSpot(int spotIndex) {
        return boardSpots.get(getCurrentTurn()).get(spotIndex);
    }

    private BoardSpot moveStonesAlongSideOf(BoardSpot currentSpot) {
        int totalStonesToMove = currentSpot.releaseStones();

        while (totalStonesToMove > 0) {
            currentSpot = currentSpot.getNextSpots().get(getCurrentTurn());
            currentSpot.increaseStonesBy(1);
            totalStonesToMove--;
        }
        return currentSpot;
    }

    private void checkStoneBonusPossibility(BoardSpot lastSpot) {
        if (hasLastStoneMovedToOwnSideEmptySpot(lastSpot)) {
            if (isOppositeSpotNotEmpty(lastSpot)) {
                moveOppositeSpotStoneToOwnMancala(lastSpot);
            }
        }
    }

    private void moveOppositeSpotStoneToOwnMancala(BoardSpot spot) {
        int firstStonesCount = spot.getOppositeSpot().releaseStones();
        int stonesCount = spot.releaseStones() + firstStonesCount;
        boardSpots.get(getCurrentTurn()).get(MANCALA_SPOT_INDEX).increaseStonesBy(stonesCount);
    }


    private boolean hasLastStoneMovedToOwnSideEmptySpot(BoardSpot currentSpot) {
        return currentSpot.getSide().equals(getCurrentTurn()) 
                && !currentSpot.isMancalaSpot() 
                && currentSpot.getStonesCount() == 1;
    }


    private boolean isOppositeSpotNotEmpty(BoardSpot currentSpot) {
        return !currentSpot.getOppositeSpot().isEmpty();
    }

    private boolean isSpotEmpty(BoardSpot currentSpot) {
        return currentSpot.isEmpty();
    }

    private boolean isSpotNumberInvalid(int spot) {
        return spot < FIRST_SPOT_INDEX || spot > LAST_SPOT_INDEX;
    }



    private int getPrimaryPlayerScores() {
        return boardSpots.get(PRIMARY).get(MANCALA_SPOT_INDEX).getStonesCount();
    }

    private int getSecondaryPlayerScores() {
        return boardSpots.get(SECONDARY).get(MANCALA_SPOT_INDEX).getStonesCount();
    }

    private List<Integer> getPrimaryPlayerSide() {
        return boardSpots.get(PRIMARY).values().stream().map(BoardSpot::getStonesCount).toList();
    }

    private List<Integer> getSecondaryPlayerSide() {
        return boardSpots.get(SECONDARY).values().stream().map(BoardSpot::getStonesCount).toList();
    }

    @Override
    public Board getBoard() {
        return BoardDto.builder()
                .currentTurn(getCurrentTurn())
                .primarySide(getPrimaryPlayerSide())
                .secondarySide(getSecondaryPlayerSide())
                .gameOver(isGameOver())
                .primaryScores(getPrimaryPlayerScores())
                .secondaryScores(getSecondaryPlayerScores())
                .build();
    }
}
