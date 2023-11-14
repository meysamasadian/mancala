package me.asadian.mancala.player.controller;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.player.dto.AuthenticationRequest;
import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import me.asadian.mancala.player.dto.PlayerResponse;
import me.asadian.mancala.player.exceptions.PlayerRegistrationInvalidDataException;
import me.asadian.mancala.player.service.AuthenticationService;
import me.asadian.mancala.player.service.PlayerService;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/api/v1/players/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final AuthenticationService authenticationService;

    @GetMapping("/")
    public ResponseEntity<PlayerResponse> getPlayer() {
        Player currentUser = authenticationService.getCurrentUser();

        var response = PlayerResponse.builder()
                .username(currentUser.getUsername())
                .avatar(currentUser.getAvatar())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerPlayer(@RequestBody PlayerRegistrationRequest request) {
        try {
            Player registeredPlayer = playerService.register(request);
            var bearerToken = authenticationService.getAuthToken(registeredPlayer);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Authorization",  bearerToken)
                    .build();
        } catch (PlayerRegistrationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new HashMap<>() {{
                        put("message", e.getMessage());
                    }});
        }
    }


    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@RequestBody AuthenticationRequest request) {
        var bearerToken  = authenticationService.authenticate(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .header("Authorization",  bearerToken)
                .build();
    }
}