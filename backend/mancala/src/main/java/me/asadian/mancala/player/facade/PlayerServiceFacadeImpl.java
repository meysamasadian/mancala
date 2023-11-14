package me.asadian.mancala.player.facade;

import lombok.AllArgsConstructor;
import me.asadian.mancala.player.service.AuthenticationService;
import me.asadian.mancala.player.service.PlayerService;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@AllArgsConstructor
public class PlayerServiceFacadeImpl implements PlayerServiceFacade {

    private final PlayerService playerService;
    private final AuthenticationService authenticationService;

    @Override
    public Optional<Player> getPlayerByUsername(String username) {
        return playerService.getPlayer(username);
    }

    @Override
    public Player getCurrentPlayer() {
        return authenticationService.getCurrentUser();
    }
}
