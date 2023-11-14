package me.asadian.mancala.timing.service;

import me.asadian.mancala.shared.events.GameTurnStartedEvent;

public interface TimeManager {
    void handle(GameTurnStartedEvent event);
}
