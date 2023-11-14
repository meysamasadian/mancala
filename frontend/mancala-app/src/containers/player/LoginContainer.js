import React, { useState } from "react";
import { login } from "../../api/login";
import { getCurrentPlayer } from "../../api/profile";
import { Login } from "../../components/player/Login";

export const LoginContainer = (props) => {
  const [error, setError] = useState();

  const onLogin = (username, password) => {
    console.log(username);
    login(username, password, handleLogin, handleLoginError);
  };

  const handleLogin = (token) => {
    console.log("login", token);
    localStorage.removeItem("token");
    localStorage.setItem("token", token);
    getCurrentPlayer(token, handleGetCurrentPlayer, handleLoginError);
  };

  const handleGetCurrentPlayer = (player) => {
    props.onLoggedIn(player);
  };

  const handleLoginError = (err) => {
    console.log("error", err);
    setError(err);
  };

  return <Login onSwitch={props.onSwitch} onLogin={onLogin} error={error} />;
};
