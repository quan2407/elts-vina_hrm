import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { LogOut, ChevronDown, ChevronUp } from "lucide-react";
import { systemMenus, hrMenus } from "../config/sidebarMenus";
import "../styles/Sidebar.css";

function Sidebar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [openMenu, setOpenMenu] = useState(""); // Track open submenu

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

  const menus = roles.includes("HR") ? hrMenus : systemMenus;

  const handleMenuClick = (item) => {
    if (item.children) {
      setOpenMenu(openMenu === item.text ? "" : item.text);
    } else {
      navigate(item.path);
    }
  };

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

      <div className="section-title">Menu</div>

      <div className="features-section">
        {menus.map((item, index) => {
          const isActive =
            location.pathname === item.path ||
            (item.children &&
              item.children.some((c) => location.pathname === c.path));
          const isSubOpen = openMenu === item.text;

          return (
            <div key={index}>
              <div
                className={`nav-item ${isActive ? "active" : ""}`}
                onClick={() => handleMenuClick(item)}
              >
                <div className="nav-icon">{item.icon(isActive)}</div>
                <div
                  className="nav-text"
                  style={{ color: isActive ? "#4f46e5" : "white" }}
                >
                  {item.text}
                </div>
                {item.children && (
                  <div className="nav-icon">
                    {isSubOpen ? (
                      <ChevronUp
                        size={16}
                        stroke="white"
                      />
                    ) : (
                      <ChevronDown
                        size={16}
                        stroke="white"
                      />
                    )}
                  </div>
                )}
                {item.badge && (
                  <div className="notification-badge">
                    <div className="notification-count">{item.badge}</div>
                  </div>
                )}
              </div>

              {item.children && (
                <div className={`submenu ${isSubOpen ? "open" : "closed"}`}>
                  {item.children.map((child, cIdx) => (
                    <div
                      key={cIdx}
                      className={`nav-item ${
                        location.pathname === child.path ? "active" : ""
                      }`}
                      onClick={() => navigate(child.path)}
                    >
                      <div className="nav-text">{child.text}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>

      <div
        className="logout-button"
        onClick={() => {
          localStorage.removeItem("accessToken");
          navigate("/");
        }}
      >
        <div className="nav-icon">
          <LogOut
            size={20}
            stroke="white"
          />
        </div>
        <div className="nav-text logout-text">Log Out</div>
      </div>
    </div>
  );
}

export default Sidebar;
