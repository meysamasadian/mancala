package me.asadian.mancala.shared.dto.game;



import me.asadian.mancala.shared.constants.game.Side;

import java.io.Serializable;
import java.util.List;

public interface Board extends Serializable {

    Side getCurrentTurn();
    long getCurrentTurnStartedAt();
    List<Integer> getPrimarySide();
    List<Integer> getSecondarySide();

    int getPrimaryScores();

    int getSecondaryScores();

    boolean isGameOver();

}
