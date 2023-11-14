package me.asadian.mancala.game.service;

import me.asadian.mancala.game.exceptions.WrongTurnException;
import me.asadian.mancala.game.service.impl.RedisGameTurnLockService;
import me.asadian.mancala.shared.dto.users.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static me.asadian.mancala.game.service.impl.RedisGameTurnLockService.GAME_TURN_LOCK_KEY_FORMAT;
import static me.asadian.mancala.shared.constants.game.GameConstants.PLAYER_TURN_TTL_IN_SECONDS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class GameTurnLockServiceTest {

    @InjectMocks
    private RedisGameTurnLockService underTest;

    @Mock
    private RedisTemplate<String, String> redisTemplate;


    private Player player;

    @Before
    public void setUp() {
        player = TestPlayerDto.builder().username("player1").build();
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
        when(redisTemplate.opsForValue().get("gameToken")).thenReturn(player.getUsername());
    }

    @Test
    public void testCheckLockWithNullUsername() throws WrongTurnException {
        String gameToken = "gameToken";

        when(redisTemplate.opsForValue().get(gameToken)).thenReturn(null);

        underTest.checkLock(gameToken, player);
    }

    @Test
    public void testCheckLockWithMatchingUsername() throws WrongTurnException {
        String gameToken = "gameToken";

        underTest.checkLock(gameToken, player);
    }

    @Test
    public void testCheckLockWithWrongUsername() {
        String gameToken = "gameToken";

        when(redisTemplate.opsForValue().get(String.format(GAME_TURN_LOCK_KEY_FORMAT, gameToken))).thenReturn("currentPlayerUsername");

        assertThrows(WrongTurnException.class, () -> underTest.checkLock(gameToken, player));
    }

    @Test
    public void testAcquireLock() {
        String gameToken = "gameToken";

        underTest.acquireLock(gameToken, player);

        verify(redisTemplate.opsForValue()).set(eq(String.format(GAME_TURN_LOCK_KEY_FORMAT, gameToken)), eq(player.getUsername()), eq(Duration.ofSeconds(PLAYER_TURN_TTL_IN_SECONDS)));
    }

    @Test
    public void testReleaseLock() {
        String gameToken = "gameToken";

        underTest.releaseLock(gameToken);

        verify(redisTemplate.opsForValue()).getAndDelete(eq(String.format(GAME_TURN_LOCK_KEY_FORMAT, gameToken)));
    }
}