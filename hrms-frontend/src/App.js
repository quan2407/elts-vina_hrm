import React from "react";
import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import AccountManagement from "./pages/AccountManagement";
import EmployeeManagement from "./pages/EmployeeManagement";

import "./App.css";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/employee-management" element={<EmployeeManagement />} />
        <Route path="/accounts" element={<AccountManagement />} />
      </Routes>
    </div>
  );
}

export default App;
