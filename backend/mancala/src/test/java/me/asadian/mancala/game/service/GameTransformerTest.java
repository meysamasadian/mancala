package me.asadian.mancala.game.service;


import me.asadian.mancala.game.exceptions.GameWithoutPlayerException;
import me.asadian.mancala.game.model.BoardStateModel;
import me.asadian.mancala.game.model.GameModel;
import me.asadian.mancala.game.service.impl.GameTransformerImpl;
import me.asadian.mancala.shared.constants.game.Side;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.Optional;

import static me.asadian.mancala.game.constants.GameConstants.MANCALA_SPOT_INDEX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GameTransformerTest {
    private GameTransformer underTest;

    @Mock
    private PlayerServiceFacade playerServiceFacade;

    @BeforeEach
    public void setUp()
    {
        this.underTest = new GameTransformerImpl(playerServiceFacade);
    }


    @Test
    public void testTransformOfCompletedModel() {
        //given
        GameModel gameModel = GameModel.builder()
                .id(1234L)
                .token("gm_test2")
                .primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .startedAt(LocalDateTime.now().minusMinutes(3))
                .finishedAt(LocalDateTime.now())
                .active(false)
                .winner(Side.SECONDARY)
                .currentState(BoardStateModel.builder()
                        .id(7655L)
                        .currentTurn(Side.PRIMARY)
                        .primarySideSpots(Arrays.asList(1, 2, 3, 4, 5, 6, 0))
                        .secondarySideSpots(Arrays.asList(1, 2, 3, 4, 5, 6, 0))
                        .isGameOver(false)
                        .build())
                .build();



        when(playerServiceFacade.getPlayerByUsername("pl_test1")).thenReturn(Optional.of(TestPlayerDto.builder().username("test1").build()));
        when(playerServiceFacade.getPlayerByUsername("pl_test2")).thenReturn(Optional.of(TestPlayerDto.builder().username("test2").build()));

        //when
        Game game;
        try {
            game = underTest.transform(gameModel);
        } catch (GameWithoutPlayerException e) {
            throw new RuntimeException(e);
        }

        //then
        assertEquals(TestPlayerDto.builder().username("test1").build(), game.getPrimaryPlayer());
        assertEquals(TestPlayerDto.builder().username("test2").build(), game.getSecondaryPlayer());
        assertEquals("gm_test2", game.getToken());
        assertEquals(gameModel.getCurrentState().getPrimarySideSpots(), game.getBoard().getPrimarySide());
        assertEquals(gameModel.getCurrentState().getSecondarySideSpots(), game.getBoard().getSecondarySide());
        assertEquals(gameModel.getCurrentState().getPrimarySideSpots().get(MANCALA_SPOT_INDEX), game.getBoard().getPrimaryScores());
        assertEquals(gameModel.getCurrentState().getSecondarySideSpots().get(MANCALA_SPOT_INDEX), game.getBoard().getSecondaryScores());
        assertEquals(gameModel.getCurrentState().getCurrentTurn(), game.getBoard().getCurrentTurn());
    }


}