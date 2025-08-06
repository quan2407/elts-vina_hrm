import React from "react";
import AttendanceMonthlyView from "../pages/AttendanceMonthlyView";

function DynamicAttendanceWrapper() {
  const roles = JSON.parse(localStorage.getItem("role") || "[]");

  if (roles.includes("ROLE_HR")) {
    return <AttendanceMonthlyView readOnly={false} />;
  }

  if (roles.includes("ROLE_PRODUCTION_MANAGER")) {
    return <AttendanceMonthlyView readOnly={true} />;
  }

  return <div>❌ Bạn không có quyền truy cập trang này.</div>;
}

export default DynamicAttendanceWrapper;
