package me.asadian.mancala.shared.facades;

import me.asadian.mancala.shared.dto.users.Player;

import java.util.Optional;

public interface PlayerServiceFacade {
    Optional<Player> getPlayerByUsername(String username);

    Player getCurrentPlayer();
}
