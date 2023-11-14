package me.asadian.mancala.game.service;


import me.asadian.mancala.game.exceptions.GameWithoutPlayerException;
import me.asadian.mancala.game.model.GameModel;
import me.asadian.mancala.shared.dto.game.Game;

public interface GameTransformer {
    Game transform(GameModel model) throws GameWithoutPlayerException;
}
