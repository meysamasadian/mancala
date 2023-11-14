package me.asadian.mancala.game.core.impl;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.asadian.mancala.shared.constants.game.Side;

import java.util.Map;


@Data
@Builder
@EqualsAndHashCode

public class BoardSpot {
    @EqualsAndHashCode.Include
    private Side side;
    @EqualsAndHashCode.Include
    private boolean mancalaSpot;
    @EqualsAndHashCode.Include
    private int index;
    private Map<Side, BoardSpot> nextSpots;
    private BoardSpot oppositeSpot;
    private int stonesCount;

    public void increaseStonesBy(int stonesCount) {
        this.stonesCount+=stonesCount;
    }

    public int releaseStones() {
        int stonesCount = this.stonesCount;
        this.stonesCount = 0;
        return stonesCount;
    }

    public boolean isEmpty() {
        return stonesCount == 0;
    }
}