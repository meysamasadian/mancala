package me.asadian.mancala.game.service;


import static org.mockito.Mockito.*;

import me.asadian.mancala.game.dto.GameDto;
import me.asadian.mancala.game.events.GameJoinRequestEvent;
import me.asadian.mancala.game.exceptions.GameNotFoundException;
import me.asadian.mancala.game.producers.GameJoinRequestProducer;
import me.asadian.mancala.game.service.impl.CompetitionServiceImpl;
import me.asadian.mancala.game.ws.message.GameMessage;
import me.asadian.mancala.game.ws.message.MessageType;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class CompetitionServiceTest {

    @InjectMocks
    private CompetitionServiceImpl underTest;

    @Mock
    private PlayerServiceFacade playerServiceFacade;

    @Mock
    private GameJoinRequestProducer gameJoinRequestProducer;

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @Mock
    private GameService gameService;

    @Test
    public void testHandleRequestToPlay() throws GameNotFoundException {
        Player currentPlayer = TestPlayerDto.builder().username("testPlayer").build();
        Player otherPlayer = TestPlayerDto.builder().username("otherPlayer").build();

        Game activeGame = GameDto.builder().token("testGameToken")
                .primaryPlayer(currentPlayer)
                .secondaryPlayer(otherPlayer)
                .winner(otherPlayer).active(true).build();
        when(playerServiceFacade.getCurrentPlayer()).thenReturn(currentPlayer);
        when(gameService.getActiveGameByPlayer(currentPlayer)).thenReturn(Optional.of(activeGame));
        when(gameService.finishGameBy(currentPlayer, activeGame.getToken())).thenReturn(activeGame);


        underTest.handleRequestToPlay();

        verify(gameService, times(1)).finishGameBy(currentPlayer, activeGame.getToken());
        verify(gameJoinRequestProducer, times(1)).sendMessage(any(GameJoinRequestEvent.class));
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(otherPlayer.getUsername(),
                        "/secured/game/player",
                        GameMessage.builder()
                                .type(MessageType.LEAVE)
                                .primaryPlayerUsername(activeGame.getPrimaryPlayer().getUsername())
                                .secondaryPlayerUsername(activeGame.getSecondaryPlayer().getUsername())
                                .gameToken(activeGame.getToken())
                                .content(activeGame)
                                .build()
                );
    }

    @Test
    public void testInitiateOrStartGame() throws GameNotFoundException {
        Player player = TestPlayerDto.builder().username("testPlayer").build();
        Player otherPlayer = TestPlayerDto.builder().username("otherPlayer").build();
        String playerUsername = "testPlayer";
        when(playerServiceFacade.getPlayerByUsername(playerUsername)).thenReturn(Optional.of(player));
        List<Game> waitingGames = new ArrayList<>();
        Game waitingGame = GameDto.builder().token("waitingGameToken")
                .primaryPlayer(otherPlayer)
                .secondaryPlayer(player)
                .active(true).build();

        waitingGames.add(waitingGame);
        when(gameService.listGamesWaitingForCompetitor()).thenReturn(waitingGames);

        when(gameService.joinGame(player, waitingGame.getToken())).thenReturn(waitingGame);
        when(gameService.initiateGame(player)).thenReturn(GameDto.builder().token("newGameToken").active(true).build());

        underTest.initiateOrStartGame(playerUsername);

        verify(gameService, times(1)).joinGame(player, waitingGame.getToken());
        verify(gameService, times(0)).initiateGame(player);
        verify(gameJoinRequestProducer, times(0)).sendMessage(any(GameJoinRequestEvent.class));
        verify(messagingTemplate, times(1))
                .convertAndSend("/secured/game",
                        GameMessage.builder()
                                .type(MessageType.JOIN)
                                .primaryPlayerUsername(waitingGame.getPrimaryPlayer().getUsername())
                                .secondaryPlayerUsername(waitingGame.getSecondaryPlayer().getUsername())
                                .gameToken(waitingGame.getToken())
                                .content(waitingGame)
                                .build()
                );
    }

    @Test
    public void testInitiateOrStartGameNoWaitingGames() throws GameNotFoundException {
        Player player = TestPlayerDto.builder().username("testPlayer").build();
        String playerUsername = "testPlayer";
        Game initiatedGame = GameDto.builder().token("initiatedGame")
                .primaryPlayer(player)
                .active(true).build();

        when(playerServiceFacade.getPlayerByUsername(playerUsername)).thenReturn(Optional.of(player));
        when(gameService.listGamesWaitingForCompetitor()).thenReturn(new ArrayList<>());
        when(gameService.initiateGame(player)).thenReturn(initiatedGame);

        underTest.initiateOrStartGame(playerUsername);

        verify(gameService, times(1)).initiateGame(player);

        verify(gameJoinRequestProducer, times(0)).sendMessage(any(GameJoinRequestEvent.class));
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(player.getUsername(),
                        "/secured/game/player",
                        GameMessage.builder()
                                .type(MessageType.WAITING)
                                .primaryPlayerUsername(initiatedGame.getPrimaryPlayer().getUsername())
                                .gameToken(initiatedGame.getToken())
                                .content(initiatedGame)
                                .build()
                );
    }
}