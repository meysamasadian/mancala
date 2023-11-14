package me.asadian.mancala.player.service.impl;

import me.asadian.mancala.player.model.UserModel;
import me.asadian.mancala.player.service.PlayerTransformer;
import me.asadian.mancala.shared.dto.users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlayerTransformerImplTest {


    private PlayerTransformer playerTransformer;

    @BeforeEach
    void setup() {
        playerTransformer = new PlayerTransformerImpl();
    }

    @Test
    void testTransform() {
        // Arrange
        UserModel playerModel = UserModel.builder()
                .id(1L)
                .username("testUser")
                .avatar("avatar1")
                .build();

        // Act
        Player playerDto = playerTransformer.transform(playerModel);

        // Assert
        assertEquals("testUser", playerDto.getUsername());
        assertEquals("avatar1", playerDto.getAvatar());
    }

}