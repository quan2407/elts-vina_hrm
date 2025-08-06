import React, { createContext, useContext, useEffect, useState } from "react";
import permissionService from "../services/permissionService";

const PermissionContext = createContext();

export const PermissionProvider = ({ children }) => {
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchPermissions = async () => {
    try {
      const res = await permissionService.getMyPermissions();
      setPermissions(res.data || []);
    } catch (err) {
      console.error("Lỗi khi lấy permissions:", err);
      setPermissions([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPermissions();
  }, []);

  const hasPermission = (apiPath, method) => {
    return permissions.some(
      (perm) =>
        perm.apiPath === apiPath &&
        perm.method.toUpperCase() === method.toUpperCase()
    );
  };

  // ✅ expose thêm hàm refresh để gọi lại bên ngoài
  return (
    <PermissionContext.Provider
      value={{
        permissions,
        hasPermission,
        loading,
        refreshPermissions: fetchPermissions,
      }}
    >
      {children}
    </PermissionContext.Provider>
  );
};

export const usePermissions = () => useContext(PermissionContext);
