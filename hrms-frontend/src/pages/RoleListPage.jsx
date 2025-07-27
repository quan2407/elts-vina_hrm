import React, { useEffect, useState } from "react";
import roleService from "../services/roleService";
import MainLayout from "../components/MainLayout";
import "../styles/RoleListPage.css";
import { getRoleDisplayName } from "../utils/roleUtils";

function RoleListPage() {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedPermissions, setSelectedPermissions] = useState(null);

  const fetchRoles = async () => {
    setLoading(true);
    try {
      const res = await roleService.getAllRoles();
      setRoles(res.data);
    } catch (err) {
      console.error("Lỗi khi tải danh sách vai trò:", err);
      alert("Không thể tải danh sách vai trò.");
    } finally {
      setLoading(false);
    }
  };

  const handleViewPermissions = async (roleId) => {
    try {
      const res = await roleService.getPermissionsByRole(roleId);
      setSelectedPermissions({
        roleId,
        list: res.data,
      });
    } catch (err) {
      console.error("Lỗi khi lấy quyền:", err);
      alert("Không thể tải quyền của vai trò.");
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách vai trò</h1>
        </div>

        <div className="role-table-container">
          <div className="role-table-header">
            <div className="role-header-cell">ID</div>
            <div className="role-header-cell">Tên vai trò</div>
            <div className="role-header-cell">Mô tả</div>
            <div className="role-header-cell">Hành động</div>
          </div>

          {loading ? (
            <div style={{ textAlign: "center", marginTop: "20px" }}>
              Đang tải...
            </div>
          ) : roles.length === 0 ? (
            <div style={{ textAlign: "center", marginTop: "20px" }}>
              Không có vai trò nào.
            </div>
          ) : (
            roles.map((role) => (
              <div
                key={role.id}
                className="role-table-row"
              >
                <div className="role-table-cell">{role.id}</div>
                <div className="role-table-cell">
                  {getRoleDisplayName(role.name)}
                </div>

                <div className="role-table-cell">{role.description || "—"}</div>
                <div className="role-table-cell">
                  <button
                    className="role-view-button"
                    onClick={() => handleViewPermissions(role.id)}
                  >
                    Xem quyền
                  </button>
                </div>
              </div>
            ))
          )}
        </div>

        {selectedPermissions && (
          <div style={{ marginTop: 30 }}>
            <h2 style={{ fontSize: "20px" }}>
              Danh sách quyền của vai trò:{" "}
              {getRoleDisplayName(
                roles.find((r) => r.id === selectedPermissions.roleId)?.name
              )}
            </h2>
            <ul>
              {selectedPermissions.list.map((perm) => (
                <li key={perm.id}>
                  <strong>{perm.method}</strong> – {perm.endpoint}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </MainLayout>
  );
}

export default RoleListPage;
