package me.asadian.mancala.game.service;

import me.asadian.mancala.game.service.impl.RedisGameJoinLockService;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;


@RunWith(SpringRunner.class)
public class GameJoinLockServiceTest {
    @InjectMocks
    private RedisGameJoinLockService underTest;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private static final String GAME_TOKEN = "gameToken";
    private static final String LOCK_KEY = "join_lock_gameToken";

    @Before
    public void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.opsForValue().setIfAbsent(eq(LOCK_KEY), eq(GAME_TOKEN), any(Duration.class)))
                .thenReturn(true);
        when(redisTemplate.opsForValue().getAndDelete(LOCK_KEY)).thenReturn(GAME_TOKEN);
    }

    @Test
    public void testAcquireLock() {
        boolean result = underTest.acquireLock(GAME_TOKEN);
        assertTrue(result);
    }

    @Test
    public void testReleaseLock() {
        underTest.releaseLock(GAME_TOKEN);
        verify(redisTemplate.opsForValue()).getAndDelete(LOCK_KEY);
    }

}