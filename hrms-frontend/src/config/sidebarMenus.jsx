import React from "react";
import {
  LayoutDashboard,
  Users,
  Briefcase,
  UserPlus,
  MessageSquare,
  Target,
  Gift,
  CalendarCheck,
  Rows4,
  Wallet,
  ClipboardPlus,
} from "lucide-react";

export const systemMenus = [
  {
    text: "Danh sách tài khoản",
    path: "/",
    icon: (isActive) => (
      <Users
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Tin nhắn",
    path: "/messages",
    icon: (isActive) => (
      <MessageSquare
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
    badge: 13,
  },
  {
    text: "Duyệt reset mật khẩu",
    path: "/admin/reset-password-requests",
    icon: (isActive) => (
      <Target
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Yêu cầu tạo tài khoản",
    path: "/admin/account-requests",
    icon: (isActive) => (
      <UserPlus
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Danh sách vai trò",
    path: "/admin/roles",
    icon: (isActive) => (
      <Users
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];

export const hrMenus = [
  {
    text: "Tổng quan",
    path: "/dashboard",
    icon: (isActive) => (
      <LayoutDashboard
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Báo cáo nhân lực",
    path: "/human-report",
    icon: (isActive) => (
      <ClipboardPlus
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Quản lý nhân viên",
    path: "/employee-management",
    icon: (isActive) => (
      <Users
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Tuyển dụng",
    path: "/jobs-management",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Danh sách line",
    path: "/line-management",
    icon: (isActive) => (
      <Rows4
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Quản lý phúc lợi ",
    path: "/benefit",
    icon: (isActive) => (
      <Gift
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Quản lý ngày nghỉ",
    path: "/holiday-management",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },

  {
    text: "Danh sách phỏng vấn",
    path: "/interviews-management",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Bảng công tháng",
    path: "/attendance-monthly",
    icon: (isActive) => (
      <LayoutDashboard
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Bảng lương tháng",
    path: "/salary-monthly",
    icon: (isActive) => (
      <Wallet
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Lịch sản xuất",
    path: "/work-schedule-view-hr",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Duyệt đơn",
    path: "/applications/approvals/hr",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
export const pmcMenus = [
  {
    text: "Lịch sản xuất",
    path: "/work-schedule-management",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Danh sách line",
    path: "/line-management",
    icon: (isActive) => (
      <Rows4
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
export const lineLeaderMenus = [];
export const productionManagerMenus = [
  {
    text: "Lịch làm việc",
    path: "/work-schedule-production",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Bảng công tháng",
    path: "/attendance-monthly-view",
    icon: (isActive) => (
      <LayoutDashboard
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Duyệt đơn",
    path: "/applications/approvals/manager",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
export const canteenMenus = [];
export const employeeMenus = [
  {
    text: "Lịch làm việc của tôi",
    path: "/my-work-schedule",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Bảng công tháng",
    path: "/my-attendance-monthly",
    icon: (isActive) => (
      <LayoutDashboard
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Bảng lương tháng",
    path: "/my-salary-monthly",
    icon: (isActive) => (
      <Wallet
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Đơn từ của tôi",
    path: "/my-applications",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
export const hrManagerMenus = [
  ...hrMenus,
  {
    text: "Phê duyệt đặc biệt",
    path: "/special-approvals",
    icon: (isActive) => (
      <Target
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
