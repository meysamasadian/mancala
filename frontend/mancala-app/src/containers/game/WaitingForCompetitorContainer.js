import React, { useState, useEffect } from "react";
import { lookFor } from "../../api/lookFor";
import { WaitingForCompetitor } from "../../components/game/WaitingForCompetitor";

export const WaitingForCompetitorContainer = (props) => {
  const [player, setPlayer] = useState(props.player);
  const [timeout, setTimeout] = useState(0);

  useEffect(() => {
    const pollingInterval = setInterval(() => {
      lookFor(player, onCompetitorFound);
    }, 5000);

    return () => clearInterval(pollingInterval);
  }, []);

  const onCompetitorFound = (game) => {
    console.log("Game...", game);
    props.onGameStarted(game);
  };

  const onNotFound = () => {
    console.log("polling....");
    setTimeout(timeout + 1000);
    setInterval(() => lookFor(player, onCompetitorFound, onNotFound), timeout);
  };

  const search = () => {
    lookFor(player, onCompetitorFound, onNotFound);
  };

  return (
    <WaitingForCompetitor
      player={props.player}
      onJoinGameRequest={props.onJoinGameRequest}
    />
  );
};
