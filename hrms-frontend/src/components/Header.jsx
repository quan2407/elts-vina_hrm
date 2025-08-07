import React, { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { Bell, ChevronDown, User, Key, HelpCircle, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "../styles/Header.css";
import { getNotifications, markNotificationAsRead } from "../services/notificationService";


function Header() {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isNotificationDropdownOpen, setIsNotificationDropdownOpen] =
    useState(false);
  const [notifications, setNotifications] = useState([]);
  const [notificationFilter, setNotificationFilter] = useState("all");
  const [showAllNotifications, setShowAllNotifications] = useState(false);

  const navigate = useNavigate();

  // Decode token to get username
  const token = localStorage.getItem("accessToken");
  let username = "Unknown";

  const filteredNotifications = notifications.filter((n) =>
    notificationFilter === "all" ? true : !n.isRead
  );

  const visibleNotifications = showAllNotifications
    ? filteredNotifications
    : filteredNotifications.slice(0, 5);


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

  const fetchNotifications = async () => {
    try {
      const response = await getNotifications();
      setNotifications(response);
    } catch (error) {
      console.error("Error fetching notifications:", error);
    }
  };

  const handleNotificationClick = async (id) => {
    try {
      await markNotificationAsRead(id);
      setNotifications((prev) =>
        prev.map((noti) =>
          noti.id === id ? { ...noti, isRead: true } : noti
        )
      );
    } catch (error) {
      console.error("Error marking notification as read:", error);
    }
  };

  function formatRelativeTime(dateString) {
    const date = new Date(dateString);
    const diff = Math.floor((new Date() - date) / 1000); // seconds

    if (diff < 60) return `${diff} giây trước`;
    if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`;
    if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`;

    return `${Math.floor(diff / 86400)} ngày trước`;
  }


  useEffect(() => {
    fetchNotifications();
  }, []);

  return (
    <div className="header">
      <div></div>
      <div className="header-actions">
        <div
          className="action-button"
          onClick={() => setIsNotificationDropdownOpen(!isNotificationDropdownOpen)}
        >
          <Bell
            size={20}
            stroke="#000"
          />
          {notifications.some((n) => !n.isRead) && (
            <div className="notification-badge">
              {notifications.filter((n) => !n.isRead).length}
            </div>
          )}

        </div>
        {isNotificationDropdownOpen && (
          <div className="notification-dropdown">
            <div className="notification-header">
              <span>Thông báo</span>
            </div>
            <div className="notification-tabs">
              <button
                className={notificationFilter === "all" ? "active" : ""}
                onClick={() => setNotificationFilter("all")}
              >
                Tất cả
              </button>
              <button
                className={notificationFilter === "unread" ? "active" : ""}
                onClick={() => setNotificationFilter("unread")}
              >
                Chưa đọc
              </button>
            </div>
            <div className="notification-list">
              {filteredNotifications.length === 0 ? (
                <div className="notification-empty">Không có thông báo nào</div>
              ) : (
                visibleNotifications.map((noti, index) => (
                  <div
                    className={`notification-item ${!noti.isRead ? 'unread' : ''}`}
                    key={index}

                    onClick={() => handleNotificationClick(noti.id)}
                    style={{ cursor: "pointer" }}
                  >
                    <div className="notification-icon">
                      <Bell size={18} />
                    </div>
                    <div className="notification-content">
                      <div className="notification-text">{noti.content}</div>
                      <div className="notification-time">{formatRelativeTime(noti.createdAt)}</div>
                    </div>
                  </div>
                ))
              )}
            </div>
            <div className="notification-footer">
              {!showAllNotifications && filteredNotifications.length > 5 && (
                <button onClick={() => setShowAllNotifications(true)}>Xem thông báo trước đó</button>
              )}
            </div>

          </div>
        )}


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
