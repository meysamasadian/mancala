package me.asadian.mancala.game.service;

import me.asadian.mancala.game.exceptions.WrongTurnException;
import me.asadian.mancala.shared.dto.users.Player;

public interface GameTurnLockService {


    /**
     * Current player by calling this method checks id the lock is available
     * @param gameToken game token
     * @param player current player
     * @throws WrongTurnException thrown if the lock is occupied
     */
    void checkLock(String gameToken, Player player) throws WrongTurnException;


    /**
     * Current player by calling this method acquires the lock if the request applicable
     * @param gameToken game token
     * @param player current player
     */
    void acquireLock(String gameToken, Player player);


    /**
     * Release the current lock on a game
     * @param gameToken game token
     */
    void releaseLock(String gameToken);
}
