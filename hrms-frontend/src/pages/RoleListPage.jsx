import React, { useEffect, useState } from "react";
import roleService from "../services/roleService";
import permissionService from "../services/permissionService";
import MainLayout from "../components/MainLayout";
import PermissionModal from "../components/PermissionModal";
import "../styles/RoleListPage.css";
import { getRoleDisplayName } from "../utils/roleUtils";

function RoleListPage() {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedPermissions, setSelectedPermissions] = useState(null);
  const [groupedPermissions, setGroupedPermissions] = useState({});
  const [showModal, setShowModal] = useState(false);

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
      const [resGrouped, resSelected] = await Promise.all([
        permissionService.getGroupedPermissions(),
        roleService.getPermissionsByRole(roleId),
      ]);
      setGroupedPermissions(resGrouped.data);
      setSelectedPermissions({
        roleId,
        list: resSelected.data,
        roleName: getRoleDisplayName(
          roles.find((r) => r.id === roleId)?.name || "Không rõ"
        ),
      });

      setShowModal(true);
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

        {showModal && selectedPermissions && (
          <PermissionModal
            groupedPermissions={groupedPermissions}
            selectedPermissions={selectedPermissions.list}
            roleId={selectedPermissions.roleId}
            roleName={selectedPermissions.roleName}
            onClose={() => setShowModal(false)}
          />
        )}
      </div>
    </MainLayout>
  );
}

export default RoleListPage;
