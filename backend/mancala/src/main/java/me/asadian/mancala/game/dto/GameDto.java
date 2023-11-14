package me.asadian.mancala.game.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.asadian.mancala.shared.dto.game.Board;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;

import static me.asadian.mancala.shared.constants.game.Side.PRIMARY;
import static me.asadian.mancala.shared.constants.game.Side.SECONDARY;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GameDto implements Game {
    private Player primaryPlayer;
    private Player secondaryPlayer;
    private String token;
    private boolean active;
    private Board board;
    private Player winner;


    @Nullable
    public Player getCurrentTurn() {
        return PRIMARY.equals(board.getCurrentTurn()) ? primaryPlayer
                : SECONDARY.equals(board.getCurrentTurn()) ? secondaryPlayer : null;
    }
}
