import React, { useState } from "react";
import { ProfileContainer } from "./player/ProfileContainer";
import { UnauthenticatedContainer } from "./player/UnauthenticatedContainer";

export const Waiting = (props) => {
  const [waiting, setWaiting] = useState(true);

  props.callback();

  return waiting && <h3>Waiting...</h3>;
};
