package me.asadian.mancala.player.controller;

import me.asadian.mancala.player.dto.AuthenticationRequest;
import me.asadian.mancala.player.dto.PlayerDto;
import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import me.asadian.mancala.player.exceptions.PlayerRegistrationInvalidDataException;
import me.asadian.mancala.player.service.AuthenticationService;
import me.asadian.mancala.player.service.PlayerService;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class PlayerControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @WithMockUser(username = "testUser", roles = "GAMER")
    public void testGetPlayer() throws Exception {
        // given
        Player currentUser = PlayerDto.builder().username("testUser").avatar("testAvatar").build();

        when(authenticationService.getCurrentUser()).thenReturn(currentUser);

        // when and then
        mockMvc.perform(get("/api/v1/players/player/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.avatar").value("testAvatar"));
    }

    @Test
    public void testRegisterPlayerSuccess() throws Exception {
        // given
        Player registeredPlayer = PlayerDto.builder().username("testUser").avatar("testAvatar").build();
        when(playerService.register(new PlayerRegistrationRequest("testUser", "testPassword", "testPassword", "testAvatar"))).thenReturn(registeredPlayer);
        when(authenticationService.getAuthToken(registeredPlayer)).thenReturn("Bearer testToken");

        // when and then
        mockMvc.perform(post("/api/v1/players/player/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"testPassword\",\"repeatPassword\":\"testPassword\",\"avatar\":\"testAvatar\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", "Bearer testToken"));
    }

    @Test
    public void testRegisterPlayerBadRequest() throws Exception {
        // given
        when(playerService.register(new PlayerRegistrationRequest("testUser", "testPassword", "testPassword", "testAvatar")))
                .thenThrow(new PlayerRegistrationInvalidDataException("Invalid data"));

        // when and then
        mockMvc.perform(post("/api/v1/players/player/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"testPassword\",\"repeatPassword\":\"testPassword\",\"avatar\":\"testAvatar\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        // Arrange
        when(authenticationService.authenticate(new AuthenticationRequest("testUser", "testPassword")))
                .thenReturn("Bearer testToken");

        // Act and Assert
        mockMvc.perform(post("/api/v1/players/player/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"testPassword\"}"))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Authorization", "Bearer testToken"));
    }
}