package me.asadian.mancala.game.core;

import lombok.Builder;
import lombok.Getter;
import me.asadian.mancala.game.core.impl.IndexedLinkedBoardManagerFactory;
import me.asadian.mancala.game.dto.BoardDto;
import me.asadian.mancala.game.exceptions.EmptySpotWasTouchedException;
import me.asadian.mancala.game.exceptions.GameOverException;
import me.asadian.mancala.game.exceptions.InvalidSpotWasTouchedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import me.asadian.mancala.shared.constants.game.Side;
import static me.asadian.mancala.shared.constants.game.Side.PRIMARY;
import static me.asadian.mancala.shared.constants.game.Side.SECONDARY;

import java.util.*;
import java.util.stream.Stream;

import static me.asadian.mancala.game.constants.GameConstants.*;
import static me.asadian.mancala.shared.constants.game.Side.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardManagerTest {

    private BoardManager underTest;
    private BoardManagerFactory factory;


    @BeforeEach
    void setUp() {
        factory = new IndexedLinkedBoardManagerFactory();
    }


    @ParameterizedTest
    @MethodSource("gameStateAndExpectationProvider")
    public void testValidMove(TestScenario scenario) {
        //Given
        initBoard(scenario);

        //when
        try {
             underTest.moveStonesOf(scenario.getTouchedSpotIndex());
        } catch (EmptySpotWasTouchedException | InvalidSpotWasTouchedException | GameOverException e) {
            throw new RuntimeException(e);
        }

        //then
        assertGameResult(scenario);
    }

    private void initBoard(TestScenario scenario) {
        if (scenario.isFirstMove()) {
            underTest = factory.initiate();
        } else {
            underTest = factory.initiate(BoardDto.builder()
                            .primarySide(scenario.getStates().get(PRIMARY))
                            .secondarySide(scenario.getStates().get(SECONDARY))
                            .currentTurn(scenario.getCurrentTurn())
                    .build());
        }
    }

    private void assertGameResult(TestScenario scenario) {
        assertGameState(scenario);
        assertGameTurn(scenario);
        assertGameOver(scenario);
        assertScore(scenario);
    }


    private void assertScore(TestScenario scenario) {
        assertEquals(scenario.getExpectedStates().get(PRIMARY).get(MANCALA_SPOT_INDEX), underTest.getBoard().getPrimaryScores());
        assertEquals(scenario.getExpectedStates().get(SECONDARY).get(MANCALA_SPOT_INDEX), underTest.getBoard().getSecondaryScores());
    }

    private void assertGameOver(TestScenario scenario) {
        assertEquals(scenario.isGameExpectedBeOver(), underTest.getBoard().isGameOver());
    }

    private void assertGameTurn(TestScenario scenario) {
        assertEquals(scenario.getExpectedNextTurn(), underTest.getBoard().getCurrentTurn());
    }

    private void assertGameState(TestScenario scenario) {
        List<Integer> primaryPlayerSide =  underTest.getBoard().getPrimarySide();
        List<Integer> secondaryPlayerSide = underTest.getBoard().getSecondarySide();
        
        for (int i=0; i<7; i++) {
            assertEquals(scenario.getExpectedStates().get(PRIMARY).get(i), primaryPlayerSide.get(i));
            assertEquals(scenario.getExpectedStates().get(SECONDARY).get(i), secondaryPlayerSide.get(i));
        }
    }

    static Stream<TestScenario> gameStateAndExpectationProvider() {
        return Stream.of(
            //1
            TestScenario.builder()
                    .firstMove(true)
                    .touchedSpotIndex(1)
                    .gameExpectedBeOver(false)
                    .expectedNextTurn(SECONDARY)
                    .expectedStates(
                            new HashMap<>() {{
                                put(PRIMARY, Arrays.asList(4,0,5,5,5,5,0));
                                put(SECONDARY, Arrays.asList(4,4,4,4,4,4,0));
                            }}
                    )
                    .build(),
                //2
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,0,5,5,5,5,0));
                                    put(SECONDARY, Arrays.asList(4,4,4,4,4,4,0));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(2)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,0,5,5,5,5,0));
                                    put(SECONDARY, Arrays.asList(4,4,0,5,5,5,1));
                                }}
                        )
                        .build(),
                //3
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,0,5,5,5,5,0));
                                    put(SECONDARY, Arrays.asList(4,4,0,5,5,5,1));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,1,6,6,5,5,0));
                                    put(SECONDARY, Arrays.asList(4,4,0,5,5,0,2));
                                }}
                        )
                        .build(),
                //4
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,1,6,6,5,5,0));
                                    put(SECONDARY, Arrays.asList(4,4,0,5,5,0,2));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(0)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,7,7,6,6,0));
                                    put(SECONDARY, Arrays.asList(4,4,0,5,5,0,2));
                                }}
                        )
                        .build(),
                //5
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,7,7,6,6,0));
                                    put(SECONDARY, Arrays.asList(4,4,0,5,5,0,2));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(1)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,7,7,6,6,0));
                                    put(SECONDARY, Arrays.asList(4,0,1,6,6,1,2));
                                }}
                        )
                        .build(),
                //6
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,7,7,6,6,0));
                                    put(SECONDARY, Arrays.asList(4,0,1,6,6,1,2));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(2)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,0,8,7,7,1));
                                    put(SECONDARY, Arrays.asList(5,1,2,6,6,1,2));
                                }}
                        )
                        .build(),
                //7
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,0,8,7,7,1));
                                    put(SECONDARY, Arrays.asList(5,1,2,6,6,1,2));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(0)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,0,8,7,7,1));
                                    put(SECONDARY, Arrays.asList(0,2,3,7,7,2,2));
                                }}
                        )
                        .build(),
                //8
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,0,8,7,7,1));
                                    put(SECONDARY, Arrays.asList(0,2,3,7,7,2,2));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(3)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,0,0,8,8,2));
                                    put(SECONDARY, Arrays.asList(1,3,4,8,8,2,2));
                                }}
                        )
                        .build(),
                //9
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,2,0,0,8,8,2));
                                    put(SECONDARY, Arrays.asList(1,3,4,8,8,2,2));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(4)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(1,3,1,1,9,9,2));
                                    put(SECONDARY, Arrays.asList(1,3,4,8,0,3,3));
                                }}
                        )
                        .build(),
                //10
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(1,3,1,1,9,9,2));
                                    put(SECONDARY, Arrays.asList(1,3,4,8,0,3,3));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(2,4,1,1,9,0,3));
                                    put(SECONDARY, Arrays.asList(2,4,5,9,1,4,3));
                                }}
                        )
                        .build(),
                //11
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(2,4,1,1,9,0,3));
                                    put(SECONDARY, Arrays.asList(2,4,5,9,1,4,3));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(3)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(3,5,2,2,10,1,3));
                                    put(SECONDARY, Arrays.asList(2,4,5,0,2,5,4));
                                }}
                        )
                        .build(),
                //12
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(3,5,2,2,10,1,3));
                                    put(SECONDARY, Arrays.asList(2,4,5,0,2,5,4));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(4)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,6,2,2,0,2,4));
                                    put(SECONDARY, Arrays.asList(3,5,6,1,3,6,4));
                                }}
                        )
                        .build(),
                //13
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,6,2,2,0,2,4));
                                    put(SECONDARY, Arrays.asList(3,5,6,1,3,6,4));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(1)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,6,2,2,0,2,4));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,7,5));
                                }}
                        )
                        .build(),
                //14
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(4,6,2,2,0,2,4));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,7,5));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,7,3,3,1,3,4));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,0,6));
                                }}
                        )
                        .build(),
                //15
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,7,3,3,1,3,4));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,0,6));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(3)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,7,3,0,2,4,5));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,0,6));
                                }}
                        )
                        .build(),
                //16
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,7,3,0,2,4,5));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,0,6));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(4)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,7,3,0,0,5,6));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,0,6));
                                }}
                        )
                        .build(),
                //17
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,7,3,0,0,5,6));
                                    put(SECONDARY, Arrays.asList(3,0,7,2,4,0,6));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(1)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,0,4,1,1,6,7));
                                    put(SECONDARY, Arrays.asList(4,1,7,2,4,0,6));
                                }}
                        )
                        .build(),
                //18
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(5,0,4,1,1,6,7));
                                    put(SECONDARY, Arrays.asList(4,1,7,2,4,0,6));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(3)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,4,1,1,6,7));
                                    put(SECONDARY, Arrays.asList(4,1,7,0,5,0,12));
                                }}
                        )
                        .build(),
                //19
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,4,1,1,6,7));
                                    put(SECONDARY, Arrays.asList(4,1,7,0,5,0,12));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(2)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,7,8));
                                    put(SECONDARY, Arrays.asList(4,1,7,0,5,0,12));
                                }}
                        )
                        .build(),
                //20
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,7,8));
                                    put(SECONDARY, Arrays.asList(4,1,7,0,5,0,12));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,0,9));
                                    put(SECONDARY, Arrays.asList(5,2,8,1,6,1,12));
                                }}
                        )
                        .build(),
                //21
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,0,9));
                                    put(SECONDARY, Arrays.asList(5,2,8,1,6,1,12));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,0,9));
                                    put(SECONDARY, Arrays.asList(5,2,8,1,6,0,13));
                                }}
                        )
                        .build(),
                //22
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,0,9));
                                    put(SECONDARY, Arrays.asList(5,2,8,1,6,0,13));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(1)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,0,9));
                                    put(SECONDARY, Arrays.asList(5,0,9,2,6,0,13));
                                }}
                        )
                        .build(),
                //23
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,2,2,0,9));
                                    put(SECONDARY, Arrays.asList(5,0,9,2,6,0,13));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(3)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,3,0,15));
                                    put(SECONDARY, Arrays.asList(0,0,9,2,6,0,13));
                                }}
                        )
                        .build(),
                //24
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,3,0,15));
                                    put(SECONDARY, Arrays.asList(0,0,9,2,6,0,13));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(3)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,3,0,15));
                                    put(SECONDARY, Arrays.asList(0,0,9,0,7,1,13));
                                }}
                        )
                        .build(),
                //25
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,3,0,15));
                                    put(SECONDARY, Arrays.asList(0,0,9,0,7,1,13));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(4)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,1,16));
                                    put(SECONDARY, Arrays.asList(1,0,9,0,7,1,13));
                                }}
                        )
                        .build(),
                //26
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,1,16));
                                    put(SECONDARY, Arrays.asList(1,0,9,0,7,1,13));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(SECONDARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,1,16));
                                    put(SECONDARY, Arrays.asList(1,0,9,0,7,0,14));
                                }}
                        )
                        .build(),
                //27
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,1,16));
                                    put(SECONDARY, Arrays.asList(1,0,9,0,7,0,14));
                                }}
                        )
                        .currentTurn(SECONDARY)
                        .touchedSpotIndex(0)
                        .expectedNextTurn(PRIMARY)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,1,16));
                                    put(SECONDARY, Arrays.asList(0,1,9,0,7,0,14));
                                }}
                        )
                        .build(),
                //28
                TestScenario.builder()
                        .states(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,1,16));
                                    put(SECONDARY, Arrays.asList(0,1,9,0,7,0,14));
                                }}
                        )
                        .currentTurn(PRIMARY)
                        .touchedSpotIndex(5)
                        .expectedNextTurn(NONE)
                        .expectedStates(
                                new HashMap<>() {{
                                    put(PRIMARY, Arrays.asList(0,0,0,0,0,0,17));
                                    put(SECONDARY, Arrays.asList(0,0,0,0,0,0,31));
                                }}
                        )
                        .gameExpectedBeOver(true)
                        .build()
        );
    }

    @Getter
    @Builder
    static class TestScenario {
        private boolean firstMove;
        private Map<Side, List<Integer>> states;
        private Side currentTurn;
        private int touchedSpotIndex;
        private boolean gameExpectedBeOver;
        private Side expectedNextTurn;
        private Map<Side, List<Integer>> expectedStates;
    }

}