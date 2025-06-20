import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import JobsPage from "./pages/JobsPage";
import JobDetail from "./pages/JobDetail";
import LoginPage from "./pages/LoginPage";
import UnauthorizedPage from "./pages/UnauthorizedPage";
import AccountManagement from "./pages/AccountManagement";
import ProtectedRoute from "./routes/ProtectedRoute";
import EmployeeManagement from "./pages/EmployeeManagement";
import EmployeeDetails from "./pages/EmployeeDetails"; // ✅ Đổi tên import cho đúng

function App() {
  return (
    <Router>
      <Routes>
        {/* Public routes */}
        <Route
          path="/"
          element={<LoginPage />}
        />
        <Route
          path="/jobs"
          element={<JobsPage />}
        />
        <Route
          path="/jobs/:id"
          element={<JobDetail />}
        />
        <Route
          path="/unauthorized"
          element={<UnauthorizedPage />}
        />

        {/* Protected route for admin */}
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
            <ProtectedRoute allowedRoles={["ROLE_HR", "ROLE_ADMIN"]}>
              <EmployeeManagement />
            </ProtectedRoute>
          }
        />

        {/* Catch all unmatched routes */}
        <Route
          path="*"
          element={
            <Navigate
              to="/"
              replace
            />
          }
        />
        <Route
          path="/employee-details"
          element={<EmployeeDetails />} // ✅ Đổi path cho đồng bộ với component
        />
      </Routes>
    </Router>
  );
}

export default App;
