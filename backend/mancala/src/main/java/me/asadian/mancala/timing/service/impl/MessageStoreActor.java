package me.asadian.mancala.timing.service.impl;


import akka.actor.AbstractActor;

import lombok.extern.log4j.Log4j2;
import me.asadian.mancala.shared.events.GameTurnExpiredEvent;
import me.asadian.mancala.shared.events.GameTurnStartedEvent;
import me.asadian.mancala.timing.producer.GameTurnExpiredProducer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static me.asadian.mancala.shared.constants.game.GameConstants.PLAYER_TURN_TTL_IN_SECONDS;


@Log4j2
public class MessageStoreActor extends AbstractActor {

    private final Map<String, GameTurnStartedEvent> messageStore = new HashMap<>();
    private final GameTurnExpiredProducer gameTurnExpiredProducer;

    public MessageStoreActor(GameTurnExpiredProducer gameTurnExpiredProducer) {
        this.gameTurnExpiredProducer = gameTurnExpiredProducer;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(GameTurnStartedEvent.class, message -> {
                    // Store the message with TTL
                    log.info(String.format("%s with %s was stored",
                            message.getGameToken(), message.getStartedAt()));
                    String key = getKey(message.getGameToken(), message.getStartedAt());
                    messageStore.put(
                            key,
                            message);

                    getContext().getSystem().scheduler().scheduleOnce(
                            Duration.ofSeconds(PLAYER_TURN_TTL_IN_SECONDS),
                            getSelf(),
                            new TTLExpiredMessage(key),
                            getContext().getSystem().dispatcher(),
                            getSelf()
                    );
                })
                .match(TTLExpiredMessage.class, expiredMessage -> {
                    String messageId = expiredMessage.messageId();
                    log.info(String.format("%s with %s was expired",
                            messageId, messageStore.get(messageId).getStartedAt()));
                    GameTurnStartedEvent content = messageStore.get(messageId);
                    if (content != null) {
                        GameTurnStartedEvent gameTurnStartedEvent = messageStore.get(messageId);
                        gameTurnExpiredProducer.sendMessage(GameTurnExpiredEvent.builder()
                                .gameToken(gameTurnStartedEvent.getGameToken())
                                .expirationOf(gameTurnStartedEvent.getStartedAt())
                                .build());
                        messageStore.remove(messageId);
                    }
                })
                .build();
    }

    private static String getKey(String gameToken, long startedAt) {
        return String.format("%s_%s", gameToken, startedAt);
    }

    record TTLExpiredMessage(String messageId) {
    }
}