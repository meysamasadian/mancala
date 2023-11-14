package me.asadian.mancala.game.service.impl;

import lombok.AllArgsConstructor;
import me.asadian.mancala.game.dto.BoardDto;
import me.asadian.mancala.game.dto.GameDto;
import me.asadian.mancala.game.exceptions.GameWithoutPlayerException;
import me.asadian.mancala.game.model.BoardStateModel;
import me.asadian.mancala.game.model.GameModel;
import me.asadian.mancala.game.service.GameTransformer;
import me.asadian.mancala.shared.constants.game.Side;
import me.asadian.mancala.shared.dto.game.*;
import me.asadian.mancala.shared.dto.users.Player;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.springframework.stereotype.Component;
import java.util.Optional;



@Component
@AllArgsConstructor
public class GameTransformerImpl implements GameTransformer {


    private final PlayerServiceFacade playerService;

    @Override
    public Game transform(GameModel model) throws GameWithoutPlayerException {
        Player primaryPlayer = playerService.getPlayerByUsername(model.getPrimaryPlayer())
                .orElseThrow(() ->
                        new GameWithoutPlayerException("Something wrong is happened with other player"));
        Player secondaryPlayer = playerService.getPlayerByUsername(model.getSecondaryPlayer()).orElse(null);

        return GameDto.builder()
                .primaryPlayer(primaryPlayer)
                .secondaryPlayer(secondaryPlayer)
                .token(model.getToken())
                .winner(Optional.ofNullable(model.getWinner()).map(winner->
                    Side.PRIMARY.equals(winner) ? primaryPlayer :
                        Side.SECONDARY.equals(winner) ? secondaryPlayer : null
                ).orElse(null))
                .board(transform(model.getCurrentState()))
                .active(model.isActive())
                .build();
    }

    private Board transform(BoardStateModel boardModel) {
        return Optional.ofNullable(boardModel)
                .map(model -> BoardDto.builder()
                        .currentTurn(model.getCurrentTurn())
                        .currentTurnStartedAt(boardModel.getCurrentTurnStartedAt())
                        .primarySide(model.getPrimarySideSpots())
                        .secondarySide(model.getSecondarySideSpots())
                        .gameOver(model.isGameOver())
                        .build())
                .orElse(BoardDto.builder().build());
    }
}
