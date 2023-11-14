package me.asadian.mancala.game.aop;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.exceptions.WrongTurnException;
import me.asadian.mancala.game.service.GameTurnLockService;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Optional;

@Aspect
@RequiredArgsConstructor
public class GameTurnLockAspect {

    private final GameTurnLockService lockService;
    private final PlayerServiceFacade playerService;

    @Before("@annotation(TurnLockRequired)")
    public void hasLocked(JoinPoint joinPoint) throws WrongTurnException {
        String gameToken = (String) joinPoint.getArgs()[0];
        Player currentPlayer = playerService.getCurrentPlayer();
        lockService.checkLock(gameToken, currentPlayer);
    }

    @After("@annotation(TurnLockRequired)")
    public void flipLock(Game game) {
        Optional.ofNullable(game.getCurrentTurn()).ifPresentOrElse(
                player -> lockService.acquireLock(game.getToken(), player),
                () -> lockService.releaseLock(game.getToken())
        );

    }

}
