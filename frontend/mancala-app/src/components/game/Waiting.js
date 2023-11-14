import React, { useState } from "react";

export const Waiting = (props) => {
  const [username, setUsername] = useState("meysam");
  const [avatar, setAvatar] = useState("avatar1.png");

  const handleSubmit = (e) => {
    e.preventDefault();
  };

  return (
    <div className="waiting-container">
      <h2>Waiting For Competitor...</h2>
    </div>
  );
};
