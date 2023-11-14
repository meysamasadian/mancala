package me.asadian.mancala.game.core;

import me.asadian.mancala.game.core.impl.BoardSpot;
import me.asadian.mancala.game.core.impl.IndexedLinkedBoardManager;
import me.asadian.mancala.game.core.impl.IndexedLinkedBoardManagerFactory;
import me.asadian.mancala.game.dto.BoardDto;
import me.asadian.mancala.shared.dto.game.Board;
import me.asadian.mancala.shared.constants.game.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static me.asadian.mancala.game.constants.GameConstants.*;
import static me.asadian.mancala.shared.constants.game.Side.PRIMARY;
import static me.asadian.mancala.shared.constants.game.Side.SECONDARY;
import static org.junit.jupiter.api.Assertions.*;

class BoardManagerFactoryTest {

    private BoardManagerFactory underTest;

    @BeforeEach
    void setUp() {
        underTest = new IndexedLinkedBoardManagerFactory();
    }

    @Test
    public void testInitiate() {
        //when
        BoardManager boardManager = underTest.initiate();

        //then
        assertBoardCreatedCorrectly(boardManager);
    }

    @Test
    public void testInitiateWithState() {
        //given

        Board board = BoardDto.builder()
                .primarySide(Arrays.asList(1,2,3,4,5,6,7))
                .secondarySide(Arrays.asList(1,2,3,7,5,6,0))
                .currentTurn(Side.SECONDARY)
                .build();


        //when
        BoardManager boardManager = underTest.initiate(board);

        //then
        assertBoardCreatedCorrectly(boardManager, board);
    }

    private void assertBoardCreatedCorrectly(BoardManager boardManager, Board board) {
        assertBoardSpotsCreatedCorrectly(boardManager, board);
        assertBoardSpotsLinkedCorrectly((IndexedLinkedBoardManager) boardManager);
        assertCurrentTurn(boardManager, board.getCurrentTurn());
    }

    private void assertBoardCreatedCorrectly(BoardManager boardManager) {
        assertBoardSpotsCreatedCorrectly(boardManager);
        assertBoardSpotsLinkedCorrectly((IndexedLinkedBoardManager) boardManager);
        assertCurrentTurn(boardManager);
    }

    private void assertBoardSpotsLinkedCorrectly(IndexedLinkedBoardManager board) {
        assertOppositeSideLinkedCorrectly(board);
        assertPrimaryPathCreatedCorrectly(board);
        assertSecondaryPathCreatedCorrectly(board);
    }

    private void assertCurrentTurn(BoardManager boardManager, Side currentTurn) {
        assertEquals(currentTurn, boardManager.getBoard().getCurrentTurn());
    }

    private void assertCurrentTurn(BoardManager boardManager) {
        assertEquals(PRIMARY, boardManager.getBoard().getCurrentTurn());
    }

    private void assertPrimaryPathCreatedCorrectly(IndexedLinkedBoardManager board) {
        Map<Integer, BoardSpot> primarySideSpots = board.getBoardSpots().get(PRIMARY);
        BoardSpot startSpot = primarySideSpots.get(FIRST_SPOT_INDEX);
        BoardSpot currentSpot = startSpot.getNextSpots().get(PRIMARY);
        int index = FIRST_SPOT_INDEX + 1;
        while (index <= MANCALA_SPOT_INDEX) {
            assertEquals(index, currentSpot.getIndex());
            assertEquals(PRIMARY, currentSpot.getSide());
            if (index == MANCALA_SPOT_INDEX) {
                assertTrue(currentSpot.isMancalaSpot());
            }
            index++;
            currentSpot = currentSpot.getNextSpots().get(PRIMARY);
        }

        index = 0;

        while (!currentSpot.equals(startSpot)) {

            if (index == MANCALA_SPOT_INDEX) {
                assertFalse(currentSpot.isMancalaSpot());
                assertEquals(startSpot, currentSpot);
                break;
            } else {
                assertEquals(index, currentSpot.getIndex());
                assertEquals(SECONDARY, currentSpot.getSide());
            }
            index++;
            currentSpot = currentSpot.getNextSpots().get(PRIMARY);
        }
    }

    private void assertSecondaryPathCreatedCorrectly(IndexedLinkedBoardManager board) {
        Map<Integer, BoardSpot> secondaryPlayerSpots = board.getBoardSpots().get(SECONDARY);
        BoardSpot startSpot = secondaryPlayerSpots.get(FIRST_SPOT_INDEX);
        BoardSpot currentSpot = startSpot.getNextSpots().get(SECONDARY);
        int index = FIRST_SPOT_INDEX + 1;
        while (index <= MANCALA_SPOT_INDEX) {
            assertEquals(index, currentSpot.getIndex());
            assertEquals(SECONDARY, currentSpot.getSide());
            if (index == MANCALA_SPOT_INDEX) {
                assertTrue(currentSpot.isMancalaSpot());
            }
            index++;
            currentSpot = currentSpot.getNextSpots().get(SECONDARY);
        }

        index = 0;

        while (!currentSpot.equals(startSpot)) {

            if (index == MANCALA_SPOT_INDEX) {
                assertFalse(currentSpot.isMancalaSpot());
                assertEquals(startSpot, currentSpot);
                break;
            } else {
                assertEquals(index, currentSpot.getIndex());
                assertEquals(PRIMARY, currentSpot.getSide());
            }
            index++;
            currentSpot = currentSpot.getNextSpots().get(SECONDARY);
        }
    }

    private void assertOppositeSideLinkedCorrectly(IndexedLinkedBoardManager board) {
        Map<Integer, BoardSpot> primarySideSpots = board.getBoardSpots().get(PRIMARY);
        Map<Integer, BoardSpot> secondarySideSpots = board.getBoardSpots().get(SECONDARY);
        primarySideSpots.values().stream().limit(6).forEach(spot-> {
            assertNotNull(spot.getOppositeSpot());
            assertEquals(SECONDARY, spot.getOppositeSpot().getSide());
            assertEquals(getOppositeIndex(spot.getIndex()), spot.getOppositeSpot().getIndex());
            assertEquals(spot, spot.getOppositeSpot().getOppositeSpot());
        });

        assertNull(primarySideSpots.get(MANCALA_SPOT_INDEX).getOppositeSpot());
        assertNull(secondarySideSpots.get(MANCALA_SPOT_INDEX).getOppositeSpot());
    }

    private int getOppositeIndex(int index) {
        return LAST_SPOT_INDEX-index;
    }

    private void assertBoardSpotsCreatedCorrectly(BoardManager boardManager, Board board) {
        assertPrimarySideSpotsCreatedCorrectly(boardManager, board.getPrimarySide());
        assertSecondarySideSpotsCreatedCorrectly(boardManager,board.getSecondarySide());
    }

    private void assertBoardSpotsCreatedCorrectly(BoardManager boardManager) {
        assertPrimarySideSpotsCreatedCorrectly(boardManager);
        assertSecondarySideSpotsCreatedCorrectly(boardManager);
    }


    private void assertPrimarySideSpotsCreatedCorrectly(BoardManager boardManager, List<Integer> primarySideStates) {
        assertPrimarySideNumberOfStones(boardManager, primarySideStates);
        assertPrimarySideTaggedCorrectly((IndexedLinkedBoardManager) boardManager);
    }

    private void assertPrimarySideSpotsCreatedCorrectly(BoardManager boardManager) {
        assertPrimarySideNumberOfStones(boardManager);
        assertPrimarySideTaggedCorrectly((IndexedLinkedBoardManager) boardManager);
    }

    private void assertSecondarySideSpotsCreatedCorrectly(BoardManager boardManager, List<Integer> secondarySideStates) {
        assertSecondarySideNumberOfStones(boardManager, secondarySideStates);
        assertSecondarySideTaggedCorrectly((IndexedLinkedBoardManager) boardManager);
    }

    private void assertSecondarySideSpotsCreatedCorrectly(BoardManager boardManager) {
        assertSecondarySideNumberOfStones(boardManager);
        assertSecondarySideTaggedCorrectly((IndexedLinkedBoardManager) boardManager);
    }

    private void assertPrimarySideTaggedCorrectly(IndexedLinkedBoardManager board) {
        Map<Integer, BoardSpot> primarySideSpots = board.getBoardSpots().get(PRIMARY);
        primarySideSpots.values().stream().limit(6).forEach(
                spot -> {
                    assertEquals(PRIMARY, spot.getSide());
                    assertFalse(spot.isMancalaSpot());
                }
        );
        primarySideSpots.values().stream().skip(6).forEach(
                spot -> {
                    assertEquals(PRIMARY, spot.getSide());
                    assertTrue(spot.isMancalaSpot());
                }
        );
    }

    private void assertSecondarySideTaggedCorrectly(IndexedLinkedBoardManager board) {
        Map<Integer, BoardSpot> secondaryPlayerSpots = board.getBoardSpots().get(SECONDARY);
        secondaryPlayerSpots.values().stream().limit(6).forEach(
                spot -> {
                    assertEquals(SECONDARY, spot.getSide());
                    assertFalse(spot.isMancalaSpot());
                }
        );
        secondaryPlayerSpots.values().stream().skip(6).forEach(
                spot -> {
                    assertEquals(SECONDARY, spot.getSide());
                    assertTrue(spot.isMancalaSpot());
                }
        );
    }

    private void assertPrimarySideNumberOfStones(BoardManager boardManager) {
        List<Integer> primarySideSpots = boardManager.getBoard().getPrimarySide();
        primarySideSpots.stream().limit(6).forEach(stonesCount -> assertEquals(4, stonesCount));
        primarySideSpots.stream().skip(6).forEach(stonesCount -> assertEquals(0, stonesCount));
    }

    private void assertPrimarySideNumberOfStones(BoardManager boardManager, List<Integer> primarySideStates) {
        List<Integer> primarySideSpots = boardManager.getBoard().getPrimarySide();
        for (int i=0; i<primarySideSpots.size(); i++) {
            assertEquals(primarySideStates.get(i), primarySideSpots.get(i));
        }
    }

    private void assertSecondarySideNumberOfStones(BoardManager boardManager) {
        List<Integer> secondarySideSpots = boardManager.getBoard().getSecondarySide();
        secondarySideSpots.stream().limit(6).forEach(stonesCount -> assertEquals(4, stonesCount));
        secondarySideSpots.stream().skip(6).forEach(stonesCount -> assertEquals(0, stonesCount));
    }

    private void assertSecondarySideNumberOfStones(BoardManager boardManager, List<Integer> secondarySideStates) {
        List<Integer> secondarySideSpots = boardManager.getBoard().getSecondarySide();
        for (int i=0; i<secondarySideSpots.size(); i++) {
            assertEquals(secondarySideStates.get(i), secondarySideSpots.get(i));
        }
    }
}