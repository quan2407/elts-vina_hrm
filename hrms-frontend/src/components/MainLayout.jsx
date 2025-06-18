import React from "react";
import Sidebar from "./Sidebar";
import "../styles/MainLayout.css";

const MainLayout = ({ children }) => {
  return (
    <div className="layout-container">
      <Sidebar />
      <div className="main-content">{children}</div>
    </div>
  );
};

export default MainLayout;
