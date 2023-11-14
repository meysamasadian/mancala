package me.asadian.mancala.game.service;

import me.asadian.mancala.shared.dto.game.Game;
import me.asadian.mancala.shared.dto.users.Player;

import java.util.List;
import java.util.Optional;

public interface GameService {
    /**
     * When there's no waiting game, a new game will be initiated for this player
     * As soon as the game has been created this will be available in waiting list
     * @param player they're going to be the first player who joins the game as primary
     * @return Game
     */
    Game initiateGame(Player player);

    /**
     * When a player starts playing, they should be joined to a waiting game.
     * This game has already been initiated by another player which explained in
     * @see GameService::initiateGame()
     * @param player they're going to be the second player who joins the game as secondary
     * @param gameToken this is the game's identifier
     * @return Game
     */
    Game joinGame(Player player, String gameToken);


    /**
     * This method is supposed to list all games which just initiated and are waiting for competitors
     * @return List<Game>
     */
    List<Game> listGamesWaitingForCompetitor();

    /**
     * This method gets the game identifier and fetches the active Game object
     * @param gameToken this is the game's identifier
     * @return Game
     */
    Game getActiveGame(String gameToken);

    /**
     * This method gets the game identifier and fetches a Game object regardless it's active or not
     * @param gameToken this is the game's identifier
     * @return Game
     */
    Game getGame(String gameToken);

    /**
     * This method is going to handle a movement which a player owns.
     * The player who it's their turn can move the stones in one of their small spots
     * This stones based on the game rules are going to be distributed among the other spots
     * @param gameToken the game identifier
     * @param spotIndex the index of spot which player is going to move them
     * @return Game
     */
    Game makeMove(String gameToken, int spotIndex);

    /**
     * This method forces a game to be finished because of any unlogical reason coming from this player
     * Which means the game is finished and the competitor wins the game
     * @param player player who makes the game finished
     * @param gameToken the current game
     * @return Game
     */
    Game finishGameBy(Player player, String gameToken);


    /**
     * This method returns the active game of player
     * @param player player we need to check active game for them
     * @return Game
     */
    Optional<Game> getActiveGameByPlayer(Player player);


    /**
     * This method forcefully reverses the game turn
     * @param gameToken current game token
     */
    void reverseTurn(String gameToken, long expirationOf);

}
