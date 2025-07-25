import React from "react";
import WorkScheduleProductionView from "../pages/WorkScheduleProductionView";
import { Navigate } from "react-router-dom";

function DynamicWorkScheduleWrapper() {
  const roles = JSON.parse(localStorage.getItem("role") || "[]");

  if (roles.includes("ROLE_HR")) {
    return <WorkScheduleProductionView canApprove={false} />;
  }

  if (roles.includes("ROLE_PRODUCTION_MANAGER")) {
    return <WorkScheduleProductionView canApprove={true} />;
  }

  return (
    <Navigate
      to="/unauthorized"
      replace
    />
  );
}

export default DynamicWorkScheduleWrapper;
