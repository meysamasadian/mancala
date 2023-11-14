package me.asadian.mancala.game.controller;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.dto.GameMoveRequest;
import me.asadian.mancala.game.service.GameService;
import me.asadian.mancala.game.ws.message.GameMessage;
import me.asadian.mancala.game.ws.message.MessageType;
import me.asadian.mancala.shared.dto.game.Game;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import static me.asadian.mancala.game.ws.constants.SocketAddress.*;

@Controller
@RequiredArgsConstructor
public class LiveGameController {

    private final GameService gameService;

    @MessageMapping(GAME_SECURED_MAKE_MOVE)
    @SendTo(GAME_SECURED_QUEUE)
    public GameMessage<Game> makeMove(@Payload GameMessage<GameMoveRequest> movementMessage,
                         SimpMessageHeaderAccessor headerAccessor)
    {
        String gameToken = (String) headerAccessor.getHeader("gameToken");
        Game game = gameService.makeMove(gameToken, movementMessage.getContent().getSpotIndex());
        return GameMessage.<Game>builder()
                .type(MessageType.MOVEMENT)
                .primaryPlayerUsername(game.getPrimaryPlayer().getUsername())
                .secondaryPlayerUsername(game.getSecondaryPlayer().getUsername())
                .gameToken(gameToken)
                .content(game)
                .build();
    }

    @MessageExceptionHandler
    @SendToUser(GAME_PLAYER_ERROR_SECURED_QUEUE)
    public String handleException(Exception e) {
        return e.getMessage();
    }

}
