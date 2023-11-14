import React from "react";
import ReactDOM from "react-dom/client";
import App from "./frontend/components/App";
import "./frontend/components/styles/index.css";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
