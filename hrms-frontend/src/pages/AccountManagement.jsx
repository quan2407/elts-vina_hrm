import React, { useEffect, useMemo, useState } from "react";
import MainLayout from "../components/MainLayout";
import AccountTable from "../components/AccountTable";
import accountService from "../services/accountService";
import departmentService from "../services/departmentService";
import positionService from "../services/positionService";
import { getAllLines } from "../services/linesService";
import "../styles/ManagementLayout.css";
import "../styles/AccountTable.css";
const ROLE_OPTIONS = [
  { value: "", label: "-- Chọn Vai Trò --" },
  { value: "ROLE_ADMIN", label: "ADMIN" },
  { value: "ROLE_HR", label: "HR" },
  { value: "ROLE_LINE_LEADER", label: "LINE LEADER" },
  { value: "ROLE_PRODUCTION_MANAGER", label: "PRODUCTION MANAGER" },
  { value: "ROLE_EMPLOYEE", label: "EMPLOYEE" },
  { value: "ROLE_CANTEEN", label: "CANTEEN" },
  { value: "ROLE_PMC", label: "PMC" },
];

function AccountManagement() {
  const [accounts, setAccounts] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  // filters
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentId, setDepartmentId] = useState(null);
  const [positionId, setPositionId] = useState(null);
  const [lineId, setLineId] = useState(null);
  const [role, setRole] = useState("");
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);

  useEffect(() => {
    (async () => {
      try {
        const depRes = await departmentService.getAllDepartments();
        setDepartments(depRes.data || []);

        const posRes = await positionService.getAll();
        setPositions(posRes.data || []);

        const lineRes = await getAllLines();
        setLines(lineRes || []);
      } catch (e) {
        console.error("Load danh mục lỗi:", e);
      }
    })();
  }, []);
  useEffect(() => {
    (async () => {
      try {
        if (departmentId) {
          const [posByDep, lineByDep] = await Promise.all([
            departmentService.getPositionsByDepartment(departmentId),
            departmentService.getLinesByDepartment(departmentId),
          ]);
          setPositions(posByDep.data || []);
          setLines(lineByDep.data || []);
        } else {
          const posRes = await positionService.getAll();
          setPositions(posRes.data || []);
          const lineRes = await getAllLines();
          setLines(lineRes || []);
        }
        setPositionId(null);
        setLineId(null);
      } catch (e) {
        console.error("Load positions/lines theo department lỗi:", e);
      }
    })();
  }, [departmentId]);

  const fetchAccounts = async (
    targetPage = page,
    targetSize = size,
    filtersOverride = null
  ) => {
    setLoading(true);
    try {
      const filters = filtersOverride ?? {
        search: searchTerm || undefined,
        departmentId: departmentId ?? undefined,
        positionId: positionId ?? undefined,
        lineId: lineId ?? undefined,
        role: role || undefined,
      };

      const res = await accountService.getAllAccounts(
        targetPage,
        targetSize,
        filters
      );
      const data = res.data;

      const formatted = (data.content || []).map((acc) => ({
        id: acc.accountId,
        username: acc.username,
        email: acc.email,
        isActive: acc.isActive,
        lastLoginAt: acc.lastLoginAt,
        roles: [acc.role],
        employeeCode: acc.employeeCode,
        employeeName: acc.employeeName,
        positionName: acc.positionName,
        departmentName: acc.departmentName,
        lineName: acc.lineName,
      }));

      setAccounts(formatted);
      setTotalPages(data.totalPages ?? 0);
    } catch (e) {
      console.error("Fetch accounts failed:", e);
      setAccounts([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };
  const handleClearFilters = () => {
    setSearchTerm("");
    setDepartmentId(null);
    setLineId(null);
    setPositionId(null);
    setRole("");
    setPage(0);
    const cleared = {
      search: undefined,
      departmentId: undefined,
      positionId: undefined,
      lineId: undefined,
      role: undefined,
    };
    fetchAccounts(0, size, cleared);
  };

  useEffect(() => {
    fetchAccounts(page, size);
  }, [page, size]);

  const handleToggleStatus = async (id) => {
    try {
      await accountService.toggleAccountStatus(id);
      fetchAccounts(page, size);
    } catch (err) {
      console.error("Error toggling account status:", err);
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        "Failed to update account status.";
      alert(msg);
    }
  };
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

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Quản lý tài khoản</h1>
          <div
            className="page-actions"
            style={{ display: "flex", gap: 8, flexWrap: "wrap" }}
          >
            <label htmlFor="accPageSize">Hiển thị</label>
            <select
              id="accPageSize"
              value={size}
              onChange={(e) => {
                const newSize = Number(e.target.value);
                setSize(newSize);
                setPage(0);
              }}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
            <span>/ trang</span>
          </div>
        </div>
        <div
          className="employee-search-wrapper"
          style={{
            display: "flex",
            gap: 8,
            flexWrap: "wrap",
          }}
        >
          <input
            type="text"
            placeholder="Tìm theo mã NV hoặc tên NV"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                setPage(0);
                fetchAccounts(0, size);
              }
            }}
            className="employee-search-input"
          />
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <select
              value={departmentId ?? ""}
              onChange={(e) =>
                setDepartmentId(e.target.value ? Number(e.target.value) : null)
              }
            >
              <option value="">-- Chọn Bộ Phận --</option>
              {departments.map((d) => (
                <option
                  key={d.id}
                  value={d.id}
                >
                  {d.name}
                </option>
              ))}
            </select>
            <select
              value={lineId ?? ""}
              onChange={(e) =>
                setLineId(e.target.value ? Number(e.target.value) : null)
              }
            >
              <option value="">-- Chọn Chuyền --</option>
              {lines.map((l) => (
                <option
                  key={l.id}
                  value={l.id}
                >
                  {l.name}
                </option>
              ))}
            </select>
            <select
              value={positionId ?? ""}
              onChange={(e) =>
                setPositionId(e.target.value ? Number(e.target.value) : null)
              }
            >
              <option value="">-- Chọn Chức Vụ --</option>
              {positions.map((p) => (
                <option
                  key={p.id}
                  value={p.id}
                >
                  {p.name}
                </option>
              ))}
            </select>
            <select
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              {ROLE_OPTIONS.map((r) => (
                <option
                  key={r.value}
                  value={r.value}
                >
                  {r.label}
                </option>
              ))}
            </select>
            <button onClick={handleClearFilters}>Xóa bộ lọc</button>

            <button
              onClick={() => {
                setPage(0);
                fetchAccounts(0, size);
              }}
            >
              Tìm kiếm
            </button>
          </div>
        </div>

        {/* Table */}
        <AccountTable
          accounts={accounts}
          loading={loading}
          onToggleStatus={handleToggleStatus}
        />

        {/* Pagination */}
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
    </MainLayout>
  );
}

export default AccountManagement;
