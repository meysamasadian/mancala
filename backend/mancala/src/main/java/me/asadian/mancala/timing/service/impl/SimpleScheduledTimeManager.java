package me.asadian.mancala.timing.service.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import me.asadian.mancala.shared.events.GameTurnStartedEvent;
import me.asadian.mancala.timing.producer.GameTurnExpiredProducer;
import me.asadian.mancala.timing.service.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SimpleScheduledTimeManager implements TimeManager {

    private final ActorRef messageStoreActor;

    public SimpleScheduledTimeManager(@Autowired GameTurnExpiredProducer turnExpiredProducer) {
        ActorSystem actorSystem = ActorSystem.create("MessageStoreSystem");
        messageStoreActor = actorSystem.actorOf(Props.create(MessageStoreActor.class, turnExpiredProducer),
                "messageStoreActor");
    }


    @Override
    public void handle(GameTurnStartedEvent event) {
        messageStoreActor.tell(event, ActorRef.noSender());
    }
}
