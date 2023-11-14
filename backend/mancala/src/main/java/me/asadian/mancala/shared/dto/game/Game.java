package me.asadian.mancala.shared.dto.game;

import jakarta.annotation.Nullable;
import me.asadian.mancala.shared.dto.users.Player;

import java.io.Serializable;

public interface Game extends Serializable {

    String getToken();

    Player getPrimaryPlayer();

    Player getSecondaryPlayer();

    boolean isActive();

    Board getBoard();


    @Nullable
    Player getCurrentTurn();

    Player getWinner();

}
