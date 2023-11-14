package me.asadian.mancala.game.producers;


import lombok.RequiredArgsConstructor;

import me.asadian.mancala.shared.events.GameTurnStartedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameTurnStartedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.spring.topics.game-turn-started-topic}")
    private String topicName;



    public void sendMessage(GameTurnStartedEvent message) {
        kafkaTemplate.send(topicName, message);
    }
}
