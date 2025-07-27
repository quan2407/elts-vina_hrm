import React, { useState } from "react";
import "../styles/PermissionModal.css";

function PermissionModal({
  groupedPermissions,
  selectedPermissions,
  onClose,
  roleName,
}) {
  const [expandedModules, setExpandedModules] = useState({});

  const toggleModule = (module) => {
    setExpandedModules((prev) => ({
      ...prev,
      [module]: !prev[module],
    }));
  };

  return (
    <div className="permission-modal-backdrop">
      <div className="permission-modal">
        <h2 className="permission-title">Quyền của vai trò: {roleName}</h2>
        <button
          className="permission-close-x"
          onClick={onClose}
        >
          ×
        </button>

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
                          checked={selectedPermissions.some(
                            (p) => p.id === perm.id
                          )}
                          readOnly
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
