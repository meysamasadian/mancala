package me.asadian.mancala.game.aop;

import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.exceptions.GameNotFoundException;
import me.asadian.mancala.game.service.GameJoinLockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class GameJoinLockAspect {
    private final GameJoinLockService gameJoinLockService;

    @Around("@annotation(JoinLockRequired)")
    public Object applyLock(ProceedingJoinPoint joinPoint) throws Throwable {
        String gameToken = (String) joinPoint.getArgs()[1];

        if (gameJoinLockService.acquireLock(gameToken)) {
            try {
              return joinPoint.proceed();
            } finally {
                gameJoinLockService.releaseLock(gameToken);
            }
        } else {
            throw new GameNotFoundException("The game s no longer available");
        }
    }
}
