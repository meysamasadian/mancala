import React, { useState } from "react";

export const Profile = (props) => {
  const [username, setUsername] = useState(props.player.username);
  const [avatar, setAvatar] = useState(props.player.avatar + ".png");

  const handleSubmit = (e) => {
    e.preventDefault();
    props.onJoinGameRequest();
  };

  const onLogout = () => {
    props.onLogout();
  };

  return (
    <div className="profile-container">
      <h2>Profile</h2>
      {props.error && <p className="error-message">props.error</p>}
      <div className="avatar-container">
        <img className="avatar-big" src={process.env.PUBLIC_URL + avatar} />
      </div>
      <form className="game-form" onSubmit={handleSubmit}>
        <h2>{username}</h2>
        <button type="submit">Start Game</button>
      </form>
      <button className="link-btn" onClick={onLogout}>
        Logout
      </button>
    </div>
  );
};
