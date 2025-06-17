import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const ProtectedRoute = ({ children, allowedRoles }) => {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    console.log("Không tìm thấy token, redirect về login");
    return (
      <Navigate
        to="/"
        replace
      />
    );
  }

  try {
    const decoded = jwtDecode(token);
    const roles = decoded.roles || [];

    console.log("Decoded token:", decoded);
    console.log("Roles:", roles);

    const hasAccess = roles.some((role) => allowedRoles.includes(role));
    if (!hasAccess) {
      console.log("Không có quyền truy cập, redirect unauthorized");
      return (
        <Navigate
          to="/unauthorized"
          replace
        />
      );
    }

    return children;
  } catch (error) {
    console.error("Token decode failed", error);
    return (
      <Navigate
        to="/"
        replace
      />
    );
  }
};

export default ProtectedRoute;
