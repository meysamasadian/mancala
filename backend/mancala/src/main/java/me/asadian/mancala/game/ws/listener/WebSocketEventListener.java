package me.asadian.mancala.game.ws.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.game.ws.message.GameMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String gameToken = (String) headerAccessor.getSessionAttributes().get("gameToken");
        Optional.ofNullable(username).ifPresent(
                playerUsername -> {
                    log.info(String.format("User %s was left the game %s", username, gameToken));
                    messageSendingOperations.convertAndSend("/topic/public", GameMessage.builder().build());
                }
        );
    }
}
