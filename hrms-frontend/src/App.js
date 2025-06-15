
import React from "react";
import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import AccountManagement from "./pages/AccountManagement";
import EmployeeManagement from "./pages/EmployeeManagement";

import JobsPage from "./pages/JobsPage";

import "./App.css";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<LoginPage />} />

        <Route path="/employee-management" element={<EmployeeManagement />} />
        <Route path="/accounts" element={<AccountManagement />} />

        <Route path="/jobs" element={<JobsPage />} />
        {/* <Route path="/jobs/:id" element={<JobDetailPage />} /> */}

      </Routes>
    </div>
  );
}

export default App;
