import React, { useState, useEffect } from "react";
import accountService from "../services/accountService";
import "../styles/AccountTable.css";

function AccountTable() {
  const [accounts, setAccounts] = useState([]);

  const fetchAccounts = async () => {
    try {
      const response = await accountService.getAllAccounts();
      const data = response.data;

      const formattedData = data.map((acc) => ({
        id: acc.accountId,
        username: acc.username,
        email: acc.email,
        isActive: acc.isActive,
        lastLoginAt: acc.lastLoginAt,
        roles: [acc.role],
      }));

      setAccounts(formattedData);
    } catch (err) {
      console.error("Error fetching accounts:", err);
      if (err.response?.status === 403) {
        alert("You are not authorized to view this page.");
      } else {
        alert("Failed to load accounts.");
      }
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  const handleToggleStatus = async (id) => {
    try {
      await accountService.toggleAccountStatus(id);
      fetchAccounts(); // Refresh list
    } catch (err) {
      console.error("Error toggling account status:", err);
      alert("Failed to update account status.");
    }
  };

  return (
    <div className="account-table-container">
      <div className="account-table-header">
        <div className="account-header-cell username-column">Tên đăng nhập</div>
        <div className="account-header-cell email-column">Email</div>
        <div className="account-header-cell role-column">Vai trò</div>
        <div className="account-header-cell active-column">Kích hoạt</div>
        <div className="account-header-cell login-column">
          Đăng nhập lần cuối
        </div>
        <div className="account-header-cell actions-column">Hành động</div>
      </div>

      {accounts.map((acc) => (
        <div
          key={acc.id}
          className="account-table-row"
        >
          <div className="account-table-cell username-column">
            {acc.username}
          </div>
          <div className="account-table-cell email-column">{acc.email}</div>
          <div className="account-table-cell role-column">
            {acc.roles
              .map((r) => r.replace("ROLE_", "").replace(/_/g, " "))
              .join(", ")}
          </div>
          <div className="account-table-cell active-column">
            {acc.isActive ? "Yes" : "No"}
          </div>
          <div className="account-table-cell login-column">
            {acc.lastLoginAt || "-"}
          </div>
          <div className="account-table-cell actions-column">
            <button
              className="account-table-action-btn account-table-active-btn"
              onClick={() => handleToggleStatus(acc.id)}
              disabled={acc.isActive}
            >
              Kích hoạt
            </button>
            <button
              className="account-table-action-btn account-table-deactive-btn"
              onClick={() => handleToggleStatus(acc.id)}
              disabled={!acc.isActive}
            >
              Vô hiệu hóa
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}

export default AccountTable;
