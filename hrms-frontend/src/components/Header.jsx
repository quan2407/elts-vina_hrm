import React, { useEffect, useState, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import { Bell, ChevronDown, User, Key, HelpCircle, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "../styles/Header.css";
import {
  getNotifications,
  markNotificationAsRead,
} from "../services/notificationService";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

function Header() {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isNotificationDropdownOpen, setIsNotificationDropdownOpen] =
    useState(false);
  const [notifications, setNotifications] = useState([]);
  const [notificationFilter, setNotificationFilter] = useState("all");
  const stompClient = useRef(null);
  const navigate = useNavigate();

  const token = localStorage.getItem("accessToken");
  let username = "Unknown";
  let accountId = null;

  if (token) {
    try {
      const decoded = jwtDecode(token);
      username = decoded.sub || "Unknown";
      accountId = decoded.accountId;
    } catch (err) {
      console.error("Invalid token", err);
    }
  }

  const fetchNotifications = async () => {
    try {
      const data = await getNotifications();
      setNotifications(data);
    } catch (error) {
      console.error("Failed to fetch notifications:", error);
    }
  };

  const handleNotificationClick = async (id) => {
    try {
      await markNotificationAsRead(id);
      setNotifications((prev) =>
        prev.map((noti) => (noti.id === id ? { ...noti, isRead: true } : noti))
      );
    } catch (err) {
      console.error("Failed to mark notification as read:", err);
    }
  };

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

  const formatRelativeTime = (dateString) => {
    const date = new Date(dateString);
    const diff = Math.floor((new Date() - date) / 1000);

    if (diff < 60) return `${diff} giây trước`;
    if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`;
    if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`;

    return `${Math.floor(diff / 86400)} ngày trước`;
  };

  // WebSocket - Real-time Notification
  useEffect(() => {
    if (!accountId) return;

    const socket = new SockJS("http://localhost:8080/ws");

    stompClient.current = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("✅ Connected to WebSocket");
        stompClient.current.subscribe(
          "/user/queue/notifications",
          (message) => {
            console.log("📨 Received notification:", message.body);
            const newNoti = JSON.parse(message.body);
            setNotifications((prev) => [newNoti, ...prev]);
          }
        );
      },
      onStompError: (frame) => {
        console.error("❌ STOMP Error:", frame);
      },
      onWebSocketError: (error) => {
        console.error("❌ WebSocket Error:", error);
      },
      onWebSocketClose: (event) => {
        console.warn("🔌 WebSocket closed:", event);
      },
    });

    stompClient.current.activate();

    return () => {
      if (stompClient.current) stompClient.current.deactivate();
    };
  }, []);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const filteredNotifications = notifications.filter((n) =>
    notificationFilter === "all" ? true : !n.isRead
  );

  return (
    <div className="header">
      <div></div>
      <div className="header-actions">
        {/* Notification Bell */}
        <div
          className="action-button"
          onClick={() => setIsNotificationDropdownOpen((prev) => !prev)}
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
            <div className="notification-header">Thông báo</div>
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
                filteredNotifications.map((noti) => (
                  <div
                    className={`notification-item ${
                      !noti.isRead ? "unread" : ""
                    }`}
                    key={noti.id}
                    onClick={() => handleNotificationClick(noti.id)}
                    style={{ cursor: "pointer" }}
                  >
                    <div className="notification-icon">
                      <Bell size={18} />
                    </div>
                    <div className="notification-content">
                      <div className="notification-text">{noti.content}</div>
                      <div className="notification-time">
                        {formatRelativeTime(noti.createdAt)}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
            <div className="notification-footer">
              <button>Xem thông báo trước đó</button>
            </div>
          </div>
        )}

        {/* User Profile */}
        <div
          className="header-profile"
          onClick={() => setIsDropdownOpen((prev) => !prev)}
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
