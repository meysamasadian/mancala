import React, { useState } from "react";
import { LoginContainer } from "./LoginContainer";
import { RegisterContainer } from "./RegisterContainer";

export const UnauthenticatedContainer = (props) => {
  const [currentForm, setCurrentForm] = useState("login");

  const switchLoginAndRegister = (current) => {
    setCurrentForm(current);
  };

  const onLoggedIn = (player) => {
    props.onLoggedIn(player);
  };

  if (currentForm === "login") {
    return (
      <LoginContainer
        onSwitch={switchLoginAndRegister}
        onLoggedIn={onLoggedIn}
      />
    );
  } else {
    return (
      <RegisterContainer
        onSwitch={switchLoginAndRegister}
        onLoggedIn={onLoggedIn}
      />
    );
  }
};
