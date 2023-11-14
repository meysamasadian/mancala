import React, { useState } from "react";
import Countdown from "./Countdown";

export const Game = (props) => {
  const [error, setError] = useState(props.error);
  const [currentPlayer, seyCurrentPlayer] = useState(props.player);
  const [primaryPlayer, setPrimaryPlayer] = useState(props.game.primaryPlayer);
  const [secondaryPlayer, setSecondaryPlayer] = useState(
    props.game.secondaryPlayer
  );
  const [winner, setWinner] = useState(props.game.board);
  const [primarySpotClass, setPrimarySpotClass] = useState(
    props.player.username === props.game.primaryPlayer.username
      ? "pot primary-pot active-pot"
      : "pot primary-pot deactived-pot"
  );
  const [secondarySpotClass, setSecondarySpotClass] = useState(
    props.player.username === props.game.secondaryPlayer.username
      ? "pot secondary-pot active-pot"
      : "pot secondary-pot deactived-pot"
  );
  const [primaryMancalaSpotClass, setPrimaryMancalaSpotClass] = useState(
    props.player.username === props.game.primaryPlayer.username
      ? "pot primary-pot active-mancala-pot"
      : "pot primary-pot deactived-mancala-pot"
  );
  const [secondaryMancalaSpotClass, setSecondaryMancalaSpotClass] = useState(
    props.player.username === props.game.secondaryPlayer.username
      ? "pot secondary-pot active-mancala-pot"
      : "pot secondary-pot deactived-mancala-pot"
  );
  const [primaryPlayerClass, setPrimaryPlayerClass] = useState(
    props.player.username === props.game.primaryPlayer.username
      ? "primary-current-player competitor-left"
      : "competitor-left"
  );
  const [secondaryPlayerClass, setSecondaryPlayerClass] = useState(
    props.player.username === props.game.secondaryPlayer.username
      ? "secondary-current-player competitor-right"
      : "competitor-right"
  );
  const handleSubmit = (e) => {
    e.preventDefault();
  };

  const onLeave = () => {
    props.onLeave(currentPlayer);
  };

  const move = (side, id, e) => {
    console.log("Mving...");
    if (
      (currentPlayer.username === primaryPlayer.username &&
        side === "primary") ||
      (currentPlayer.username === secondaryPlayer.username &&
        side === "secondary")
    ) {
      if (currentPlayer.username !== props.game.currentTurn.username) {
        setError("It's not your turn");
      } else {
        setError();
        props.onMove(id);
      }
    }
  };

  return (
    <div className="game-container">
      {error && <p className="error-message">{error}</p>}
      <div className="competitor-container">
        <div className="competitor-timer">
          {primaryPlayer.username === props.game.currentTurn.username && (
            <Countdown startedAt={props.game.board.currentTurnStartedAt} />
          )}
        </div>
        <div className="competitor-timer">
          {secondaryPlayer.username === props.game.currentTurn.username && (
            <Countdown startedAt={props.game.board.currentTurnStartedAt} />
          )}
        </div>
      </div>
      <div className="competitor-container">
        <div className={primaryPlayerClass}>
          <img
            className="competitor-avatar-left"
            src={primaryPlayer.avatar + ".png"}
          />
          <div className="competitor-score-left">
            <h2 className="username">{primaryPlayer.username}</h2>
            <h3 className="score">{props.game.board.primarySide[6]}</h3>
          </div>
        </div>
        <div>
          <p className="game-token">{props.game.token}</p>
          <button className="leave-button" onClick={onLeave}>
            Leave Game
          </button>
        </div>
        <div className={secondaryPlayerClass}>
          <img
            className="competitor-avatar-right"
            src={secondaryPlayer.avatar + ".png"}
          />
          <div className="competitor-score-right">
            <h2 className="username">{secondaryPlayer.username}</h2>
            <h3 className="score">{props.game.board.secondarySide[6]}</h3>
          </div>
        </div>
      </div>
      <div className="board">
        <div className="section endsection">
          <div className={primaryMancalaSpotClass} id="mb">
            <h1 className="mancala-bead">{props.game.board.primarySide[6]}</h1>
          </div>
        </div>
        <div className="section midsection">
          <div className="midrow topmid">
            <div
              className={primarySpotClass}
              onClick={(e) => move("primary", 5, e)}
              id="pt1"
            >
              <h1 className="bead">{props.game.board.primarySide[5]}</h1>
            </div>
            <div
              className={primarySpotClass}
              onClick={(e) => move("primary", 4, e)}
              id="pt2"
            >
              <h1 className="bead">{props.game.board.primarySide[4]}</h1>
            </div>
            <div
              className={primarySpotClass}
              onClick={(e) => move("primary", 3, e)}
              id="pt3"
            >
              <h1 className="bead">{props.game.board.primarySide[3]}</h1>
            </div>
            <div
              className={primarySpotClass}
              onClick={(e) => move("primary", 2, e)}
              id="pt4"
            >
              <h1 className="bead">{props.game.board.primarySide[2]}</h1>
            </div>
            <div
              className={primarySpotClass}
              onClick={(e) => move("primary", 1, e)}
              id="pt5"
            >
              <h1 className="bead">{props.game.board.primarySide[1]}</h1>
            </div>
            <div
              className={primarySpotClass}
              onClick={(e) => move("primary", 0, e)}
              id="pt6"
            >
              <h1 className="bead">{props.game.board.primarySide[0]}</h1>
            </div>
          </div>
          <div className="midrow botmid">
            <div
              className={secondarySpotClass}
              onClick={(e) => move("secondary", 0, e)}
              id="pb6"
            >
              <h1 className="bead">{props.game.board.secondarySide[0]}</h1>
            </div>
            <div
              className={secondarySpotClass}
              onClick={(e) => move("secondary", 1, e)}
              id="pb5"
            >
              <h1 className="bead">{props.game.board.secondarySide[1]}</h1>
            </div>
            <div
              className={secondarySpotClass}
              onClick={(e) => move("secondary", 2, e)}
              id="pb4"
            >
              <h1 className="bead">{props.game.board.secondarySide[2]}</h1>
            </div>
            <div
              className={secondarySpotClass}
              onClick={(e) => move("secondary", 3, e)}
              id="pb3"
            >
              <h1 className="bead">{props.game.board.secondarySide[3]}</h1>
            </div>
            <div
              className={secondarySpotClass}
              onClick={(e) => move("secondary", 4, e)}
              id="pb2"
            >
              <h1 className="bead">{props.game.board.secondarySide[4]}</h1>
            </div>
            <div
              className={secondarySpotClass}
              onClick={(e) => move("secondary", 5, e)}
              id="pb1"
            >
              <h1 className="bead">{props.game.board.secondarySide[5]}</h1>
            </div>
          </div>
        </div>
        <div className="section endsection">
          <div className={secondaryMancalaSpotClass} id="mt">
            <h1 className="mancala-bead">
              {props.game.board.secondarySide[6]}
            </h1>
          </div>
        </div>
      </div>
    </div>
  );
};
