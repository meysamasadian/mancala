package me.asadian.mancala.shared.constants.game;

public enum Side {
    PRIMARY,
    SECONDARY,
    NONE;

    public Side reverse() {
        return this.equals(PRIMARY) ? SECONDARY : this.equals(SECONDARY) ? PRIMARY : NONE;
    }
}
