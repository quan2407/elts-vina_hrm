const notificationLinks = {
  LEAVE_REQUEST: "1", // nghỉ phép
  APPROVAL: "2", // phê duyệt đơn từ
  INTERVIEW_SCHEDULE: "/interviews-management", // lịch phỏng vấn
  SHIFT_CHANGED: "4", // lịch làm việc
  LINE_CHANGED: "/my-work-schedule", // thay đổi ca làm việc
  APPLICATION_SUBMITTED: "/applications/approvals/manager",
  APPLICATION_REJECTED: "/my-applications",
  APPLICATION_APPROVED: "/my-applications",
  APPLICATION_NEEDS_HR_APPROVAL: "/applications/approvals/hr",
  SCHEDULE_SUBMITTED: "/work-schedule-production",
  SCHEDULE_REJECTED: "/work-schedule-management",
  SCHEDULE_NEEDS_REVISION: "/work-schedule-management",
  SALARY_CREATED: "/my-salary-monthly",
  SALARY_UPDATED: "/my-salary-monthly",
  SALARY_LOCKED: "/my-salary-monthly",
  SALARY_UNLOCKED: "/my-salary-monthly",
  ATTENDANCE_UPDATED: "/my-attendance-monthly",
  LEAVE_CODE_UPDATED: "/my-attendance-monthly",
  ATTENDANCE_DAILY_UPDATED: "/my-attendance-monthly",
};

export default notificationLinks;
