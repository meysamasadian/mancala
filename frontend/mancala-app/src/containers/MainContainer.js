import React, { useState } from "react";
import { getCurrentPlayer } from "../api/profile";
import { WaitingForCompetitorContainer } from "./game/WaitingForCompetitorContainer";
import { GameContainer } from "./game/GameContainer";
import { ProfileContainer } from "./player/ProfileContainer";
import { UnauthenticatedContainer } from "./player/UnauthenticatedContainer";
import { Waiting } from "./Waiting";
import { joinGame } from "../api/join";

export const MainContainer = (props) => {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [player, setPlayer] = useState();
  const [joinGameRequest, setJoinGameRequest] = useState(false);
  const [error, setError] = useState();
  const [activeGameToken, setActiveGameToken] = useState(
    localStorage.getItem("gameToken")
  );
  const [game, setGame] = useState();

  const onUnathenticated = () => {
    localStorage.removeItem("token");
    setToken();
  };

  const onAthenticated = (player) => {
    setPlayer(player);
    setActiveGameToken();
    setGame();
    setJoinGameRequest();
    localStorage.removeItem("gameToken");
  };

  const loadCurrentUser = () => {
    getCurrentPlayer(token, setPlayer, onUnathenticated);
  };

  const onJoinGameRequest = () => {
    joinGame(player, onJoinGameRequestSuccess, onJoinGameRequestFailed);
  };

  const onJoinGameRequestSuccess = () => {
    setJoinGameRequest(true);
  };

  const onJoinGameRequestFailed = () => {
    setError("Try later!");
  };

  const onLeaveGame = () => {
    console.log("sdafasfsfdff");
    localStorage.removeItem("gameToken");
    setGame();
    setActiveGameToken();
    setJoinGameRequest(false);
  };

  const onLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("gameToken");
    setToken();
    setPlayer();
    setGame();
    setActiveGameToken();
  };

  const onGameStarted = (gameResponse) => {
    setGame(gameResponse.game);
    setActiveGameToken(gameResponse.game.token);
    localStorage.setItem("gameToken", gameResponse.game.token);
  };

  if (player) {
    if (!joinGameRequest && !activeGameToken) {
      return (
        <ProfileContainer
          onUnathenticated={onUnathenticated}
          player={player}
          error={error}
          onLogout={onLogout}
          onJoinGameRequest={onJoinGameRequest}
        />
      );
    } else {
      if (!activeGameToken) {
        return (
          <WaitingForCompetitorContainer
            player={player}
            onGameStarted={onGameStarted}
          />
        );
      } else {
        return (
          <GameContainer
            gameToken={activeGameToken}
            game={game}
            player={player}
            onLeave={onLeaveGame}
          />
        );
      }
    }
  } else {
    if (!token) {
      return <UnauthenticatedContainer onLoggedIn={onAthenticated} />;
    } else {
      return <Waiting callback={loadCurrentUser} />;
    }
  }
};
