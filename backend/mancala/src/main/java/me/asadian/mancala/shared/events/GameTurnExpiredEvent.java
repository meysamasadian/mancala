package me.asadian.mancala.shared.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameTurnExpiredEvent implements Serializable {
    private String gameToken;
    private long expirationOf;
}
