import React, { useEffect, useMemo, useState } from "react";
import accountRequestService from "../services/accountRequestService";
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

function AccountRequestPage() {
  // data
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [processingId, setProcessingId] = useState(null);

  // pagination
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  // filters (giống Account)
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentId, setDepartmentId] = useState(null);
  const [positionId, setPositionId] = useState(null);
  const [lineId, setLineId] = useState(null);
  const [status, setStatus] = useState("all");

  // danh mục
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);

  const gridCols = "1.1fr 1.4fr 1fr 1fr 1fr 1.2fr 1fr 1.8fr";

  // load danh mục ban đầu
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

  // khi đổi department → reload position/line theo bộ phận
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

  // fetch list
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
        departmentId: departmentId ?? undefined,
        positionId: positionId ?? undefined,
        lineId: lineId ?? undefined,
      };

      const res = await accountRequestService.getRequests(
        targetPage,
        targetSize,
        filters
      );
      const data = res.data;
      setRequests(Array.isArray(data?.content) ? data.content : []);
      setTotalPages(data?.totalPages ?? 0);
    } catch (err) {
      console.error("Lỗi tải yêu cầu:", err);
      alert("Không thể tải yêu cầu.");
      setRequests([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  // clear filters
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
      departmentId: undefined,
      positionId: undefined,
      lineId: undefined,
    };
    fetchRequests(0, size, cleared);
  };

  // auto refetch theo trang/size
  useEffect(() => {
    fetchRequests(page, size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  // pagination numbers
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

  // actions
  const handleApprove = async (id) => {
    if (!window.confirm("Xác nhận duyệt yêu cầu này?")) return;
    try {
      setProcessingId(id);
      await accountRequestService.approveRequest(id);
      alert("Duyệt thành công!");
      fetchRequests();
    } catch {
      alert("Lỗi duyệt yêu cầu.");
    } finally {
      setProcessingId(null);
    }
  };

  const handleReject = async (id) => {
    if (!window.confirm("Xác nhận từ chối yêu cầu này?")) return;
    try {
      setProcessingId(id);
      await accountRequestService.rejectRequest(id);
      alert("Từ chối thành công!");
      fetchRequests();
    } catch {
      alert("Lỗi từ chối yêu cầu.");
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Yêu cầu tạo tài khoản</h1>
          <div
            className="page-actions"
            style={{ display: "flex", gap: 8, flexWrap: "wrap" }}
          >
            <label htmlFor="reqPageSize">Hiển thị</label>
            <select
              id="reqPageSize"
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

        {/* Filter bar (giống trang Account) */}
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
            {/* Status */}
            <select
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
                setPage(0);
                fetchRequests(0, size, {
                  status: e.target.value,
                  search: searchTerm || undefined,
                  departmentId: departmentId ?? undefined,
                  positionId: positionId ?? undefined,
                  lineId: lineId ?? undefined,
                });
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

            {/* Department */}
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

            {/* Line */}
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

            {/* Position */}
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

        {/* Table */}
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
            requests.map((req) => (
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
                <div className="reset-request-table-cell">
                  {req.approved === null
                    ? "Chờ duyệt"
                    : req.approved
                    ? "Đã duyệt"
                    : "Từ chối"}
                </div>

                <div
                  className="reset-request-table-cell"
                  style={{ display: "flex", justifyContent: "center" }}
                >
                  {req.approved === null ? (
                    <>
                      <button
                        onClick={() => handleApprove(req.id)}
                        disabled={processingId === req.id}
                        style={{
                          backgroundColor: "#1d4ed8",
                          color: "#fff",
                          border: "none",
                          borderRadius: "8px",
                          padding: "8px 14px",
                          fontWeight: "600",
                          cursor: "pointer",
                          opacity: processingId === req.id ? 0.6 : 1,
                        }}
                      >
                        {processingId === req.id ? "Đang xử lý..." : "Duyệt"}
                      </button>
                      <button
                        onClick={() => handleReject(req.id)}
                        disabled={processingId === req.id}
                        style={{
                          backgroundColor: "#dc2626",
                          color: "#fff",
                          border: "none",
                          borderRadius: "8px",
                          padding: "8px 14px",
                          fontWeight: "600",
                          cursor: "pointer",
                          opacity: processingId === req.id ? 0.6 : 1,
                          marginLeft: 8,
                        }}
                      >
                        Từ chối
                      </button>
                    </>
                  ) : (
                    <span style={{ color: "#6b7280" }}>—</span>
                  )}
                </div>
              </div>
            ))
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

export default AccountRequestPage;
