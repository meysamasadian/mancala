package me.asadian.mancala.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.asadian.mancala.shared.dto.game.Board;
import me.asadian.mancala.shared.constants.game.Side;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BoardDto implements Board {
    private List<Integer> primarySide;
    private List<Integer> secondarySide;
    private boolean gameOver;
    private Side currentTurn;
    private long currentTurnStartedAt;
    private int primaryScores;
    private int secondaryScores;
}
