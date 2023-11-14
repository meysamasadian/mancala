import React, { useState } from "react";
import { Profile } from "../../components/player/Profile";

export const ProfileContainer = (props) => {
  const [player, setPlayer] = useState(props.player);

  return (
    <Profile
      player={player}
      error={props.error}
      onLogout={props.onLogout}
      onJoinGameRequest={props.onJoinGameRequest}
    />
  );
};
