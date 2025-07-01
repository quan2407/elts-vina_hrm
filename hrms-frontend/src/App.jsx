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
import EmployeeDetails from "./pages/EmployeeDetails";
import ApplyJob from "./pages/ApplyJob";
import EmployeeCreate from "./pages/EmployeeCreate";
import JobsManagement from "./pages/RecruitmentManagement";
import RecruitmentCreate from "./pages/RecruitmentCreate";
import RecruitmentDetailManagement from "./pages/JobsDetailManagement";
import CandidateManagement from "./pages/CandidateManagement";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import ProfilePage from "./pages/ProfilePage";
import ChangePasswordPage from "./pages/ChangePasswordPage";
import WorkScheduleManagement from "./pages/WorkScheduleManagement";
import AboutUs from "./pages/AboutUs";
import InterviewCreate from "./pages/InterviewCreate";
import InterviewManagement from "./pages/InterviewManagement";
import InterviewDetail from "./pages/InterviewDetail";

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
          path="/reset-password"
          element={<ResetPasswordPage />}
        />

        <Route
          path="/about-us"
          element={<AboutUs />} />

        <Route
          path="/jobs"
          element={<JobsPage />}
        />
        <Route
          path="/jobs/:id"
          element={<JobDetail />}
        />
        <Route
          path="/applyjob/:id"
          element={<ApplyJob />}
        />
        <Route
          path="/jobs-management"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <JobsManagement />
            </ProtectedRoute>
          }
        />
        <Route
          path="/recruitment-create"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <RecruitmentCreate />
            </ProtectedRoute>
          }
        />

        <Route
          path="/jobsdetail-management/:jobId"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <RecruitmentDetailManagement />
            </ProtectedRoute>
          }
        />

        <Route
          path="candidates-management/:jobId"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <CandidateManagement />
            </ProtectedRoute>
          }
        />

        <Route
          path="/add-interview/:id"
          element={

            <ProtectedRoute allowedRoles={["ROLE_HR"]}>

              <InterviewCreate />

            </ProtectedRoute>}

        />

        <Route
          path="/interviews-management"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <InterviewManagement />
            </ProtectedRoute>
          }
        />

        <Route
          path="/interviews-management/:id"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <InterviewDetail />
            </ProtectedRoute>
          }
        />

        <Route
          path="/unauthorized"
          element={<UnauthorizedPage />}
        />
        <Route
          path="/profile"
          element={<ProfilePage />}
        />

        {/* Protected routes */}
        <Route
          path="/work-schedule-management"
          element={
            <ProtectedRoute allowedRoles={["ROLE_PMC"]}>
              <WorkScheduleManagement />
            </ProtectedRoute>
          }
        />
        <Route
          path="/change-password"
          element={
            <ProtectedRoute
              allowedRoles={[
                "ROLE_ADMIN",
                "ROLE_HR",
                "ROLE_EMPLOYEE",
                "ROLE_LINE_LEADER",
                "ROLE_PMC",
                "ROLE_CANTEEN",
                "ROLE_PRODUCTION_MANAGER",
              ]}
            >
              <ChangePasswordPage />
            </ProtectedRoute>
          }
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
            <ProtectedRoute allowedRoles={["ROLE_HR", "ROLE_ADMIN"]}>
              <EmployeeManagement />
            </ProtectedRoute>
          }
        />
        <Route
          path="/employee-create"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR"]}>
              <EmployeeCreate />
            </ProtectedRoute>
          }
        />
        <Route
          path="/employees/:id"
          element={
            <ProtectedRoute allowedRoles={["ROLE_HR", "ROLE_ADMIN"]}>
              <EmployeeDetails />
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
      </Routes>
    </Router>
  );
}

export default App;
