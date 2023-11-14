package me.asadian.mancala.game.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.asadian.mancala.shared.constants.game.Side;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BoardStateModel {
    @Id
    @GeneratedValue
    private long id;
    private Side currentTurn;
    private long currentTurnStartedAt;
    private List<Integer> primarySideSpots;
    private List<Integer> secondarySideSpots;
    private boolean isGameOver;
    private Side touchedSpotSide;
    private int touchedSpotIndex;
}
