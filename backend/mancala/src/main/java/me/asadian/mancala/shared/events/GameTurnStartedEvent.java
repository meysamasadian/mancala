package me.asadian.mancala.shared.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.asadian.mancala.shared.constants.game.Side;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GameTurnStartedEvent implements Serializable {
    private String gameToken;
    private Side side;
    private String playerUsername;
    private long startedAt;
}
