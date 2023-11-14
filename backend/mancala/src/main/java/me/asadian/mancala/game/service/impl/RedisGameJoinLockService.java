package me.asadian.mancala.game.service.impl;

import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.service.GameJoinLockService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
@RequiredArgsConstructor
public class RedisGameJoinLockService implements GameJoinLockService {

    private static final String GAME_JOIN_LOCK_KEY_FORMAT = "join_lock_%s";
    private static final long GAME_JOIN_LOCK_LOCK_TTL_IS_SEC = 5;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean acquireLock(String gameToken) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(getKey(gameToken),
                gameToken,
                Duration.ofSeconds(GAME_JOIN_LOCK_LOCK_TTL_IS_SEC)
        ));
    }

    @Override
    public void releaseLock(String gameToken) {
        redisTemplate.opsForValue().getAndDelete(getKey(gameToken));
    }

    private static String getKey(String gameToken) {
        return String.format(GAME_JOIN_LOCK_KEY_FORMAT, gameToken);
    }
}
