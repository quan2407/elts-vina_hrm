import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { usePermissions } from "../contexts/PermissionContext";
import { ChevronDown, ChevronUp } from "lucide-react";
import {
  systemMenus,
  hrMenus,
  pmcMenus,
  productionManagerMenus,
  canteenMenus,
  lineLeaderMenus,
  employeeMenus,
  hrManagerMenus,
} from "../config/sidebarMenus";
import employeeService from "../services/employeeService";
import "../styles/Sidebar.css";

function Sidebar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [openMenu, setOpenMenu] = useState("");
  const { hasPermission, loading } = usePermissions();

  const token = localStorage.getItem("accessToken");
  const [delayPassed, setDelayPassed] = useState(false);
  const [employeeName, setEmployeeName] = useState("Loading...");

  useEffect(() => {
    const timeout = setTimeout(() => setDelayPassed(true), 300);
    return () => clearTimeout(timeout);
  }, []);

  useEffect(() => {
    const fetchEmployeeName = async () => {
      try {
        const res = await employeeService.getCurrentEmployeeName();
        setEmployeeName(res.data);
      } catch (err) {
        console.error("Không lấy được tên nhân viên", err);
        setEmployeeName("Unknown");
      }
    };
    if (token) fetchEmployeeName();
  }, [token]);

  let roles = [];
  if (token) {
    try {
      const decoded = jwtDecode(token);
      roles = decoded.roles
        ? decoded.roles.map((r) => r.replace("ROLE_", "").toUpperCase())
        : [];
    } catch (err) {
      console.error("Invalid token", err);
    }
  }

  let menus;
  if (roles.includes("PMC")) menus = pmcMenus;
  else if (roles.includes("HR")) menus = hrMenus;
  else if (roles.includes("HR_MANAGER")) menus = hrManagerMenus;
  else if (roles.includes("LINE_LEADER")) menus = lineLeaderMenus;
  else if (roles.includes("PRODUCTION_MANAGER")) menus = productionManagerMenus;
  else if (roles.includes("CANTEEN")) menus = canteenMenus;
  else if (roles.includes("EMPLOYEE")) menus = employeeMenus;
  else menus = systemMenus;

  const closeOffcanvasIfOpen = () => {
    const el = document.getElementById("sidebarOffcanvas");
    if (!el) return;
    if (typeof window !== "undefined" && window.bootstrap) {
      const instance = window.bootstrap.Offcanvas.getInstance(el) || window.bootstrap.Offcanvas.getOrCreateInstance(el);
      instance.hide();
    }
  };

  const handleMenuClick = (item) => {
    if (item.children) {
      setOpenMenu(openMenu === item.text ? "" : item.text);
    } else {
      navigate(item.path);
      closeOffcanvasIfOpen();
    }
  };

  const renderMenuList = (isOffcanvas = false) => (
    <div className="features-section">
      {!delayPassed || loading ? (
        <div className="loading-message">Đang tải menu...</div>
      ) : (
        menus.map((item, index) => {
          const allowed = !item.apiPath || hasPermission(item.apiPath, item.method || "GET");
          if (!allowed) return null;

          const isActive =
            location.pathname === item.path ||
            (item.children && item.children.some((c) => location.pathname === c.path));
          const isSubOpen = openMenu === item.text;

          return (
            <div key={index}>
              <div
                className={`nav-item ${isActive ? "active" : ""}`}
                onClick={() => handleMenuClick(item)}
                role="button"
                {...(isOffcanvas ? { "data-bs-dismiss": "offcanvas" } : {})}
              >
                <div className="nav-icon">{item.icon(isActive)}</div>
                <div className="nav-text" style={{ color: isActive ? "#4f46e5" : "white" }}>
                  {item.text}
                </div>
                {item.children && (
                  <div className="nav-icon">
                    {isSubOpen ? <ChevronUp size={16} stroke="white" /> : <ChevronDown size={16} stroke="white" />}
                  </div>
                )}
                {item.badge && (
                  <div className="notification-badge">
                    <div className="notification-count">{item.badge}</div>
                  </div>
                )}
              </div>

              {item.children && isSubOpen &&
                item.children
                  .filter((child) => !child.apiPath || hasPermission(child.apiPath, child.method || "GET"))
                  .map((child, cIdx) => (
                    <div
                      key={cIdx}
                      className={`nav-item ${location.pathname === child.path ? "active" : ""}`}
                      onClick={() => {
                        navigate(child.path);
                        closeOffcanvasIfOpen();
                      }}
                      role="button"
                      {...(isOffcanvas ? { "data-bs-dismiss": "offcanvas" } : {})}
                    >
                      <div className="nav-text">{child.text}</div>
                    </div>
                  ))}
            </div>
          );
        })
      )}
    </div>
  );

  return (
    <>
      {/* Desktop fixed sidebar */}
      <aside className="sidebar d-none d-md-flex">
        <div className="sidebar-inner">
          <div className="sidebar-top">
            <div className="user-profile">
              <img
                className="profile-image"
                src="https://cdn.builder.io/api/v1/image/assets/TEMP/8020efea94b33e133d3f1c7ace70ab7dcdce7dee?placeholderIfAbsent=true"
                alt="Profile"
              />
              <div className="user-name">{employeeName}</div>
              <div className="user-role">{roles.join(", ") || "UNKNOWN"}</div>
            </div>
            <div className="section-title">Menu</div>
          </div>
          <div className="menu-scroll">{renderMenuList(false)}</div>
        </div>
      </aside>

      {/* Mobile offcanvas sidebar */}
      <div
        className="offcanvas offcanvas-start d-md-none"
        tabIndex="-1"
        id="sidebarOffcanvas"
        aria-labelledby="sidebarOffcanvasLabel"
      >
        <div className="offcanvas-header">
          <h5 className="offcanvas-title" id="sidebarOffcanvasLabel">Menu</h5>
          <button type="button" className="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div className="offcanvas-body p-0">
          <div className="offcanvas-inner">
            <div className="sidebar-top">
              <div className="user-profile">
                <img
                  className="profile-image"
                  src="https://cdn.builder.io/api/v1/image/assets/TEMP/8020efea94b33e133d3f1c7ace70ab7dcdce7dee?placeholderIfAbsent=true"
                  alt="Profile"
                />
                <div className="user-name">{employeeName}</div>
                <div className="user-role">{roles.join(", ") || "UNKNOWN"}</div>
              </div>
              <div className="section-title">Menu</div>
            </div>
            <div className="menu-scroll">{renderMenuList(true)}</div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Sidebar;