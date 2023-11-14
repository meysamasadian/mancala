package me.asadian.mancala.game.service;

public interface GameJoinLockService {
    boolean acquireLock(String gameToken);

    void releaseLock(String gameToken);
}
