import React, { useState } from "react";
import { AvatarSelector } from "./AvatarSelector";

export const Register = (props) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [avatar, setAvatar] = useState("avatar4");

  const handleSubmit = (e) => {
    e.preventDefault();
    props.onRegister(username, password, repeatPassword, avatar);
  };

  return (
    <div className="auth-form-container">
      <h2>Register</h2>
      {props.error && <p className="error-message">{props.error.message}</p>}
      {props.error &&
        props.error.message &&
        props.error.message.includes("password") &&
        !props.error.message.includes("Re-password") && (
          <ul className="error-message-details">
            <li>At least 8 characters and at most 20 characters in length.</li>
            <li>Atleast one uppercase letter (A-Z).</li>
            <li>At least one lowercase letter (a-z).</li>
            <li>At least one digit (0-9).</li>
            <li>At least one special character from the set [@#$%^&+=!*].</li>
            <li>No whitespace characters (spaces) are allowed.</li>
          </ul>
        )}
      <form className="register-form" onSubmit={handleSubmit}>
        <AvatarSelector onSelect={setAvatar} />
        <label htmlFor="username">User Name</label>
        <input
          value={username}
          name="username"
          onChange={(e) => setUsername(e.target.value)}
          id="username"
          placeholder="Username"
        />
        <label htmlFor="password">Password</label>
        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          placeholder="********"
          id="password"
          name="password"
        />
        <label htmlFor="re_password">Re-Password</label>
        <input
          value={repeatPassword}
          onChange={(e) => setRepeatPassword(e.target.value)}
          type="password"
          placeholder="********"
          id="repeatPassword"
          name="re_passwrepeatPasswordord"
        />
        <button type="submit">Register</button>
      </form>
      <button className="link-btn" onClick={() => props.onSwitch("login")}>
        Already have an account? Login here.
      </button>
    </div>
  );
};
