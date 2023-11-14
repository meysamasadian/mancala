package me.asadian.mancala.game.service.impl;

import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.events.GameJoinRequestEvent;
import me.asadian.mancala.game.exceptions.GameNotFoundException;
import me.asadian.mancala.game.producers.GameJoinRequestProducer;
import me.asadian.mancala.game.service.CompetitionService;
import me.asadian.mancala.game.service.GameService;
import me.asadian.mancala.game.ws.message.GameMessage;
import me.asadian.mancala.game.ws.message.MessageType;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import static me.asadian.mancala.game.ws.constants.SocketAddress.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final PlayerServiceFacade playerServiceFacade;
    private final GameJoinRequestProducer gameJoinRequestProducer;
    private final GameService gameService;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void handleRequestToPlay() {
        Player currentPlayer = playerServiceFacade.getCurrentPlayer();

        terminatePreviousActiveGame(currentPlayer);

        gameJoinRequestProducer.sendMessage(GameJoinRequestEvent.builder()
                .requestedAt(LocalDateTime.now())
                .playerUsername(currentPlayer.getUsername())
                .build());
    }

    private void terminatePreviousActiveGame(Player currentPlayer) {
        gameService.getActiveGameByPlayer(currentPlayer).ifPresent(game -> {
            try {
                game = gameService.finishGameBy(currentPlayer, game.getToken());
                notifyOtherSide(game);
            } catch (GameNotFoundException ignored) {}
        });
    }

    @Override
    public void initiateOrStartGame(String playerUsername) {
        playerServiceFacade.getPlayerByUsername(playerUsername).ifPresent(
                player -> {
                    List<Game> waitingGames = gameService.listGamesWaitingForCompetitor();
                    for (Game game : waitingGames) {
                        try {
                            startGame(player, game);
                            return;
                        } catch (GameNotFoundException ignored) {}
                    }
                    initiateWaitingGame(player);
                }
        );
    }

    @Override
    public void leaveGame(String gameToken) {
        Player player = playerServiceFacade.getCurrentPlayer();
        gameService.finishGameBy(player, gameToken);
    }

    private void startGame(Player player, Game game) throws GameNotFoundException {
        game = gameService.joinGame(player, game.getToken());
        notifyBothSideGameStarted(game);
    }


    private void initiateWaitingGame(Player player) {
        Game game = gameService.initiateGame(player);
        messagingTemplate.convertAndSendToUser(
                player.getUsername(),
                GAME_PLAYER_SECURED_QUEUE,
                GameMessage.builder()
                        .type(MessageType.WAITING)
                        .gameToken(game.getToken())
                        .primaryPlayerUsername(player.getUsername())
                        .content(game)
                        .build()
        );
    }



    private void notifyBothSideGameStarted(Game game) {
        messagingTemplate.convertAndSend(GAME_SECURED_QUEUE, GameMessage.builder()
                .gameToken(game.getToken())
                .type(MessageType.JOIN)
                .primaryPlayerUsername(game.getPrimaryPlayer().getUsername())
                .secondaryPlayerUsername(game.getSecondaryPlayer().getUsername())
                .content(game)
                .build());
    }


    private void notifyOtherSide(Game game) {
        Optional.ofNullable(game.getWinner()).ifPresent(
                player ->
                        messagingTemplate.convertAndSendToUser(
                                game.getWinner().getUsername(),
                                GAME_PLAYER_SECURED_QUEUE,
                                GameMessage.builder()
                                        .type(MessageType.LEAVE)
                                        .primaryPlayerUsername(game.getPrimaryPlayer().getUsername())
                                        .secondaryPlayerUsername(game.getSecondaryPlayer().getUsername())
                                        .gameToken(game.getToken())
                                        .content(game)
                                        .build()
                        )
        );
    }
}
