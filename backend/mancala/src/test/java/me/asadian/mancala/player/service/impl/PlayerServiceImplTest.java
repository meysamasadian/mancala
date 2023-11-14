package me.asadian.mancala.player.service.impl;

import me.asadian.mancala.player.dto.PlayerDto;
import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import me.asadian.mancala.player.exceptions.PlayerRegistrationInvalidDataException;
import me.asadian.mancala.shared.security.Role;
import me.asadian.mancala.player.model.UserModel;
import me.asadian.mancala.player.repository.UserRepository;
import me.asadian.mancala.player.service.PlayerTransformer;
import me.asadian.mancala.shared.dto.users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerServiceImplTest {

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Mock
    private UserRepository repository;

    @Mock
    private PlayerTransformer transformer;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterPlayerSuccess() throws PlayerRegistrationInvalidDataException {
        // given
        PlayerRegistrationRequest validRequest = new PlayerRegistrationRequest("username", "StrongP@ssw0rd", "StrongP@ssw0rd", "avatar1");

        when(repository.findByUsername("username")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("StrongP@ssw0rd")).thenReturn("encodedPassword");


        // when
        playerService.register(validRequest);

        // then
        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);
        verify(repository).save(userModelArgumentCaptor.capture());
        var model = userModelArgumentCaptor.getValue();
        assertEquals("avatar1", model.getAvatar());
        assertEquals("username", model.getUsername());
        assertEquals("encodedPassword", model.getPassword());
        assertEquals(Role.GAMER, model.getRole());

        ArgumentCaptor<UserModel> secondUserModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);
        verify(repository).save(secondUserModelArgumentCaptor.capture());
        var secondModel = secondUserModelArgumentCaptor.getValue();
        assertEquals("avatar1", secondModel.getAvatar());
        assertEquals("username", secondModel.getUsername());
        assertEquals("encodedPassword", secondModel.getPassword());
        assertEquals(Role.GAMER, model.getRole());
    }

    @Test
    public void testRegisterPlayerUsernameDuplicated() {
        // Given
        PlayerRegistrationRequest duplicateRequest = new PlayerRegistrationRequest("existingUsername", "password", "password", "avatar");

        UserModel existingUserModel = UserModel.builder()
                .username("existingUsername")
                .password("encodedPassword")
                .avatar("avatar")
                .role(Role.GAMER)
                .build();

        when(repository.findByUsername("existingUsername")).thenReturn(Optional.of(existingUserModel));

        // When and Then
        assertThrows(PlayerRegistrationInvalidDataException.class, () -> playerService.register(duplicateRequest));
    }

    @Test
    public void testGetPlayerByUsernameSuccess() {
        // Given
        String username = "testUser";
        UserModel userModel = UserModel.builder()
                .avatar("avatar")
                .username(username)
                .password("encodedPassword")
                .role(Role.GAMER)
                .build();
        Player expectedPlayer = PlayerDto.builder()
                .avatar("avatar")
                .username(username)
                .build();

        when(repository.findByUsername(username)).thenReturn(Optional.of(userModel));
        when(transformer.transform(userModel)).thenReturn(expectedPlayer);

        // when
        Optional<Player> result = playerService.getPlayer(username);

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedPlayer, result.get());
    }

    @Test
    public void testGetPlayerByUsernameNotFound() {
        // given
        String username = "nonexistentUser";

        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        // when
        Optional<Player> result = playerService.getPlayer(username);

        // then
        assertTrue(result.isEmpty());
    }

}