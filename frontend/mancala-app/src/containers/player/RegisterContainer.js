import React, { useState } from "react";
import { Register } from "./../../components/player/Register";
import { getCurrentPlayer } from "../../api/profile";
import { register } from "../../api/register";

export const RegisterContainer = (props) => {
  const [error, setError] = useState();

  const onRegister = (username, password, repeatPassword, avatar) => {
    console.log(username, password, repeatPassword, avatar);
    register(
      username,
      password,
      repeatPassword,
      avatar,
      handleRegister,
      handleRegisterError
    );
  };

  const handleRegister = (token) => {
    console.log("login", token);
    localStorage.removeItem("token");
    localStorage.setItem("token", token);
    getCurrentPlayer(token, handleGetCurrentPlayer, handleRegisterError);
  };

  const handleGetCurrentPlayer = (player) => {
    props.onLoggedIn(player);
  };

  const handleRegisterError = (err) => {
    console.log("error", err);
    err.then((error) => setError(error));
  };

  return (
    <Register onSwitch={props.onSwitch} onRegister={onRegister} error={error} />
  );
};
