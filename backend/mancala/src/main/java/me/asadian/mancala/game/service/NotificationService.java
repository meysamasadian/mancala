package me.asadian.mancala.game.service;


import me.asadian.mancala.game.ws.message.GameMessage;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface NotificationService {
    Flux<ServerSentEvent<GameMessage<Game>>> getWaitingToJoinNotification(Player player);

    void addWaitingToJoinNotification(Game game, Player player);


}
