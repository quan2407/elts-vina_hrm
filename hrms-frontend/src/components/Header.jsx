import React, { useEffect, useMemo, useRef, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { Bell, ChevronDown, User, Key, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "../styles/Header.css";
import { getNotifications, markNotificationAsRead } from "../services/notificationService";
import notificationLinks from "../constants/notificationLinks.jsx";

function Header() {
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [notificationFilter, setNotificationFilter] = useState("all");
  const [showAllNotifications, setShowAllNotifications] = useState(false);
  const [username, setUsername] = useState("Unknown");

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) return;
    try {
      const decoded = jwtDecode(token);
      setUsername(decoded?.sub || "Unknown");
    } catch (err) {
      console.error("Invalid token", err);
    }
  }, []);

  const filteredNotifications = useMemo(
    () => notifications.filter((n) => (notificationFilter === "all" ? true : !n.isRead)),
    [notifications, notificationFilter]
  );

  const visibleNotifications = useMemo(
    () => (showAllNotifications ? filteredNotifications : filteredNotifications.slice(0, 5)),
    [filteredNotifications, showAllNotifications]
  );

  const handleSignOut = () => {
    localStorage.removeItem("accessToken");
    window.location.href = "/";
  };

  const handleGoToProfile = () => {
    navigate("/profile");
    setIsProfileOpen(false);
  };

  const handleChangePassword = () => {
    navigate("/change-password");
    setIsProfileOpen(false);
  };

  const fetchNotifications = async () => {
    try {
      const response = await getNotifications();
      const withLinks = (response || []).map((n) => {
        const t = n.type?.code || n.type?.name || n.type || n.notificationType;
        return { ...n, link: notificationLinks[t] || n.link || null };
      });
      setNotifications(withLinks);
    } catch (error) {
      console.error("Error fetching notifications:", error);
    }
  };

  const handleNotificationClick = async (id, link) => {
    try {
      await markNotificationAsRead(id);
      setNotifications((prev) => prev.map((noti) => (noti.id === id ? { ...noti, isRead: true } : noti)));
      if (link) navigate(link);
    } catch (error) {
      console.error("Error marking notification as read:", error);
    }
  };

  function formatRelativeTime(dateString) {
    const date = new Date(dateString);
    const diff = Math.floor((new Date() - date) / 1000);
    if (diff < 60) return `${diff} giây trước`;
    if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`;
    if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`;
    return `${Math.floor(diff / 86400)} ngày trước`;
  }

  useEffect(() => {
    fetchNotifications();
  }, []);

  const profileRef = useRef(null);
  const notificationAreaRef = useRef(null);

  useEffect(() => {
    const onClickOutside = (e) => {
      if (profileRef.current && !profileRef.current.contains(e.target)) setIsProfileOpen(false);
      if (notificationAreaRef.current && !notificationAreaRef.current.contains(e.target)) setIsNotificationOpen(false);
    };
    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, []);

  return (
    <header className="header">
      {/* Toggle for mobile: opens offcanvas sidebar */}
      <button
        className="btn btn-outline-secondary d-md-none"
        type="button"
        data-bs-toggle="offcanvas"
        data-bs-target="#sidebarOffcanvas"
        aria-controls="sidebarOffcanvas"
      >
        ☰
      </button>

      <div className="header-actions ms-auto">
        <div className="notification-area" ref={notificationAreaRef}>
          <div
            className="action-button position-relative"
            onClick={() => setIsNotificationOpen((s) => !s)}
            aria-label="Thông báo"
          >
            <Bell size={20} stroke="#000" />
            {notifications.some((n) => !n.isRead) && (
              <div className="notification-badge">
                {notifications.filter((n) => !n.isRead).length}
              </div>
            )}
          </div>

          {isNotificationOpen && (
            <div className="notification-dropdown" onMouseDown={(e) => e.stopPropagation()}>
              <div className="notification-header">
                <span>Thông báo</span>
              </div>

              <div className="notification-tabs">
                <button className={notificationFilter === "all" ? "active" : ""} onClick={() => setNotificationFilter("all")}>
                  Tất cả
                </button>
                <button className={notificationFilter === "unread" ? "active" : ""} onClick={() => setNotificationFilter("unread")}>
                  Chưa đọc
                </button>
              </div>

              <div className="notification-list">
                {filteredNotifications.length === 0 ? (
                  <div className="notification-empty">Không có thông báo nào</div>
                ) : (
                  visibleNotifications.map((noti) => (
                    <div
                      key={noti.id}
                      className={`notification-item ${!noti.isRead ? "unread" : ""}`}
                      onClick={() => handleNotificationClick(noti.id, noti.link)}
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
        </div>

        {/* Profile */}
        <div className="header-profile" ref={profileRef} onClick={() => setIsProfileOpen((s) => !s)}>
          <img className="header-avatar" src="https://i.pravatar.cc/40" alt="User" />
          <span className="header-username">{username}</span>
          <ChevronDown size={16} stroke="#000" />
        </div>

        {isProfileOpen && (
          <div className="profile-dropdown" onMouseDown={(e) => e.stopPropagation()}>
            <div className="profile-info">
              <div className="profile-name">{username}</div>
            </div>
            <div className="profile-item" onClick={handleGoToProfile}>
              <User size={16} /> <span>Hồ sơ cá nhân</span>
            </div>
            <div className="profile-item" onClick={handleChangePassword}>
              <Key size={16} /> <span>Đổi mật khẩu</span>
            </div>
            <div className="profile-divider"></div>
            <div className="profile-item profile-logout" onClick={handleSignOut}>
              <LogOut size={16} /> <span>Đăng xuất</span>
            </div>
          </div>
        )}
      </div>
    </header>
  );
}

export default Header;