package me.asadian.mancala.player.service;

import me.asadian.mancala.player.dto.AuthenticationRequest;
import me.asadian.mancala.shared.dto.users.Player;

public interface AuthenticationService {
    String getAuthToken(Player player);
    String authenticate(AuthenticationRequest request);

    Player getCurrentUser();
}
