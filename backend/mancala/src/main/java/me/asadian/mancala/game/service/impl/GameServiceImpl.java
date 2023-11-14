package me.asadian.mancala.game.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.game.aop.AuthorizedRequired;
import me.asadian.mancala.game.aop.JoinLockRequired;
import me.asadian.mancala.game.aop.TurnLockRequired;
import me.asadian.mancala.game.core.BoardManager;
import me.asadian.mancala.game.core.BoardManagerFactory;
import me.asadian.mancala.game.dto.BoardDto;
import me.asadian.mancala.game.dto.GameDto;
import me.asadian.mancala.game.exceptions.*;
import me.asadian.mancala.game.model.BoardStateModel;
import me.asadian.mancala.game.model.GameModel;
import me.asadian.mancala.game.producers.GameTurnStartedProducer;
import me.asadian.mancala.game.repository.GameRepository;
import me.asadian.mancala.game.service.GameService;
import me.asadian.mancala.game.service.GameTokenGenerator;
import me.asadian.mancala.game.service.GameTransformer;
import me.asadian.mancala.shared.constants.game.Side;
import me.asadian.mancala.shared.dto.game.Board;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.events.GameTurnStartedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static me.asadian.mancala.game.constants.GameConstants.RECENTLY_PERIOD;

@Service
@Log4j2
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository repository;
    private final BoardManagerFactory boardManagerFactory;
    private final GameTransformer transformer;
    private final ExecutorService executorService;
    private final GameTokenGenerator tokenGenerator;
    private final GameTurnStartedProducer turnStartedProducer;


    @Override
    public Game initiateGame(Player player) {
        GameModel gameModel = repository.save(GameModel.builder()
                .active(true)
                .token(tokenGenerator.generate())
                .primaryPlayer(player.getUsername())
                .createdAt(LocalDateTime.now())
                .build());

        return transformer.transform(gameModel);
    }

    @Override
    @JoinLockRequired
    public Game joinGame(Player player, String gameToken) {
        GameModel gameModel = repository
                .findByTokenAndActiveTrueAndSecondaryPlayerNull(
                        gameToken
                ).orElseThrow(() -> new GameNotFoundException("The game is no longer available"));

        BoardManager boardManager = boardManagerFactory.initiate();

        LocalDateTime gameStartedAt = LocalDateTime.now();

        gameModel.setSecondaryPlayer(player.getUsername());
        gameModel.setStartedAt(gameStartedAt);
        gameModel.setCurrentState(
                BoardStateModel.builder()
                        .primarySideSpots(boardManager.getBoard().getPrimarySide())
                        .secondarySideSpots(boardManager.getBoard().getSecondarySide())
                        .currentTurn(boardManager.getBoard().getCurrentTurn())
                        .currentTurnStartedAt(Timestamp.valueOf(gameStartedAt).getTime())
                        .build()
        );

        gameModel = repository.save(gameModel);

        turnStartedProducer.sendMessage(
                GameTurnStartedEvent.builder()
                        .playerUsername(gameModel.getPrimaryPlayer())
                        .gameToken(gameModel.getToken())
                        .side(gameModel.getCurrentState().getCurrentTurn())
                        .startedAt(Timestamp.valueOf(gameStartedAt).getTime())
                        .build());

        return transformer.transform(gameModel);
    }

    @Override
    public List<Game> listGamesWaitingForCompetitor() {
        return repository.findAllRecentlyWaitingForSecondaryPlayer(LocalDateTime.now().minusMinutes(RECENTLY_PERIOD))
                .stream().map(transformer::transform).toList();
    }

    @Override
    @AuthorizedRequired
    @Cacheable(value = "games", key="#gameToken")
    public Game getActiveGame(String gameToken) throws GameNotFoundException, GameWithoutPlayerException {
        GameModel gameModel = repository.findByTokenAndActiveTrue(gameToken)
                .orElseThrow(() -> new GameNotFoundException("The game is no longer available"));
        return transformer.transform(gameModel);
    }

    @Override
    @AuthorizedRequired
    public Game getGame(String gameToken) throws GameNotFoundException {
        GameModel gameModel = repository.findByToken(gameToken)
                .orElseThrow(() -> new GameNotFoundException("The game is no longer available"));
        return transformer.transform(gameModel);
    }


    @Override
    @TurnLockRequired
    @AuthorizedRequired
    @CacheEvict(value = "games", key="#gameToken", allEntries = true)
    public Game makeMove(String gameToken, int spotIndex) {
        Game game = getActiveGame(gameToken);

        BoardManager boardManager = game.getBoard() != null ?
                boardManagerFactory.initiate(game.getBoard()) :
                boardManagerFactory.initiate();

        try {
            boardManager.moveStonesOf(spotIndex);
        } catch (EmptySpotWasTouchedException | InvalidSpotWasTouchedException | GameOverException e) {
            throw new GameMovementException(e.getMessage(), e);
        }

        long turnStartedAt = System.currentTimeMillis();

        storeAndNotifyAsync(game, boardManager, turnStartedAt);

        return GameDto.builder()
                .primaryPlayer(game.getPrimaryPlayer())
                .secondaryPlayer(game.getSecondaryPlayer())
                .token(game.getToken())
                .active(!boardManager.getBoard().isGameOver())
                .board(createBoard(boardManager.getBoard(), turnStartedAt))
                .winner(getWinner(game, boardManager))
                .build();
    }

    private Board createBoard(Board board, long turnStartedAt) {
        return BoardDto.builder()
                .primarySide(board.getPrimarySide())
                .secondarySide(board.getSecondarySide())
                .currentTurn(board.getCurrentTurn())
                .currentTurnStartedAt(turnStartedAt)
                .primaryScores(board.getPrimaryScores())
                .secondaryScores(board.getSecondaryScores())
                .gameOver(board.isGameOver())
                .build();
    }

    private void storeAndNotifyAsync(Game game,
                                     BoardManager boardManager,
                                     long turnStartedAt) {
        try {
            executorService.invokeAll(Arrays.asList(
                    storeGameTask(game, boardManager, turnStartedAt),
                    informNewTurnTask(game, boardManager, turnStartedAt)
            ));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Player getWinner(Game game, BoardManager boardManager) {
        return getPlayerBySide(game, getWinnerSide(boardManager));
    }

    @NotNull
    private static Side getWinnerSide(BoardManager boardManager) {
        return boardManager.getBoard().getPrimaryScores() > boardManager.getBoard().getSecondaryScores() ?
                Side.PRIMARY :
                boardManager.getBoard().getPrimaryScores() < boardManager.getBoard().getSecondaryScores() ?
                        Side.SECONDARY : Side.NONE;
    }

    @Override
    @CacheEvict(value = "games", key="#gameToken", allEntries = true)
    public Game finishGameBy(Player player, String gameToken) throws GameNotFoundException {
        GameModel gameModel = repository.findByTokenAndActiveTrue(gameToken)
                .orElseThrow(() -> new GameNotFoundException("The game is no longer available"));

        gameModel.setFinishedAt(LocalDateTime.now());
        gameModel.setWinner(oppositeSide(gameModel, player));
        gameModel.setActive(false);
        repository.save(gameModel);

        return transformer.transform(gameModel);
    }

    private Side oppositeSide(GameModel gameModel, Player player) {
        return player.getUsername().equals(gameModel.getPrimaryPlayer()) ? Side.SECONDARY : Side.PRIMARY;
    }

    @Override
    public Optional<Game> getActiveGameByPlayer(Player player) {
        return repository.findByActiveTrueAndPrimaryPlayerOrSecondaryPlayer(player.getUsername(),
                player.getUsername()).map(transformer::transform);
    }

    private Callable<Void> storeGameTask(Game game, BoardManager boardManager, long turnStartedAt) {
        return () -> {
            Optional<GameModel> optionalGameModel = repository.findByToken(game.getToken());

            //Update History
            optionalGameModel.ifPresent(gameModel -> {
                //Add new State
                BoardStateModel newBoardState = BoardStateModel.builder()
                        .primarySideSpots(boardManager.getBoard().getPrimarySide())
                        .secondarySideSpots(boardManager.getBoard().getSecondarySide())
                        .currentTurn(boardManager.getBoard().getCurrentTurn())
                        .currentTurnStartedAt(turnStartedAt)
                        .build();

                gameModel.setCurrentState(newBoardState);

                //Check for game is over
                if (boardManager.getBoard().isGameOver()) {
                    gameModel.setWinner(
                            getWinnerSide(boardManager)
                    );
                    gameModel.setActive(false);
                    gameModel.setFinishedAt(LocalDateTime.now());
                }

                repository.save(gameModel);
            });

            return null;
        };
    }

    private Callable<Void> informNewTurnTask(Game game, BoardManager boardManager, long turnStartedAt) {
        return () -> {
            turnStartedProducer.sendMessage(
                    GameTurnStartedEvent.builder()
                            .playerUsername(getPlayerBySide(game, boardManager.getBoard().getCurrentTurn()).getUsername())
                            .gameToken(game.getToken())
                            .side(boardManager.getBoard().getCurrentTurn())
                            .startedAt(turnStartedAt)
                            .build()
            );

            return null;
        };
    }

    private Player getPlayerBySide(Game game, Side side) {
        return Side.PRIMARY.equals(side) ? game.getPrimaryPlayer() : game.getSecondaryPlayer();
    }

    private String getPlayerUsernameBySide(GameModel gameModel) {
        return Side.PRIMARY.equals(gameModel.getCurrentState().getCurrentTurn()) ? gameModel.getPrimaryPlayer() :
                Side.SECONDARY.equals(gameModel.getCurrentState().getCurrentTurn()) ? gameModel.getSecondaryPlayer() :
                        null;
    }

    @Override
    @CacheEvict(value = "games", key="#gameToken", allEntries = true)
    public void reverseTurn(String gameToken, long expirationOf) {
        repository.findByToken(gameToken)
                .filter(gameModel -> gameModel.getCurrentState() != null
                        && gameModel.getCurrentState().getCurrentTurnStartedAt() == expirationOf)
                .map(gameModel -> {
                    long delta= System.currentTimeMillis()-gameModel.getCurrentState().getCurrentTurnStartedAt();
                    log.info(String.format("%s has to switch turn after %d", gameModel.getToken(), delta));
                    return gameModel;
                })
                .ifPresent(gameModel ->
                        Optional.of(gameModel.getCurrentState())
                                .ifPresent(board -> {
                                    //Add new State
                                    long now = System.currentTimeMillis();
                                    BoardStateModel newBoardState = BoardStateModel.builder()
                                            .primarySideSpots(board.getPrimarySideSpots())
                                            .secondarySideSpots(board.getSecondarySideSpots())
                                            .currentTurn(board.getCurrentTurn().reverse())
                                            .currentTurnStartedAt(now)
                                            .build();

                                    gameModel.setCurrentState(newBoardState);
                                    repository.save(gameModel);

                                    //Inform timing
                                    turnStartedProducer.sendMessage(
                                            GameTurnStartedEvent.builder()
                                                    .playerUsername(getPlayerUsernameBySide(gameModel))
                                                    .gameToken(gameToken)
                                                    .side(gameModel.getCurrentState().getCurrentTurn())
                                                    .startedAt(now)
                                                    .build()
                                    );
                                })
                );
    }
}
