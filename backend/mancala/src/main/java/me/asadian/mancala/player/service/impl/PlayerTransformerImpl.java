package me.asadian.mancala.player.service.impl;

import lombok.AllArgsConstructor;
import me.asadian.mancala.player.dto.PlayerDto;
import me.asadian.mancala.player.model.UserModel;
import me.asadian.mancala.player.service.PlayerTransformer;
import me.asadian.mancala.shared.dto.users.Player;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class PlayerTransformerImpl implements PlayerTransformer {

    @Override
    public Player transform(UserModel playerModel) {
        return PlayerDto.builder()
                .username(playerModel.getUsername())
                .avatar(playerModel.getAvatar())
                .build();
    }
}
