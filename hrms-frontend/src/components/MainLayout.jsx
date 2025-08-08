import React from "react";
import Sidebar from "./Sidebar";
import Header from "./Header";
import "../styles/MainLayout.css";

const MainLayout = ({ children }) => {
  return (
    <div className="layout-container bg-light">
      <Sidebar />

      <div className="main-content">
        <Header />
        <div className="content-wrapper container-fluid">
          {children}
        </div>
      </div>
    </div>
  );
};

export default MainLayout;
