import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "../assets/styles/Sidebar.css";
import { systemMenus, hrMenus } from "../config/sidebarMenus";

function Sidebar() {
  const navigate = useNavigate();
  const location = useLocation();

  const token = localStorage.getItem("accessToken");
  let username = "Mock User";
  let roles = [];

  if (token) {
    try {
      const decoded = jwtDecode(token);
      username = decoded.sub || "Unknown";
      roles = decoded.roles
        ? decoded.roles.map((r) => r.replace("ROLE_", "").toUpperCase())
        : [];
    } catch (err) {
      console.error("Invalid token", err);
    }
  }

  const isHrPage =
    location.pathname.startsWith("/employee-management") ||
    location.pathname.startsWith("/jobs") ||
    location.pathname.startsWith("/candidates");

  const menus = isHrPage ? hrMenus : systemMenus;

  return (
    <div className="sidebar">
      <div className="user-profile">
        <img
          className="profile-image"
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/8020efea94b33e133d3f1c7ace70ab7dcdce7dee?placeholderIfAbsent=true"
          alt="Profile"
        />
        <div className="user-name">{username}</div>
        <div className="user-role">{roles.join(", ") || "UNKNOWN"}</div>
      </div>

      <div className="features-section">
        {menus.map((item, index) => (
          <div
            key={index}
            className={`nav-item ${
              location.pathname === item.path ? "active" : ""
            }`}
            onClick={() => navigate(item.path)}
          >
            <div className="nav-icon">{item.icon}</div>
            <div className="nav-text">{item.text}</div>
            {item.badge && (
              <div className="notification-badge">
                <div className="notification-count">{item.badge}</div>
              </div>
            )}
          </div>
        ))}
      </div>

      <div
        className="logout-button"
        onClick={() => {
          localStorage.removeItem("accessToken");
          navigate("/");
        }}
      >
        <div className="nav-icon">
          <svg
            width="25"
            height="25"
            viewBox="0 0 25 25"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M19.1406 2.6416C22.2168 4.83887 24.2188 8.43262 24.2188 12.5C24.2188 19.1797 18.8086 24.5947 12.1338 24.6094C5.46876 24.624 0.00977454 19.1895 8.91193e-06 12.5195C-0.0048739 8.45215 1.99708 4.84863 5.06837 2.64648C5.63966 2.24121 6.43556 2.41211 6.77735 3.02246L7.54884 4.39453C7.83692 4.90723 7.7002 5.55664 7.22657 5.9082C5.2002 7.41211 3.90626 9.79492 3.90626 12.4951C3.90138 17.002 7.54395 20.7031 12.1094 20.7031C16.582 20.7031 20.3418 17.0801 20.3125 12.4463C20.2979 9.91699 19.1065 7.47559 16.9873 5.90332C16.5137 5.55176 16.3818 4.90234 16.6699 4.39453L17.4414 3.02246C17.7832 2.41699 18.5742 2.23633 19.1406 2.6416ZM14.0625 12.8906V1.17188C14.0625 0.522461 13.54 0 12.8906 0H11.3281C10.6787 0 10.1563 0.522461 10.1563 1.17188V12.8906C10.1563 13.54 10.6787 14.0625 11.3281 14.0625H12.8906C13.54 14.0625 14.0625 13.54 14.0625 12.8906Z"
              fill="white"
            />
          </svg>
        </div>
        <div className="nav-text logout-text">Log Out</div>
      </div>
    </div>
  );
}

export default Sidebar;
