package me.asadian.mancala.game.aop;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.exceptions.UnauthorizedGameAccessException;
import me.asadian.mancala.game.repository.GameRepository;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAop {

    private final GameRepository gameRepository;
    private final PlayerServiceFacade playerServiceFacade;


    @Before("@annotation(AuthorizedRequired)")
    public void check(JoinPoint joinPoint) throws Throwable {
        String gameToken = (String)joinPoint.getArgs()[0];
        Player currentPlayer = playerServiceFacade.getCurrentPlayer();
        if (!gameRepository.existsByTokenAndPrimaryPlayerOrSecondaryPlayer(gameToken, currentPlayer.getUsername(), currentPlayer.getUsername())) {
            throw new UnauthorizedGameAccessException("Not allowed to access");
        }
    }
}
