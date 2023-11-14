package me.asadian.mancala.timing.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.shared.events.GameTurnExpiredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class GameTurnExpiredProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.spring.topics.game-turn-expired-topic}")
    private String topicName;

    public void sendMessage(GameTurnExpiredEvent message) {
        kafkaTemplate.send(topicName, message);
    }
}
