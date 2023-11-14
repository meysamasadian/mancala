package me.asadian.mancala.game.ws.constants;



public interface SocketAddress {

    String GAME_SECURED_MAKE_MOVE = "/secured/game.makeMove";
    String GAME_SECURED_QUEUE = "/secured/game";
    String GAME_PLAYER_SECURED_QUEUE = "/secured/game/player";
    String GAME_PLAYER_ERROR_SECURED_QUEUE = "/secured/game/player/error";

}
