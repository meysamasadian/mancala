package me.asadian.mancala.game.core.impl;

import me.asadian.mancala.game.core.BoardManager;
import me.asadian.mancala.game.core.BoardManagerFactory;
import me.asadian.mancala.shared.constants.game.Side;
import me.asadian.mancala.shared.dto.game.Board;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static me.asadian.mancala.game.constants.GameConstants.*;
import static me.asadian.mancala.shared.constants.game.Side.PRIMARY;
import static me.asadian.mancala.shared.constants.game.Side.SECONDARY;

@Component
public class IndexedLinkedBoardManagerFactory implements BoardManagerFactory {

    @Override
    public BoardManager initiate() {
        Map<Side, Map<Integer, BoardSpot>> boardSpots = new HashMap<>() {{
            put(PRIMARY, new HashMap<>());
            put(SECONDARY, new HashMap<>());
        }};

        initSmallSpots(boardSpots);
        initMancalaSpots(boardSpots);

        return new IndexedLinkedBoardManager(boardSpots, PRIMARY);
    }

    private void initSmallSpots(Map<Side, Map<Integer, BoardSpot>> boardSpots) {
        for (int index = FIRST_SPOT_INDEX; index < NUMBER_OF_SMALL_SPOTS; index++) {

            initSmallSpot(boardSpots, index, PRIMARY, NUMBER_OF_STONES_IN_EACH_SPOT);
            initSmallSpot(boardSpots, getOppositeSideIndex(index), SECONDARY, NUMBER_OF_STONES_IN_EACH_SPOT);

            linkSmallSpots(boardSpots, index);
        }
    }

    private void linkSmallSpots(Map<Side, Map<Integer, BoardSpot>> boardSpots, final int currentIndex) {
        if (currentIndex > FIRST_SPOT_INDEX) {
            boardSpots.get(PRIMARY).get(getPreviousSpot(currentIndex)).setNextSpots(new HashMap<>(){{
                put(PRIMARY, boardSpots.get(PRIMARY).get(currentIndex));
                put(SECONDARY, boardSpots.get(PRIMARY).get(currentIndex));
            }});

            boardSpots.get(SECONDARY).get(getOppositeSideIndex(currentIndex)).setNextSpots(new HashMap<>(){{
                put(PRIMARY, boardSpots.get(SECONDARY).get(getNextSpotInOppositeSide(currentIndex)));
                put(SECONDARY, boardSpots.get(SECONDARY).get(getNextSpotInOppositeSide(currentIndex)));
            }});
            
        }

        boardSpots.get(PRIMARY).get(currentIndex).setOppositeSpot(boardSpots.get(SECONDARY).get(5 - currentIndex));
        boardSpots.get(SECONDARY).get(5 - currentIndex).setOppositeSpot(boardSpots.get(PRIMARY).get(currentIndex));
    }

    private void initMancalaSpots(Map<Side, Map<Integer, BoardSpot>> boardSpots) {
        initMancalaSpot(boardSpots, PRIMARY, 0);
        linkMancala(boardSpots, PRIMARY, SECONDARY);

        initMancalaSpot(boardSpots, SECONDARY, 0);
        linkMancala(boardSpots, SECONDARY, PRIMARY);
    }
    


    @Override
    public BoardManager initiate(Board board) {
        Map<Side, Map<Integer, BoardSpot>> boardSpots = new HashMap<>() {{
            put(PRIMARY, new HashMap<>());
            put(SECONDARY, new HashMap<>());
        }};


        initSmallSpots(boardSpots, board);
        initMancalaSpots(boardSpots, board);

        return new IndexedLinkedBoardManager(boardSpots, board.getCurrentTurn());
    }

    private void initSmallSpots(Map<Side, Map<Integer, BoardSpot>> boardSpots, Board board) {
        for (int index = FIRST_SPOT_INDEX; index < NUMBER_OF_SMALL_SPOTS; index++) {

            int oppositeSideIndex = getOppositeSideIndex(index);
            int stonesCount = board.getPrimarySide().get(index);
            int oppositeSideStoneCount = board.getSecondarySide().get(oppositeSideIndex);

            initSmallSpot(boardSpots, index, PRIMARY, stonesCount);
            initSmallSpot(boardSpots, oppositeSideIndex, SECONDARY, oppositeSideStoneCount);
            
            linkSmallSpots(boardSpots, index);
        }
    }
    
    private void initSmallSpot(Map<Side, Map<Integer, BoardSpot>> boardSpots, int index, Side side, int numberOfStone) {
        boardSpots.get(side).put(index,
                BoardSpot.builder()
                        .stonesCount(numberOfStone)
                        .index(index)
                        .side(side)
                        .build());
    }

    private void initMancalaSpots(Map<Side, Map<Integer, BoardSpot>> boardSpots, Board board) {

        initMancalaSpot(boardSpots, PRIMARY, board.getPrimarySide().get(MANCALA_SPOT_INDEX));
        linkMancala(boardSpots, PRIMARY, SECONDARY);

        initMancalaSpot(boardSpots,SECONDARY, board.getSecondarySide().get(MANCALA_SPOT_INDEX));
        linkMancala(boardSpots, SECONDARY, PRIMARY);
    }

    private static void linkMancala(Map<Side, Map<Integer, BoardSpot>> boardSpots, Side side, Side oppositeSide) {
        boardSpots.get(side).get(LAST_SPOT_INDEX).setNextSpots(new HashMap<>() {{
            put(side, boardSpots.get(side).get(MANCALA_SPOT_INDEX));
            put(oppositeSide, boardSpots.get(oppositeSide).get(FIRST_SPOT_INDEX));
        }});

        boardSpots.get(side).get(MANCALA_SPOT_INDEX).setNextSpots(new HashMap<>() {{
            put(side, boardSpots.get(oppositeSide).get(FIRST_SPOT_INDEX));
        }});
    }

    private void initMancalaSpot(Map<Side, Map<Integer, BoardSpot>> boardSpots, Side side, int stonesCount) {
        boardSpots.get(side).put(MANCALA_SPOT_INDEX,BoardSpot.builder()
                .side(side)
                .stonesCount(stonesCount)
                .index(MANCALA_SPOT_INDEX)
                .mancalaSpot(true)
                .build()
        );
    }

    private static int getNextSpotInOppositeSide(int index) {
        return LAST_SPOT_INDEX - index + 1;
    }

    private static int getPreviousSpot(int index) {
        return index - 1;
    }

    private static int getOppositeSideIndex(int index) {
        return LAST_SPOT_INDEX - index;
    }
}
