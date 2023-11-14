package me.asadian.mancala.game.service.impl;

import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.exceptions.WrongTurnException;
import me.asadian.mancala.game.service.GameTurnLockService;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static me.asadian.mancala.shared.constants.game.GameConstants.PLAYER_TURN_TTL_IN_SECONDS;


@Component
@RequiredArgsConstructor
public class RedisGameTurnLockService implements GameTurnLockService {

    public static final String GAME_TURN_LOCK_KEY_FORMAT = "turn_lock_%s";

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void checkLock(String gameToken, Player player) throws WrongTurnException {
        String currentPlayerUsername = redisTemplate.opsForValue().get(getKey(gameToken));
        if (currentPlayerUsername != null && !currentPlayerUsername.equals(player.getUsername())) {
            throw new WrongTurnException("Invalid turn");
        }
    }

    @Override
    public void acquireLock(String gameToken, Player player) {
        redisTemplate.opsForValue().set(getKey(gameToken),
                player.getUsername(),
                Duration.ofSeconds(PLAYER_TURN_TTL_IN_SECONDS)
        );
    }

    @Override
    public void releaseLock(String gameToken) {
        redisTemplate.opsForValue().getAndDelete(getKey(gameToken));
    }


    private static String getKey(String gameToken) {
        return String.format(GAME_TURN_LOCK_KEY_FORMAT, gameToken);
    }
}
