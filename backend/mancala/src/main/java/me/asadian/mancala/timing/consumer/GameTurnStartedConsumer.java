package me.asadian.mancala.timing.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.shared.events.GameTurnStartedEvent;
import me.asadian.mancala.timing.service.TimeManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class GameTurnStartedConsumer {

    private final TimeManager timeManager;


    @KafkaListener(
            topics = "${spring.kafka.spring.topics.game-turn-started-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "turnStartedListenerContainerFactory"
    )
    public void listen(GameTurnStartedEvent message) {
        log.info(String.format("GameTurnStartedEvent for %s was received", message.getPlayerUsername()));
        timeManager.handle(message);
    }
}
