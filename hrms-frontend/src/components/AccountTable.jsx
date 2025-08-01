import React, { useState, useEffect } from "react";
import accountService from "../services/accountService";
import "../styles/AccountTable.css";

function AccountTable() {
  const [accounts, setAccounts] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const fetchAccounts = async () => {
    try {
      const response = await accountService.getAllAccounts(page, size);
      const data = response.data;

      const formattedData = data.content.map((acc) => ({
        id: acc.accountId,
        username: acc.username,
        email: acc.email,
        isActive: acc.isActive,
        lastLoginAt: acc.lastLoginAt,
        roles: [acc.role],
      }));

      setAccounts(formattedData);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error("Error fetching accounts:", err);
      alert("Failed to load accounts.");
    }
  };
  const getPageNumbers = () => {
    const range = [];
    const maxPagesToShow = 5;
    let start = Math.max(0, page - Math.floor(maxPagesToShow / 2));
    let end = Math.min(totalPages, start + maxPagesToShow);
    if (end - start < maxPagesToShow) {
      start = Math.max(0, end - maxPagesToShow);
    }
    for (let i = start; i < end; i++) {
      range.push(i);
    }
    return range;
  };

  useEffect(() => {
    fetchAccounts();
  }, [page]);

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
            <div className="account-action-buttons-wrapper">
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
        </div>
      ))}
      <div className="account-pagination-container">
        <button
          className="account-pagination-btn"
          onClick={() => setPage(0)}
          disabled={page === 0}
        >
          «
        </button>
        <button
          className="account-pagination-btn"
          onClick={() => setPage(page - 1)}
          disabled={page === 0}
        >
          ‹
        </button>

        {getPageNumbers().map((p) => (
          <button
            key={p}
            onClick={() => setPage(p)}
            className={`account-pagination-btn ${
              p === page ? "account-pagination-active" : ""
            }`}
          >
            {p + 1}
          </button>
        ))}

        <button
          className="account-pagination-btn"
          onClick={() => setPage(page + 1)}
          disabled={page === totalPages - 1}
        >
          ›
        </button>
        <button
          className="account-pagination-btn"
          onClick={() => setPage(totalPages - 1)}
          disabled={page === totalPages - 1}
        >
          »
        </button>
      </div>
    </div>
  );
}

export default AccountTable;
