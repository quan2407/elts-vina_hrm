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
    key: "system_accounts",
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
    key: "system_reset_password_requests",
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
    key: "system_account_requests",
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
    key: "system_roles",
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
    key: "hr_dashboard",
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
    key: "hr_human_report",
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
    key: "hr_employee_management",
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
    key: "hr_recruitment",
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
    key: "hr_line_management",
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
    key: "hr_benefits_management",
    text: "Quản lý phúc lợi",
    path: "/benefits-management",
    apiPath: "/api/hr/benefits",
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
    key: "hr_holiday_management",
    text: "Quản lý ngày nghỉ",
    path: "/holiday-management",
    pathsToMatch: ["/holiday-management"],
    apiPath: "/api/holidays",
    icon: (isActive) => (
      <CalendarCheck
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    key: "hr_interviews",
    text: "Danh sách phỏng vấn",
    path: "/interviews-management",
    pathsToMatch: ["/interviews-management", "/interviews-management/:id"],
    apiPath: "/api/interview",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    key: "hr_attendance_monthly",
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
    key: "hr_salary_monthly",
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
    key: "hr_work_schedule",
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
    key: "hr_applications_approval",
    text: "Duyệt đơn bên HR",
    path: "/applications/approvals/hr",
    pathsToMatch: [
      { path: "/applications/approvals/hr", end: true },
      { path: "/applications/:id", end: true },
      { path: "/create-application", end: true },
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
    key: "employee_my_schedule",
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
    key: "pmc_work_schedule",
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
    key: "pmc_line_management",
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
    key: "employee_my_schedule", // dùng chung key với employee & lineLeader
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
    key: "employee_my_schedule",
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
    key: "employee_my_attendance",
    text: "Bảng công tháng của tôi",
    path: "/my-attendance-monthly",
    pathsToMatch: ["/my-attendance-monthly", "/create-application"],
    apiPath: "/api/attendances/employee",
    icon: (isActive) => (
      <LayoutDashboard
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    key: "employee_my_salary",
    text: "Bảng lương tháng của tôi",
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
    key: "employee_my_applications",
    text: "Đơn từ của tôi",
    path: "/my-applications",
    pathsToMatch: [
      { path: "/my-applications", end: true },
      { path: "/applications/:id", end: true },
    ],
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
    key: "production_manager_work_schedule",
    text: "Duyệt Lịch sản xuất",
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
    key: "production_manager_attendance_monthly",
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
    key: "production_manager_applications_approval",
    text: "Duyệt đơn bên quản lý",
    path: "/applications/approvals/manager",
    pathsToMatch: [
      { path: "/applications/approvals/manager", end: true },
      { path: "/applications/:id", end: true },
      { path: "/create-application", end: true },
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
    key: "employee_my_schedule", // dùng chung key với employee & lineLeader
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
    key: "employee_my_schedule",
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
    key: "employee_my_attendance",
    text: "Bảng công tháng của tôi",
    path: "/my-attendance-monthly",
    apiPath: "/api/attendances/employee",
    pathsToMatch: ["/my-attendance-monthly", "/create-application"],
    icon: (isActive) => (
      <LayoutDashboard
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    key: "employee_my_salary",
    text: "Bảng lương tháng của tôi",
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
    key: "employee_my_applications",
    text: "Đơn từ của tôi",
    path: "/my-applications",
    pathsToMatch: [
      { path: "/my-applications", end: true },
      { path: "/applications/:id", end: true },
    ],
    apiPath: "/api/applications/me",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
  {
    key: "employee_interviews",
    text: "Danh sách phỏng vấn",
    path: "/interviews-management",
    apiPath: "/api/interview",
    icon: (isActive) => (
      <Briefcase
        size={20}
        stroke={isActive ? "#4f46e5" : "white"}
      />
    ),
  },
];

export const hrManagerMenus = [...hrMenus];
