package me.asadian.mancala.player.service;

import me.asadian.mancala.player.model.UserModel;
import me.asadian.mancala.shared.dto.users.Player;

public interface PlayerTransformer {
    Player transform(UserModel playerModel);
}
