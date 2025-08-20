import React from "react";
import "../styles/AccountTable.css";

function AccountTable({ accounts, loading, onToggleStatus }) {
  const isAdminRole = (roles) =>
    Array.isArray(roles) &&
    roles.some(
      (r) => (typeof r === "string" ? r : r?.roleName) === "ROLE_ADMIN"
    );

  if (loading) return <div className="account-table-loading">Đang tải...</div>;

  return (
    <div className="account-table-container">
      <div className="account-table-header">
        <div className="account-header-cell username-column">Tên đăng nhập</div>
        <div className="account-header-cell code-column">Mã NV</div>
        <div className="account-header-cell name-column">Tên NV</div>
        <div className="account-header-cell position-column">Chức vụ</div>
        <div className="account-header-cell dept-column">Phòng ban</div>
        <div className="account-header-cell line-column">Chuyền</div>
        <div className="account-header-cell email-column">Email</div>
        <div className="account-header-cell role-column">Vai trò</div>
        <div className="account-header-cell active-column">Kích hoạt</div>
        <div className="account-header-cell login-column">
          Đăng nhập lần cuối
        </div>
        <div className="account-header-cell actions-column">Hành động</div>
      </div>

      {accounts.map((acc) => {
        const admin = isAdminRole(acc.roles);
        const roleText = acc.roles
          .map((r) =>
            String(typeof r === "string" ? r : r?.roleName || "")
              .replace("ROLE_", "")
              .replace(/_/g, " ")
          )
          .join(", ");

        return (
          <div
            key={acc.id}
            className={`account-table-row ${admin ? "row-admin" : ""}`}
          >
            <div className="account-table-cell username-column">
              {acc.username}
            </div>
            <div className="account-table-cell code-column">
              {acc.employeeCode || "-"}
            </div>
            <div className="account-table-cell name-column">
              {acc.employeeName || "-"}
            </div>
            <div className="account-table-cell position-column">
              {acc.positionName || "-"}
            </div>
            <div className="account-table-cell dept-column">
              {acc.departmentName || "-"}
            </div>
            <div className="account-table-cell line-column">
              {acc.lineName || "-"}
            </div>
            <div className="account-table-cell email-column">{acc.email}</div>
            <div className="account-table-cell role-column">
              {roleText || "-"}
            </div>
            <div className="account-table-cell active-column">
              {acc.isActive ? "Yes" : "No"}
            </div>
            <div className="account-table-cell login-column">
              {acc.lastLoginAt || "-"}
            </div>
            <div className="account-table-cell actions-column">
              <div className="account-action-buttons-wrapper">
                <button
                  className="account-table-action-btn account-table-active-btn"
                  onClick={() => onToggleStatus(acc.id)}
                  disabled={admin || acc.isActive}
                  title={admin ? "ADMIN không thể thay đổi trạng thái" : ""}
                >
                  Kích hoạt
                </button>
                <button
                  className="account-table-action-btn account-table-deactive-btn"
                  onClick={() => onToggleStatus(acc.id)}
                  disabled={admin || !acc.isActive}
                  title={admin ? "ADMIN không thể thay đổi trạng thái" : ""}
                >
                  Vô hiệu hóa
                </button>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default AccountTable;
