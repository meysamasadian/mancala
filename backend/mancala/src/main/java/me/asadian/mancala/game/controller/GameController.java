package me.asadian.mancala.game.controller;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.dto.GameResponse;
import me.asadian.mancala.game.exceptions.GameNotFoundException;
import me.asadian.mancala.game.service.GameService;
import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.facades.PlayerServiceFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final PlayerServiceFacade playerServiceFacade;

    @GetMapping("/active/{gameToken}")
    public ResponseEntity<GameResponse> getActiveGame(@PathVariable String gameToken) throws GameNotFoundException {
        Game game = gameService.getActiveGame(gameToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GameResponse.builder().game(game).build());
    }

    @GetMapping("/{gameToken}")
    public ResponseEntity<GameResponse> getGame(@PathVariable String gameToken) throws GameNotFoundException {
        Game game = gameService.getGame(gameToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GameResponse.builder().game(game).build());
    }

    @GetMapping("/find/player")
    public ResponseEntity<GameResponse> getStartedGameByPlayer() {
        return gameService.getActiveGameByPlayer(playerServiceFacade.getCurrentPlayer())
                .filter(game -> game.getSecondaryPlayer() != null)
                .map(game -> ResponseEntity.status(HttpStatus.OK).body(GameResponse.builder()
                        .game(game).build()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{gameToken}/move/{spotIndex}")
    public ResponseEntity<GameResponse> makeMove(@PathVariable String gameToken, @PathVariable int spotIndex) {

        Game game = gameService.makeMove(gameToken, spotIndex);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GameResponse.builder().game(game).build());
    }

}
