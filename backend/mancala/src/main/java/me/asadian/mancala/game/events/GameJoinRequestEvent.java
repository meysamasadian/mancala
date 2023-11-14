package me.asadian.mancala.game.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GameJoinRequestEvent implements Serializable {
    private LocalDateTime requestedAt;
    private String playerUsername;
}
