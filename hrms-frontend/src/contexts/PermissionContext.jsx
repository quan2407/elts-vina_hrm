import React, { createContext, useContext, useEffect, useState } from "react";
import permissionService from "../services/permissionService";

const PermissionContext = createContext();

export const PermissionProvider = ({ children }) => {
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
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

    fetchPermissions();
  }, []);

  const hasPermission = (apiPath, method) => {
    return permissions.some(
      (perm) =>
        perm.apiPath === apiPath &&
        perm.method.toUpperCase() === method.toUpperCase()
    );
  };

  return (
    <PermissionContext.Provider value={{ permissions, hasPermission, loading }}>
      {children}
    </PermissionContext.Provider>
  );
};

export const usePermissions = () => useContext(PermissionContext);
