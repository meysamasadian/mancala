import React, { useState } from "react";
import Countdown from "./Countdown";

export const GameResult = (props) => {
  const [error, setError] = useState(props.error);
  const [currentPlayer, seyCurrentPlayer] = useState(props.player);
  const [game, setGame] = useState(props.game);
  const [primaryPlayer, setPrimaryPlayer] = useState(props.game.primaryPlayer);
  const [secondaryPlayer, setSecondaryPlayer] = useState(
    props.game.secondaryPlayer
  );
  const [board, setBoard] = useState(props.game.board);
  const [winner, setWinner] = useState(props.game.winner);
  const [result, setResult] = useState(
    props.game.winner
      ? props.game.winner.username === props.player.username
        ? "You won the Game!"
        : "You lost the Game"
      : "The Game was draw!"
  );
  const [resultTitle, setResultTitle] = useState(
    props.game.winner
      ? props.game.winner.username === props.player.username
        ? "Congratulations!"
        : "Sorry!"
      : "Not bad!"
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

  return (
    <div className="game-container">
      {error && <p className="error-message">error</p>}
      <div className="competitor-container">
        <div className={primaryPlayerClass}>
          <img
            className="competitor-avatar-left"
            src={primaryPlayer.avatar + ".png"}
          />
          <div className="competitor-score-left">
            <h2 className="username">{primaryPlayer.username}</h2>
            <h3 className="score">{game.board.primarySide[6]}</h3>
          </div>
        </div>
        <div className={secondaryPlayerClass}>
          <img
            className="competitor-avatar-right"
            src={secondaryPlayer.avatar + ".png"}
          />
          <div className="competitor-score-right">
            <h2 className="username">{secondaryPlayer.username}</h2>
            <h3 className="score">{game.board.secondarySide[6]}</h3>
          </div>
        </div>
      </div>
      <div className="game-result-container">
        <h1 className="game-result-title">{resultTitle}</h1>
        <h2 className="game-result">{result}</h2>
        <button onClick={props.onLeave} className="leave-button">
          Leave!
        </button>
      </div>
    </div>
  );
};
