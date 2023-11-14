import React, { useState } from "react";

export const Login = (props) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(props.error);

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(username);
    console.log(password);

    props.onLogin(username, password);
  };

  return (
    <div className="auth-form-container">
      <h2>Login</h2>
      {props.error && <p className="error-message">{props.error}</p>}
      <form className="login-form" onSubmit={handleSubmit}>
        <label htmlFor="username">Username</label>
        <input
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="username"
          id="username"
          name="username"
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
        <button type="submit">Log In</button>
      </form>
      <button className="link-btn" onClick={() => props.onSwitch("register")}>
        Don't have an account? Register here.
      </button>
    </div>
  );
};
