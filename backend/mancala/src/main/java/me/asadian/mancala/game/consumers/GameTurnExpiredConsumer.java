package me.asadian.mancala.game.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.game.service.GameService;
import me.asadian.mancala.shared.events.GameTurnExpiredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class GameTurnExpiredConsumer {

    private final GameService gameService;


    @KafkaListener(topics = "${spring.kafka.spring.topics.game-turn-expired-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "turnExpiredListenerContainerFactory"
    )
    public void listen(GameTurnExpiredEvent message) {
        log.info(String.format("GameTurnExpiredEvent for %s was received", message.getGameToken()));
        gameService.reverseTurn(message.getGameToken(), message.getExpirationOf());
    }
}
