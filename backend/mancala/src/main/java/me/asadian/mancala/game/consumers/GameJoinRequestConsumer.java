package me.asadian.mancala.game.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.game.events.GameJoinRequestEvent;
import me.asadian.mancala.game.service.CompetitionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class GameJoinRequestConsumer {

    private final CompetitionService competitionService;


    @KafkaListener(topics = "${spring.kafka.spring.topics.game-join-request-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "gameJoinListenerContainerFactory"
    )
    public void listen(GameJoinRequestEvent message) {
        log.info(String.format("GameJoinRequestEvent for %s was received", message.getPlayerUsername()));
        competitionService.initiateOrStartGame(message.getPlayerUsername());
    }
}
