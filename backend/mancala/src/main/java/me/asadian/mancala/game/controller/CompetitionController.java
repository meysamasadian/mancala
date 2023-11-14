package me.asadian.mancala.game.controller;


import lombok.RequiredArgsConstructor;
import me.asadian.mancala.game.service.CompetitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @PostMapping("/join")
    public ResponseEntity<Void> requestToJoinGame() {
        competitionService.handleRequestToPlay();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{gameToken}/leave")
    public ResponseEntity<Void> leaveGame(@PathVariable String gameToken) {
        competitionService.leaveGame(gameToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
