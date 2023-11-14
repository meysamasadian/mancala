import React, { useState, useEffect } from "react";
import { loadGame } from "../../api/loadGame.js";
import { leaveGame } from "../../api/leaveGame.js";
import { Game } from "../../components/game/Game";
import { GameResult } from "../../components/game/GameResult";
import { Waiting } from "../Waiting.js";
import { move } from "../../api/move.js";

export const GameContainer = (props) => {
  const [player, setPlayer] = useState(props.player);
  const [game, setGame] = useState();
  const [gameToken, setGameToken] = useState(props.gameToken);
  const [finished, setFinished] = useState();
  const [error, setError] = useState();

  useEffect(() => {
    const pollingInterval = setInterval(() => {
      if (!finished) {
        loadGame(game, onGameUpdated, onGameError);
      }
    }, 2000);

    return () => clearInterval(pollingInterval);
  }, []);

  const onGameUpdated = (game) => {
    setFinished(!game.active);
    setGame(game);
  };

  const onGameError = (error) => {
    setError(error);
  };

  const fetchGame = () => {
    loadGame(game, onGameUpdated, onGameError);
  };

  const onLeave = (player) => {
    if (game.active) {
      leaveGame(player, onLeaveCompleted, onLeaveFailed);
    }
  };

  const onLeaveCompleted = () => {
    props.onLeave(player);
  };

  const onLeaveFailed = (error) => {
    setError(error);
  };

  const onMove = (index) => {
    console.log("Running move in GC...", index);
    move(index, onGameUpdated, onGameError);
  };

  return game ? (
    finished ? (
      <GameResult game={game} player={player} onLeave={onLeaveCompleted} />
    ) : (
      <Game
        game={game}
        player={player}
        error={error}
        onLeave={onLeave}
        onMove={onMove}
      />
    )
  ) : (
    <Waiting callback={fetchGame} />
  );
};
