import React, { useEffect, useMemo, useState } from "react";
import authService from "../services/authService";
import departmentService from "../services/departmentService";
import positionService from "../services/positionService";
import { getAllLines } from "../services/linesService";
import MainLayout from "../components/MainLayout";
import "../styles/ResetRequestTable.css";
import "../styles/ManagementLayout.css";

const STATUS_OPTIONS = [
  { value: "all", label: "Tất cả" },
  { value: "pending", label: "Chờ duyệt" },
  { value: "approved", label: "Đã duyệt" },
  { value: "rejected", label: "Từ chối" },
];

function ResetPasswordRequestsPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [processingKey, setProcessingKey] = useState(null);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentId, setDepartmentId] = useState(null);
  const [positionId, setPositionId] = useState(null);
  const [lineId, setLineId] = useState(null);
  const [status, setStatus] = useState("all");
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);
  const gridCols = "1.1fr 1.4fr 1.8fr 1fr 1fr 1fr 1.2fr 1fr 1.2fr";
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
  const departmentName = useMemo(() => {
    if (!departmentId) return undefined;
    const d = departments.find((x) => x.id === departmentId);
    return d?.name;
  }, [departmentId, departments]);

  const positionName = useMemo(() => {
    if (!positionId) return undefined;
    const p = positions.find((x) => x.id === positionId);
    return p?.name;
  }, [positionId, positions]);

  const lineName = useMemo(() => {
    if (!lineId) return undefined;
    const l = lines.find((x) => x.id === lineId);
    return l?.name;
  }, [lineId, lines]);

  const fetchRequests = async (
    targetPage = page,
    targetSize = size,
    filtersOverride = null
  ) => {
    setLoading(true);
    try {
      const filters = filtersOverride ?? {
        status,
        search: searchTerm || undefined,
        departmentName,
        positionName,
        lineName,
      };

      const res = await authService.getResetRequests(
        targetPage,
        targetSize,
        filters
      );
      const data = res.data;
      setRequests(Array.isArray(data?.content) ? data.content : []);
      setTotalPages(data?.totalPages ?? 0);
    } catch (err) {
      console.error("Lỗi tải yêu cầu reset:", err);
      alert("Không thể tải danh sách yêu cầu.");
      setRequests([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };
  const handleClearFilters = () => {
    setSearchTerm("");
    setDepartmentId(null);
    setPositionId(null);
    setLineId(null);
    setStatus("all");
    setPage(0);
    const cleared = {
      status: "all",
      search: undefined,
      departmentName: undefined,
      positionName: undefined,
      lineName: undefined,
    };
    fetchRequests(0, size, cleared);
  };

  useEffect(() => {
    fetchRequests(page, size);
  }, [page, size]);
  const pageNumbers = useMemo(() => {
    const range = [];
    const max = 5;
    if (totalPages <= 0) return range;
    let start = Math.max(0, page - Math.floor(max / 2));
    let end = Math.min(totalPages, start + max);
    if (end - start < max) start = Math.max(0, end - max);
    for (let i = start; i < end; i++) range.push(i);
    return range;
  }, [page, totalPages]);
  const handleApprove = async (email) => {
    if (!window.confirm(`Xác nhận duyệt reset mật khẩu cho: ${email}?`)) return;
    try {
      setProcessingKey(email);
      await authService.approveResetPassword({ email });
      alert("Phê duyệt thành công! Mật khẩu mới đã được gửi.");
      fetchRequests();
    } catch (err) {
      console.error("Phê duyệt thất bại:", err);
      alert("Đã xảy ra lỗi khi duyệt.");
    } finally {
      setProcessingKey(null);
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Yêu cầu reset mật khẩu</h1>

          <div
            className="page-actions"
            style={{ display: "flex", gap: 8, flexWrap: "wrap" }}
          >
            <label htmlFor="resetPageSize">Hiển thị</label>
            <select
              id="resetPageSize"
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
          style={{ display: "flex", gap: 8, flexWrap: "wrap" }}
        >
          <input
            type="text"
            placeholder="Tìm theo mã NV hoặc tên NV"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                setPage(0);
                fetchRequests(0, size);
              }
            }}
            className="employee-search-input"
          />

          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <select
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
              }}
            >
              {STATUS_OPTIONS.map((s) => (
                <option
                  key={s.value}
                  value={s.value}
                >
                  {s.label}
                </option>
              ))}
            </select>
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

            <button onClick={handleClearFilters}>Xóa bộ lọc</button>
            <button
              onClick={() => {
                setPage(0);
                fetchRequests(0, size);
              }}
            >
              Tìm kiếm
            </button>
          </div>
        </div>
        <div className="reset-request-table-container">
          <div
            className="reset-request-table-header"
            style={{
              display: "grid",
              gridTemplateColumns: gridCols,
              columnGap: 16,
              alignItems: "center",
            }}
          >
            <div className="reset-request-header-cell">Mã NV</div>
            <div className="reset-request-header-cell">Nhân viên</div>
            <div className="reset-request-header-cell">Email</div>
            <div className="reset-request-header-cell">Chức vụ</div>
            <div className="reset-request-header-cell">Phòng ban</div>
            <div className="reset-request-header-cell">Chuyền</div>
            <div className="reset-request-header-cell">Thời gian</div>
            <div className="reset-request-header-cell">Trạng thái</div>
            <div className="reset-request-header-cell">Hành động</div>
          </div>

          {loading ? (
            <div style={{ textAlign: "center", marginTop: 20 }}>
              Đang tải...
            </div>
          ) : requests.length === 0 ? (
            <div style={{ textAlign: "center", marginTop: 20 }}>
              Không có yêu cầu nào.
            </div>
          ) : (
            requests.map((req) => {
              const isPending = !req.approved; // ✅ pending theo approvedAt
              const statusLabel = isPending
                ? "Chờ duyệt"
                : req.approved
                ? "Đã duyệt"
                : "Từ chối";
              return (
                <div
                  key={req.id}
                  className="reset-request-table-row"
                  style={{
                    display: "grid",
                    gridTemplateColumns: gridCols,
                    columnGap: 16,
                    alignItems: "center",
                  }}
                >
                  <div className="reset-request-table-cell">
                    {req.employeeCode ?? "—"}
                  </div>
                  <div className="reset-request-table-cell">
                    {req.employeeName ?? "—"}
                  </div>
                  <div className="reset-request-table-cell">
                    {req.email ?? "—"}
                  </div>
                  <div className="reset-request-table-cell">
                    {req.positionName ?? "—"}
                  </div>
                  <div className="reset-request-table-cell">
                    {req.departmentName ?? "—"}
                  </div>
                  <div className="reset-request-table-cell">
                    {req.lineName ?? "—"}
                  </div>
                  <div className="reset-request-table-cell">
                    {req.requestedAt
                      ? new Date(req.requestedAt).toLocaleString("vi-VN")
                      : "—"}
                  </div>
                  <div className="reset-request-table-cell">{statusLabel}</div>

                  <div
                    className="reset-request-table-cell"
                    style={{ display: "flex", justifyContent: "center" }}
                  >
                    {isPending ? (
                      <button
                        onClick={() => handleApprove(req.email)}
                        disabled={processingKey === req.email}
                        style={{
                          backgroundColor: "#1d4ed8",
                          color: "#fff",
                          border: "none",
                          borderRadius: "8px",
                          padding: "8px 14px",
                          fontWeight: "600",
                          cursor: "pointer",
                          opacity: processingKey === req.email ? 0.6 : 1,
                        }}
                      >
                        {processingKey === req.email
                          ? "Đang xử lý..."
                          : "Duyệt"}
                      </button>
                    ) : (
                      <span style={{ color: "#6b7280" }}>—</span>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>

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

export default ResetPasswordRequestsPage;
