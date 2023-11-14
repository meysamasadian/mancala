package me.asadian.mancala.game.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.asadian.mancala.shared.dto.users.Player;


@AllArgsConstructor
@Data
@Builder
public class TestPlayerDto implements Player {
    private String username;
    private String avatar;
}
