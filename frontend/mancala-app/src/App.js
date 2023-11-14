import React, { useState } from "react";
import logo from "./logo.svg";
import "./App.css";
import { MainContainer } from "./containers/MainContainer";

function App() {
  const [currentForm, setCurrentForm] = useState("login");

  const toggleForm = (formName) => {
    setCurrentForm(formName);
  };

  return (
    <div className="App">
      <MainContainer />
    </div>
  );
}

export default App;
