package me.asadian.mancala.player.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.asadian.mancala.shared.dto.users.Player;


@AllArgsConstructor
@Data
@Builder
public class PlayerDto implements Player {
    private String username;
    private String avatar;
}
