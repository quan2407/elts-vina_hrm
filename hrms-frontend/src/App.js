import React from "react";
import { Routes, Route } from "react-router-dom";

import LoginPage from "./pages/LoginPage";
import AccountManagement from "./pages/AccountManagement";
import EmployeeManagement from "./pages/EmployeeManagement";
import JobsPage from "./pages/JobsPage";
import JobDetailPage from "./pages/JobDetailPage";
import UnauthorizedPage from "./pages/UnauthorizedPage";

import ProtectedRoute from "./routes/ProtectedRoute";

import "./App.css";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route
          path="/"
          element={<LoginPage />}
        />

        <Route
          path="/accounts"
          element={
            <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
              <AccountManagement />
            </ProtectedRoute>
          }
        />

        <Route
          path="/employee-management"
          element={
            <ProtectedRoute allowedRoles={["ROLE_EMPLOYEE", "ROLE_ADMIN"]}>
              <EmployeeManagement />
            </ProtectedRoute>
          }
        />

        <Route
          path="/jobs"
          element={<JobsPage />}
        />
        <Route
          path="/jobs/:id"
          element={<JobDetailPage />}
        />
        <Route
          path="/unauthorized"
          element={<UnauthorizedPage />}
        />
      </Routes>
    </div>
  );
}

export default App;
