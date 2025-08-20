import React, { useState, useEffect, useMemo } from "react";
import accountService from "../services/accountService";
import "../styles/AccountTable.css";

function AccountTable() {
  const [accounts, setAccounts] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const response = await accountService.getAllAccounts(page, size);
      const data = response.data;

      const formattedData = (data.content || []).map((acc) => ({
        id: acc.accountId,
        username: acc.username,
        email: acc.email,
        isActive: acc.isActive,
        lastLoginAt: acc.lastLoginAt,
        roles: [acc.role], // ví dụ: "ROLE_ADMIN"

        // Các trường từ Employee (không lấy employeeId theo yêu cầu)
        employeeCode: acc.employeeCode,
        employeeName: acc.employeeName,
        positionName: acc.positionName,
        departmentName: acc.departmentName,
        lineName: acc.lineName,
      }));

      setAccounts(formattedData);
      setTotalPages(data.totalPages ?? 0);
    } catch (err) {
      console.error("Error fetching accounts:", err);
      alert("Failed to load accounts.");
    } finally {
      setLoading(false);
    }
  };

  const isAdminRole = (roles) =>
    Array.isArray(roles) &&
    roles.some(
      (r) => (typeof r === "string" ? r : r?.roleName) === "ROLE_ADMIN"
    );

  const pageNumbers = useMemo(() => {
    const range = [];
    const maxPagesToShow = 5;
    if (totalPages <= 0) return range;

    let start = Math.max(0, page - Math.floor(maxPagesToShow / 2));
    let end = Math.min(totalPages, start + maxPagesToShow);
    if (end - start < maxPagesToShow) start = Math.max(0, end - maxPagesToShow);
    for (let i = start; i < end; i++) range.push(i);
    return range;
  }, [page, totalPages]);

  useEffect(() => {
    fetchAccounts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  const handleToggleStatus = async (id) => {
    try {
      await accountService.toggleAccountStatus(id);
      fetchAccounts();
    } catch (err) {
      console.error("Error toggling account status:", err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        "Failed to update account status.";
      alert(msg);
    }
  };

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

      {loading && <div className="account-table-loading">Đang tải...</div>}

      {!loading &&
        accounts.map((acc) => {
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
                    onClick={() => handleToggleStatus(acc.id)}
                    disabled={admin || acc.isActive}
                    title={admin ? "ADMIN không thể thay đổi trạng thái" : ""}
                  >
                    Kích hoạt
                  </button>
                  <button
                    className="account-table-action-btn account-table-deactive-btn"
                    onClick={() => handleToggleStatus(acc.id)}
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
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page === 0}
        >
          ‹
        </button>

        {pageNumbers.map((p) => (
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
          onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
          disabled={page === totalPages - 1 || totalPages === 0}
        >
          ›
        </button>
        <button
          className="account-pagination-btn"
          onClick={() => setPage(totalPages - 1)}
          disabled={page === totalPages - 1 || totalPages === 0}
        >
          »
        </button>
      </div>
    </div>
  );
}

export default AccountTable;
