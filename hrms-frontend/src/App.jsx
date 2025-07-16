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
import BenefitManagement from "./pages/BenefitManagementHR.jsx";
import EmployeeCreate from "./pages/EmployeeCreate";
import JobsManagement from "./pages/RecruitmentManagement";
import RecruitmentCreate from "./pages/RecruitmentCreate";
import RecruitmentDetailManagement from "./pages/JobsDetailManagement";
import CandidateManagement from "./pages/CandidateManagement";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import ProfilePage from "./pages/ProfilePage";
import ChangePasswordPage from "./pages/ChangePasswordPage";
import WorkScheduleManagement from "./pages/WorkScheduleManagement";
import WorkScheduleProductionView from "./pages/WorkScheduleProductionView";
import AboutUs from "./pages/AboutUs";
import { App as AntdApp, ConfigProvider } from "antd";
import InterviewCreate from "./pages/InterviewCreate";
import InterviewManagement from "./pages/InterviewManagement";
import AttendanceMonthlyView from "./pages/AttendanceMonthlyView";
import InterviewDetail from "./pages/InterviewDetail";
import Dashboard from "./pages/Dashboard.jsx";
import ResetPasswordRequestsPage from "./pages/ResetPasswordRequestsPage";
import SalaryMonthlyView from "./pages/SalaryMonthlyView";
import LineManagement from "./pages/LineManagementPMC.jsx";
import EmployeeInLineManagement from "./pages/EmployeeInLineManagement.jsx";
import EmployeeWorkScheduleView from "./pages/EmployeeWorkScheduleView";
import HolidayManagement from "./pages/HolidayManagement";
import AccountRequestPage from "./pages/AccountRequestPage.jsx";

function App() {
  return (
    <ConfigProvider>
      <AntdApp>
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
              element={<AboutUs />}
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
              path="/applyjob/:id"
              element={<ApplyJob />}
            />
            <Route
              path="/admin/account-requests"
              element={
                <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                  <AccountRequestPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/holiday-management"
              element={
                <ProtectedRoute allowedRoles={["ROLE_HR"]}>
                  <HolidayManagement />
                </ProtectedRoute>
              }
            />

            <Route
              path="/dashboard"
              element={
                <ProtectedRoute allowedRoles={["ROLE_HR"]}>
                  <Dashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/my-work-schedule"
              element={
                <ProtectedRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                  <EmployeeWorkScheduleView />
                </ProtectedRoute>
              }
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
              path="/admin/reset-password-requests"
              element={
                <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                  <ResetPasswordRequestsPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/attendance-monthly"
              element={
                <ProtectedRoute allowedRoles={["ROLE_HR"]}>
                  <AttendanceMonthlyView />
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
                </ProtectedRoute>
              }
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
              path="/salary-monthly"
              element={
                <ProtectedRoute allowedRoles={["ROLE_HR"]}>
                  <SalaryMonthlyView />
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
              path="/employees/:id"
              element={
                <ProtectedRoute allowedRoles={["ROLE_HR", "ROLE_ADMIN"]}>
                  <EmployeeDetails />
                </ProtectedRoute>
              }
            />
            <Route
              path="/work-schedule-management"
              element={
                <ProtectedRoute allowedRoles={["ROLE_PMC"]}>
                  <WorkScheduleManagement />
                </ProtectedRoute>
              }
            />
            <Route
              path="/work-schedule-production"
              element={
                <ProtectedRoute allowedRoles={["ROLE_PRODUCTION_MANAGER"]}>
                  <WorkScheduleProductionView />
                </ProtectedRoute>
              }
            />
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

            {/* Module's Benefit */}
            <Route
              path="/benefit"
              element={
                <ProtectedRoute allowedRoles={["ROLE_HR", "ROLE_USER"]}>
                  <BenefitManagement />
                </ProtectedRoute>
              }
            />

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
              path="/line-management"
              element={
                <ProtectedRoute allowedRoles={["ROLE_PMC"]}>
                  <LineManagement />
                </ProtectedRoute>
              }
            />
            <Route
              path="/employee/line/:id"
              element={
                <ProtectedRoute allowedRoles={["ROLE_PMC"]}>
                  <EmployeeInLineManagement />
                </ProtectedRoute>
              }
            />

            <Route path="" />
          </Routes>
        </Router>
      </AntdApp>
    </ConfigProvider>
  );
}

export default App;
