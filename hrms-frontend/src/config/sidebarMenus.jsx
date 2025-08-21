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
    path: "/accounts",
    icon: (isActive) => (
      <Users
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
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
    apiPath: "/api/human-report/full-emp",
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
    pathsToMatch: [
      "/employee-management",
      "/employee-create",
      "/employees/:id",
    ],
    apiPath: "/api/employees",
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
    pathsToMatch: [
      "/jobs-management",
      "/recruitment-create",
      "candidates-management/:jobId",
      "/jobsdetail-management/:jobId",
      "/add-interview/:id",
    ],
    apiPath: "/api/recruitment",
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
    pathsToMatch: ["/employee/line-hr/:id", "/line-management"],
    apiPath: "/api/lines",
    icon: (isActive) => (
      <Rows4
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Quản lý phúc lợi ",
    path: "/benefits-management",
    pathsToMatch: [
      "/benefits-management/benefit/:benefitId",
      "/benefits-management/benefit/:benefitId/position/:positionId",
      "/benefits-management",
    ],
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
    pathsToMatch: ["/holiday-management"],
    apiPath: "/api/employees",
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
    pathsToMatch: ["/interviews-management", "/interviews-management/:id"],
    apiPath: "/api/recruitment",
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
    pathsToMatch: ["/attendance-monthly"],
    apiPath: "/api/attendances/view-by-month",
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
    pathsToMatch: ["/salary-monthly"],
    apiPath: "/api/salaries",
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
    apiPath: "/api/work-schedule-details/view-by-month",
    pathsToMatch: ["/work-schedule-view-hr"],
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
    pathsToMatch: [
      "/applications/approvals/hr",
      "/create-application",
      "/applications/:id",
    ],
    apiPath: "/api/applications/step-2",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Màn hình nhân viên",
    path: "/my-work-schedule",
    viewAs: "employee",
    icon: (isActive) => (
      <ClipboardPlus
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
    apiPath: "/api/work-schedule-details/view-by-month",
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
    pathsToMatch: ["/line-management", "/employee/line/:id"],
    apiPath: "/api/lines",
    icon: (isActive) => (
      <Rows4
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Màn hình nhân viên",
    path: "/my-work-schedule",
    viewAs: "employee",
    icon: (isActive) => (
      <ClipboardPlus
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
export const lineLeaderMenus = [
  {
    text: "Lịch làm việc của tôi",
    path: "/my-work-schedule",
    pathsToMatch: ["/my-work-schedule", "/create-application"],
    apiPath: "/api/work-schedules/employee-view",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Bảng công tháng",
    pathsToMatch: ["/my-attendance-monthly", "/create-application"],
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
    apiPath: "/api/salaries/employee-months",
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
    pathsToMatch: ["/my-applications", "/applications/:id"],
    apiPath: "/api/applications/me",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];
export const productionManagerMenus = [
  {
    text: "Lịch làm việc",
    path: "/work-schedule-production",
    apiPath: "/api/work-schedule-details/view-by-month",
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
    apiPath: "/api/attendances/view-by-month",
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
    pathsToMatch: [
      "/applications/approvals/manager",
      "/create-application",
      "/applications/:id",
    ],
    apiPath: "/api/applications/step-1",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    text: "Màn hình nhân viên",
    path: "/my-work-schedule",
    viewAs: "employee",
    icon: (isActive) => (
      <ClipboardPlus
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
    pathsToMatch: ["/my-work-schedule", "/create-application"],
    apiPath: "/api/work-schedules/employee-view",
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
    pathsToMatch: ["/my-attendance-monthly", "/create-application"],
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
    apiPath: "/api/salaries/employee-months",
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
    pathsToMatch: ["/my-applications", "/applications/:id"],
    apiPath: "/api/applications/me",
    icon: (isActive) => (
      <Briefcase
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
];
export const hrManagerMenus = [...hrMenus];
