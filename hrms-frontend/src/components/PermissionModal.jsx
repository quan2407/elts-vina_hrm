import React, { useState } from "react";
import "../styles/PermissionModal.css";
import roleService from "../services/roleService";

function PermissionModal({
  groupedPermissions,
  selectedPermissions,
  onClose,
  roleName,
  roleId, // ✅ sử dụng prop truyền vào
}) {
  const [expandedModules, setExpandedModules] = useState({});
  const [permissionList, setPermissionList] = useState(selectedPermissions); // local copy

  const toggleModule = (module) => {
    setExpandedModules((prev) => ({
      ...prev,
      [module]: !prev[module],
    }));
  };

  const handleTogglePermission = async (permId) => {
    const isSelected = permissionList.some((p) => p.id === permId);
    let newList;

    if (isSelected) {
      newList = permissionList.filter((p) => p.id !== permId);
    } else {
      const allPerms = Object.values(groupedPermissions).flat();
      const permObj = allPerms.find((p) => p.id === permId);
      if (!permObj) return;
      newList = [...permissionList, permObj];
    }

    setPermissionList(newList);

    try {
      await roleService.updatePermissions(
        roleId,
        newList.map((p) => p.id)
      );
    } catch (err) {
      alert("Lỗi khi cập nhật quyền.");
      console.error("Update permission error:", err);
    }
  };

  return (
    <div className="permission-modal-backdrop">
      <div className="permission-modal">
        <div className="permission-header">
          <h2 className="permission-title">Quyền của vai trò: {roleName}</h2>
          <button
            className="permission-close-x"
            onClick={onClose}
          >
            ×
          </button>
        </div>

        <div className="permission-modal-content">
          {Object.entries(groupedPermissions).map(([module, perms]) => {
            const isExpanded = expandedModules[module] ?? true;
            return (
              <div
                className="permission-section"
                key={module}
              >
                <div
                  className="permission-section-header"
                  onClick={() => toggleModule(module)}
                >
                  <span className="arrow">{isExpanded ? "▾" : "▸"}</span>
                  <span className="module-name">{module}</span>
                </div>
                {isExpanded && (
                  <div className="permission-grid">
                    {perms.map((perm) => (
                      <div
                        className="permission-item"
                        key={perm.id}
                      >
                        <input
                          type="checkbox"
                          checked={permissionList.some((p) => p.id === perm.id)}
                          onChange={() => handleTogglePermission(perm.id)}
                        />
                        <div className="permission-info">
                          <span
                            className={`method method-${perm.method.toLowerCase()}`}
                          >
                            {perm.method}
                          </span>
                          <code className="path">{perm.apiPath}</code>
                          <span className="description">{perm.name}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>

        <div className="permission-footer">
          <button
            className="permission-close-button"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

export default PermissionModal;
