// main.jsx
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import "bootstrap/dist/css/bootstrap.min.css";
import { PermissionProvider } from "./contexts/PermissionContext"; // ✅ Thêm dòng này

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <PermissionProvider>
      {" "}
      {/* ✅ Bọc App ở đây */}
      <App />
    </PermissionProvider>
  </StrictMode>
);
