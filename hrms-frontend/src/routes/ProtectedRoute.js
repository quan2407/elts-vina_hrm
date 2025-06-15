import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const ProtectedRoute = ({ children, allowedRoles }) => {
  const token = localStorage.getItem("token");
  if (!token) {
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

    const hasAccess = roles.some((role) => allowedRoles.includes(role));
    if (!hasAccess) {
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
