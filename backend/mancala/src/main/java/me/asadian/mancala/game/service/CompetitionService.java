package me.asadian.mancala.game.service;


public interface CompetitionService {


    /**
     * This method receives a request from the current user and raise an event
     */
    void handleRequestToPlay();


    /**
     * This method either initiate a waitingGame or starts a game
     * @param playerUsername player username who's going to play
     */
    void initiateOrStartGame(String playerUsername);

    /**
     * This method allows the current user to leave the game
     * @param gameToken the current game
     */
    void leaveGame(String gameToken);

}
