import React, { useState } from "react";
import { jwtDecode } from "jwt-decode";
import { Bell, ChevronDown, User, Key, HelpCircle, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "../styles/Header.css";

function Header() {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const navigate = useNavigate();

  // Lấy username từ token
  const token = localStorage.getItem("accessToken");
  let username = "Unknown";

  if (token) {
    try {
      const decoded = jwtDecode(token);
      username = decoded.sub || "Unknown";
    } catch (err) {
      console.error("Invalid token", err);
    }
  }

  const handleSignOut = () => {
    localStorage.removeItem("accessToken");
    window.location.href = "/";
  };

  const handleGoToProfile = () => {
    navigate("/profile");
    setIsDropdownOpen(false);
  };

  const handleChangePassword = () => {
    navigate("/change-password");
    setIsDropdownOpen(false);
  };

  return (
    <div className="header">
      <div></div>
      <div className="header-actions">
        <div className="action-button">
          <Bell
            size={20}
            stroke="#000"
          />
          <div className="notification-badge">1</div>
        </div>

        <div
          className="header-profile"
          onClick={() => setIsDropdownOpen(!isDropdownOpen)}
        >
          <img
            className="header-avatar"
            src="https://i.pravatar.cc/40"
            alt="User"
          />
          <span className="header-username">{username}</span>
          <ChevronDown
            size={16}
            stroke="#000"
          />
        </div>

        {isDropdownOpen && (
          <div className="profile-dropdown">
            <div className="profile-info">
              <div className="profile-name">{username}</div>
            </div>

            <div
              className="profile-item"
              onClick={handleGoToProfile}
            >
              <User size={16} /> <span>Hồ sơ cá nhân</span>
            </div>

            <div
              className="profile-item"
              onClick={handleChangePassword}
            >
              <Key size={16} /> <span>Đổi mật khẩu</span>
            </div>

            <div className="profile-item">
              <HelpCircle size={16} /> <span>Hỗ trợ</span>
            </div>

            <div className="profile-divider"></div>

            <div
              className="profile-item profile-logout"
              onClick={handleSignOut}
            >
              <LogOut size={16} /> <span>Đăng xuất</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Header;
