package me.asadian.mancala.game.service.impl;

import me.asadian.mancala.game.service.NotificationService;
import me.asadian.mancala.game.ws.message.GameMessage;
import me.asadian.mancala.game.ws.message.MessageType;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Component
public class NotificationServiceImpl implements NotificationService {

    Map<String, GameMessage<Game>> waitingToJoinMessages = new HashMap<>();

    @Override
    public Flux<ServerSentEvent<GameMessage<Game>>> getWaitingToJoinNotification(Player player) {
        GameMessage<Game> message = waitingToJoinMessages.get(player.getUsername());
        return Flux.interval(Duration.ofSeconds(1)).map(sequence ->
                ServerSentEvent.<GameMessage<Game>>builder()
                        .id(message.getGameToken())
                        .event("game-waiting-to-join-event")
                        .data(message)
                        .build()
        );
    }

    @Override
    public void addWaitingToJoinNotification(Game game, Player player) {
        GameMessage<Game> message = GameMessage.<Game>builder()
                .type(MessageType.WAITING)
                .gameToken(game.getToken())
                .primaryPlayerUsername(player.getUsername())
                .content(game)
                .build();

        waitingToJoinMessages.put(player.getUsername(), message);
    }
}
