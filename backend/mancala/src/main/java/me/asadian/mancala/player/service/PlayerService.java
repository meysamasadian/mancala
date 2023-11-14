package me.asadian.mancala.player.service;

import me.asadian.mancala.player.dto.PlayerRegistrationRequest;
import me.asadian.mancala.player.exceptions.PlayerRegistrationInvalidDataException;
import me.asadian.mancala.shared.dto.users.Player;

import java.util.Optional;

public interface PlayerService {
    /**
     * This method load the useful data of a player
     * @param username is a safe identifier to identify a specific user
     * @return Player
     */
    Optional<Player> getPlayer(String username);


    /**
     * This method receives a request to register a specific user
     * @param request request contains the registration data of an user
     * @return Player
     */

    Player register(PlayerRegistrationRequest request) throws PlayerRegistrationInvalidDataException;
}
