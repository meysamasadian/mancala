package me.asadian.mancala.game.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.asadian.mancala.shared.dto.game.Game;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GameResponse {
    private Game game;
    private LocalDateTime currentTurnStartedAt;
}
