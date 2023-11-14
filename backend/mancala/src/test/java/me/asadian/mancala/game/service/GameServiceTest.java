package me.asadian.mancala.game.service;

import me.asadian.mancala.game.core.BoardManager;
import me.asadian.mancala.game.core.BoardManagerFactory;
import me.asadian.mancala.game.core.impl.IndexedLinkedBoardManagerFactory;
import me.asadian.mancala.game.dto.BoardDto;
import me.asadian.mancala.game.dto.GameDto;
import me.asadian.mancala.game.exceptions.*;
import me.asadian.mancala.game.model.BoardStateModel;
import me.asadian.mancala.game.model.GameModel;
import me.asadian.mancala.game.producers.GameTurnStartedProducer;
import me.asadian.mancala.game.repository.GameRepository;
import me.asadian.mancala.game.service.impl.GameServiceImpl;
import me.asadian.mancala.shared.constants.game.Side;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private GameService underTest;

    @Mock
    private GameRepository repository;
    @Mock
    private BoardManagerFactory boardManagerFactory;

    @Mock
    private GameTransformer transformer;


    @Mock
    private ExecutorService executorService;


    @Mock
    private GameTokenGenerator gameTokenGenerator;

    @Mock
    private GameTurnStartedProducer turnStartedProducer;

    private AutoCloseable autoCloseable;


    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new GameServiceImpl(
                repository, boardManagerFactory, transformer, executorService, gameTokenGenerator, turnStartedProducer
        );
    }

    @AfterEach
    void tearDown() {
        try {
            autoCloseable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInitiateGame() {
        //given
        Player player = TestPlayerDto.builder().username("pl_test1").build();

        //when
        try {
            underTest.initiateGame(player);
        } catch (GameWithoutPlayerException e) {
            throw new RuntimeException(e);
        }

        //then
        ArgumentCaptor<GameModel> repoGameModelArgumentCaptor = ArgumentCaptor.forClass(GameModel.class);
        ArgumentCaptor<GameModel> transGameModelArgumentCaptor = ArgumentCaptor.forClass(GameModel.class);

        verify(repository).save(repoGameModelArgumentCaptor.capture());
        verify(transformer).transform(transGameModelArgumentCaptor.capture());

        GameModel gameModel = repoGameModelArgumentCaptor.getValue();
        assertTrue(gameModel.isActive());
        assertNotNull(gameModel.getCreatedAt());
        assertEquals("pl_test1", gameModel.getPrimaryPlayer());
    }


    @Test
    public void testJoinGame() {
        //given
        Player player = TestPlayerDto.builder().username("pl_test2").build();
        String gameToken = "uniqueToken";
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate();
        GameModel model = GameModel.builder().primaryPlayer(player.getUsername()).token(gameToken)
                .currentState(BoardStateModel.builder()
                        .secondarySideSpots(boardManager.getBoard().getSecondarySide())
                        .primarySideSpots(boardManager.getBoard().getPrimarySide())
                        .currentTurn(boardManager.getBoard().getCurrentTurn())
                        .build())
                .build();

        when(repository.findByTokenAndActiveTrueAndSecondaryPlayerNull(gameToken))
                .then(invocationOnMock -> Optional.of(GameModel.builder().active(true).primaryPlayer("pl_test1")
                        .createdAt(LocalDateTime.now().minusMinutes(1)).build()));
        when(boardManagerFactory.initiate()).thenAnswer(invocationOnMock -> boardManager);
        when(repository.save(any())).thenAnswer(invocation -> model);



        //when
        try {
            underTest.joinGame(player, gameToken);
        } catch (GameNotFoundException | GameWithoutPlayerException e) {
            throw new RuntimeException(e);
        }

        //then
        ArgumentCaptor<GameModel> repoGameModelArgumentCaptor = ArgumentCaptor.forClass(GameModel.class);
        ArgumentCaptor<GameModel> transGameModelArgumentCaptor = ArgumentCaptor.forClass(GameModel.class);

        verify(repository).save(repoGameModelArgumentCaptor.capture());
        verify(transformer).transform(transGameModelArgumentCaptor.capture());

        GameModel gameModel = repoGameModelArgumentCaptor.getValue();
        assertTrue(gameModel.isActive());
        assertNotNull(gameModel.getCreatedAt());
        assertNotNull(gameModel.getStartedAt());
        assertEquals("pl_test1", gameModel.getPrimaryPlayer());
        assertEquals("pl_test2", gameModel.getSecondaryPlayer());

    }

    @Test
    public void testListGamesWaitingForCompetitor() {
        //given
        GameModel model1 = GameModel.builder().active(true).primaryPlayer("pl_test1").createdAt(LocalDateTime.now().minusMinutes(2)).build();
        GameModel model2 = GameModel.builder().active(true).primaryPlayer("pl_test2").createdAt(LocalDateTime.now().minusMinutes(3)).build();
        when(repository.findAllRecentlyWaitingForSecondaryPlayer(any()))
                .thenAnswer(invocationOnMock -> Arrays.asList(model1, model2));

        //when
        underTest.listGamesWaitingForCompetitor();

        //then
        verify(transformer, times(1)).transform(model1);
        verify(transformer, times(1)).transform(model2);
    }


    @Test
    public void testGetGame() {
        //given
        String gameToken = "uniqueToken";
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test2").createdAt(LocalDateTime.now().minusMinutes(3)).build();


        when(repository.findByTokenAndActiveTrue(gameToken))
                .thenAnswer(invocationOnMock -> Optional.of(model));

        //when
        try {
            underTest.getActiveGame(gameToken);
        } catch (GameNotFoundException e) {
            throw new RuntimeException(e);
        }

        //then
        verify(transformer, times(1)).transform(model);

    }


    @Test
    public void testMakeMoveInitiation() throws InterruptedException {
        //given
        Player player1 = TestPlayerDto.builder().username("pl_test1").build();
        Player player2 = TestPlayerDto.builder().username("pl_test2").build();
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .build();
        String gameToken = "uniqueToken";
        Game game = GameDto.builder()
                .primaryPlayer(player1)
                .secondaryPlayer(player2)
                .token(gameToken)
                .active(true)
                .build();

        int spotIndex = 1;
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate();

        when(repository.findByTokenAndActiveTrue(gameToken)).thenAnswer(invocationOnMock -> Optional.ofNullable(model));
        when(transformer.transform(model)).thenAnswer(invocationOnMock -> game);
        when(boardManagerFactory.initiate()).thenAnswer(invocationOnMock -> boardManager);
        when(executorService.invokeAll(anyList())).thenAnswer(invocationOnMock -> null);

        //when
        Game result = underTest.makeMove(gameToken, spotIndex);

        //then
        assertNotNull(result);
        assertEquals(gameToken, result.getToken());
        assertTrue(result.isActive());
        assertEquals(player1, result.getPrimaryPlayer());
        assertEquals(player2, result.getSecondaryPlayer());
        assertEquals(Arrays.asList(4,0,5,5,5,5,0), result.getBoard().getPrimarySide());
        assertEquals(Arrays.asList(4,4,4,4,4,4,0), result.getBoard().getSecondarySide());
        assertEquals(Side.SECONDARY, result.getBoard().getCurrentTurn());

    }


    @Test
    public void testMakeMoveMiddleOfGame() throws InterruptedException {
        //given
        Player player1 = TestPlayerDto.builder().username("pl_test1").build();
        Player player2 = TestPlayerDto.builder().username("pl_test2").build();
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .currentState(
                        BoardStateModel.builder()
                                .currentTurn(Side.SECONDARY)
                                .primarySideSpots(Arrays.asList(0,2,7,7,6,6,0))
                                .secondarySideSpots(Arrays.asList(4,0,1,6,6,1,2))
                                .build()
                )
                .build();
        String gameToken = "uniqueToken";
        Game game = GameDto.builder()
                .primaryPlayer(player1)
                .secondaryPlayer(player2)
                .board(
                        BoardDto.builder()
                                .currentTurn(Side.SECONDARY)
                                .primarySide(Arrays.asList(0,2,7,7,6,6,0))
                                .secondarySide(Arrays.asList(4,4,0,5,5,0,2))
                                .build()
                )
                .token(gameToken)
                .active(true)
                .build();

        int spotIndex = 1;
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate(game.getBoard());
        when(repository.findByTokenAndActiveTrue(gameToken)).thenAnswer(invocationOnMock -> Optional.ofNullable(model));
        when(transformer.transform(model)).thenAnswer(invocationOnMock -> game);
        when(boardManagerFactory.initiate(game.getBoard())).thenAnswer(invocationOnMock -> boardManager);
        when(executorService.invokeAll(anyList())).thenAnswer(invocationOnMock -> null);

        //when
        Game result = underTest.makeMove(gameToken, spotIndex);

        //then
        assertNotNull(result);
        assertEquals(gameToken, result.getToken());
        assertTrue(result.isActive());
        assertEquals(player1, result.getPrimaryPlayer());
        assertEquals(player2, result.getSecondaryPlayer());
        assertEquals(Arrays.asList(0,2,7,7,6,6,0), result.getBoard().getPrimarySide());
        assertEquals(Arrays.asList(4,0,1,6,6,1,2), result.getBoard().getSecondarySide());
        assertEquals(Side.PRIMARY, result.getBoard().getCurrentTurn());

    }


    @Test
    public void testMakeMoveBonusStone() throws InterruptedException {
        //given
        Player player1 = TestPlayerDto.builder().username("pl_test1").build();
        Player player2 = TestPlayerDto.builder().username("pl_test2").build();
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .currentState(
                        BoardStateModel.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySideSpots(Arrays.asList(0,0,0,2,2,0,9))
                                .secondarySideSpots(Arrays.asList(5,0,9,2,6,0,13))
                                .build()
                )
                .build();
        String gameToken = "uniqueToken";
        Game game = GameDto.builder()
                .primaryPlayer(player1)
                .secondaryPlayer(player2)
                .board(
                        BoardDto.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySide(Arrays.asList(0,0,0,2,2,0,9))
                                .secondarySide(Arrays.asList(5,0,9,2,6,0,13))
                                .build()
                )
                .token(gameToken)
                .active(true)
                .build();

        int spotIndex = 3;
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate(game.getBoard());
        when(repository.findByTokenAndActiveTrue(gameToken)).thenAnswer(invocationOnMock -> Optional.ofNullable(model));
        when(transformer.transform(model)).thenAnswer(invocationOnMock -> game);
        when(boardManagerFactory.initiate(game.getBoard())).thenAnswer(invocationOnMock -> boardManager);
        when(executorService.invokeAll(anyList())).thenAnswer(invocationOnMock -> null);

        //when
        Game result = underTest.makeMove(gameToken, spotIndex);

        //then
        assertNotNull(result);
        assertEquals(gameToken, result.getToken());
        assertTrue(result.isActive());
        assertEquals(player1, result.getPrimaryPlayer());
        assertEquals(player2, result.getSecondaryPlayer());
        assertEquals(Arrays.asList(0,0,0,0,3,0,15), result.getBoard().getPrimarySide());
        assertEquals(Arrays.asList(0,0,9,2,6,0,13), result.getBoard().getSecondarySide());
        assertEquals(Side.SECONDARY, result.getBoard().getCurrentTurn());

    }

    @Test
    public void testMakeMoveBonusTurn() throws InterruptedException {
        //given
        Player player1 = TestPlayerDto.builder().username("pl_test1").build();
        Player player2 = TestPlayerDto.builder().username("pl_test2").build();
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .currentState(
                        BoardStateModel.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySideSpots(Arrays.asList(0,0,0,2,2,0,9))
                                .secondarySideSpots(Arrays.asList(5,0,9,2,6,0,13))
                                .build()
                )
                .build();
        String gameToken = "gm_test1";
        Game game = GameDto.builder()
                .primaryPlayer(player1)
                .secondaryPlayer(player2)
                .board(
                        BoardDto.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySide(Arrays.asList(0,0,0,2,2,0,9))
                                .secondarySide(Arrays.asList(5,0,9,2,6,0,13))
                                .build()
                )
                .token(gameToken)
                .active(true)
                .build();

        int spotIndex = 3;
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate(game.getBoard());

        when(repository.findByTokenAndActiveTrue(gameToken)).thenAnswer(invocationOnMock -> Optional.ofNullable(model));
        when(transformer.transform(model)).thenAnswer(invocationOnMock -> game);
        when(boardManagerFactory.initiate(game.getBoard())).thenAnswer(invocationOnMock -> boardManager);
        when(executorService.invokeAll(anyList())).thenAnswer(invocationOnMock -> null);

        //when
        Game result = underTest.makeMove(gameToken, spotIndex);

        //then
        assertNotNull(result);
        assertEquals(gameToken, result.getToken());
        assertTrue(result.isActive());
        assertEquals(player1, result.getPrimaryPlayer());
        assertEquals(player2, result.getSecondaryPlayer());
        assertEquals(Arrays.asList(0,0,0,0,3,0,15), result.getBoard().getPrimarySide());
        assertEquals(Arrays.asList(0,0,9,2,6,0,13), result.getBoard().getSecondarySide());
        assertEquals(Side.SECONDARY, result.getBoard().getCurrentTurn());

    }

    @Test
    public void testMakeMoveTurnBonus() throws InterruptedException {
        //given
        Player player1 = TestPlayerDto.builder().username("pl_test1").build();
        Player player2 = TestPlayerDto.builder().username("pl_test2").build();
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .currentState(
                        BoardStateModel.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySideSpots(Arrays.asList(0,0,4,1,1,6,7))
                                .secondarySideSpots(Arrays.asList(4,1,7,0,5,0,12))
                                .build()
                )
                .build();
        String gameToken = "gm_test1";
        Game game = GameDto.builder()
                .primaryPlayer(player1)
                .secondaryPlayer(player2)
                .board(
                        BoardDto.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySide(Arrays.asList(0,0,4,1,1,6,7))
                                .secondarySide(Arrays.asList(4,1,7,0,5,0,12))
                                .build()
                )
                .token(gameToken)
                .active(true)
                .build();

        int spotIndex = 2;
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate(game.getBoard());
        when(repository.findByTokenAndActiveTrue(gameToken)).thenAnswer(invocationOnMock -> Optional.ofNullable(model));
        when(transformer.transform(model)).thenAnswer(invocationOnMock -> game);
        when(boardManagerFactory.initiate(game.getBoard())).thenAnswer(invocationOnMock -> boardManager);
        when(executorService.invokeAll(anyList())).thenAnswer(invocationOnMock -> null);

        //when
        Game result = underTest.makeMove(gameToken, spotIndex);

        //then
        assertNotNull(result);
        assertEquals(gameToken, result.getToken());
        assertTrue(result.isActive());
        assertEquals(player1, result.getPrimaryPlayer());
        assertEquals(player2, result.getSecondaryPlayer());
        assertEquals(Arrays.asList(0,0,0,2,2,7,8), result.getBoard().getPrimarySide());
        assertEquals( Arrays.asList(4,1,7,0,5,0,12), result.getBoard().getSecondarySide());
        assertEquals(Side.PRIMARY, result.getBoard().getCurrentTurn());

    }

    @Test
    public void testMakeMoveFinishGame() throws InterruptedException {
        //given
        Player player1 = TestPlayerDto.builder().username("pl_test1").build();
        Player player2 = TestPlayerDto.builder().username("pl_test2").build();
        GameModel model = GameModel.builder().active(true).primaryPlayer("pl_test1")
                .secondaryPlayer("pl_test2")
                .startedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .currentState(
                        BoardStateModel.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySideSpots(Arrays.asList(0,0,0,0,0,1,16))
                                .secondarySideSpots(Arrays.asList(0,1,9,0,7,0,14))
                                .build()
                )
                .build();
        String gameToken = "gm_test1";
        Game game = GameDto.builder()
                .primaryPlayer(player1)
                .secondaryPlayer(player2)
                .board(
                        BoardDto.builder()
                                .currentTurn(Side.PRIMARY)
                                .primarySide(Arrays.asList(0,0,0,0,0,1,16))
                                .secondarySide(Arrays.asList(0,1,9,0,7,0,14))
                                .build()
                )
                .token(gameToken)
                .active(true)
                .build();

        int spotIndex = 5;
        BoardManager boardManager = new IndexedLinkedBoardManagerFactory().initiate(game.getBoard());
        when(repository.findByTokenAndActiveTrue(gameToken)).thenAnswer(invocationOnMock -> Optional.ofNullable(model));
        when(transformer.transform(model)).thenAnswer(invocationOnMock -> game);
        when(boardManagerFactory.initiate(game.getBoard())).thenAnswer(invocationOnMock -> boardManager);
        when(executorService.invokeAll(anyList())).thenAnswer(invocationOnMock -> null);

        //when
        Game result  = underTest.makeMove(gameToken, spotIndex);

        //then
        assertNotNull(result);
        assertEquals(gameToken, result.getToken());
        assertFalse(result.isActive());
        assertEquals(player1, result.getPrimaryPlayer());
        assertEquals(player2, result.getSecondaryPlayer());
        assertEquals(Arrays.asList(0,0,0,0,0,0,17), result.getBoard().getPrimarySide());
        assertEquals(Arrays.asList(0,0,0,0,0,0,31), result.getBoard().getSecondarySide());
        assertTrue(result.getBoard().isGameOver());
        assertEquals(player2 ,result.getWinner());
        assertEquals(Side.NONE, result.getBoard().getCurrentTurn());

    }
}