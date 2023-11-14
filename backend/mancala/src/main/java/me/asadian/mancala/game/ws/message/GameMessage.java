package me.asadian.mancala.game.ws.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class  GameMessage<T> {
    private MessageType type;
    private String gameToken;
    private T content;
    private String primaryPlayerUsername;
    private String secondaryPlayerUsername;
}
