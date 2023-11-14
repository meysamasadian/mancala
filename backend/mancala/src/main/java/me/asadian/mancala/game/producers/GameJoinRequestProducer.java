package me.asadian.mancala.game.producers;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.events.GameJoinRequestEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameJoinRequestProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.spring.topics.game-join-request-topic}")
    private String topicName;

    public void sendMessage(GameJoinRequestEvent message) {
        kafkaTemplate.send(topicName, message);
    }
}
