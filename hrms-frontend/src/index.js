import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import { BrowserRouter } from "react-router-dom"; // ✅ Thêm dòng này

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      {" "}
      {/* ✅ Bọc App trong BrowserRouter để dùng các hook điều hướng */}
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

// Nếu muốn đo hiệu năng, giữ lại như cũ
reportWebVitals();
