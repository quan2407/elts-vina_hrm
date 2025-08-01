import React, { useEffect, useState } from "react";
import authService from "../services/authService";
import MainLayout from "../components/MainLayout";
import "../styles/ResetRequestTable.css";
import "../styles/ManagementLayout.css";

function ResetPasswordRequestsPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [approvingEmail, setApprovingEmail] = useState(null);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const res = await authService.getPendingResetRequests(page, size);
      setRequests(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error("Lỗi khi tải yêu cầu reset:", err);
      alert("Không thể tải danh sách yêu cầu.");
    } finally {
      setLoading(false);
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

  const handleApprove = async (email) => {
    if (!window.confirm(`Xác nhận duyệt reset mật khẩu cho: ${email}?`)) return;
    try {
      setApprovingEmail(email);
      await authService.approveResetPassword({ email });
      alert("Phê duyệt thành công! Mật khẩu mới đã được gửi.");
      await fetchRequests();
    } catch (err) {
      console.error("Phê duyệt thất bại:", err);
      alert("Đã xảy ra lỗi khi duyệt.");
    } finally {
      setApprovingEmail(null);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [page]);

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Duyệt yêu cầu reset mật khẩu</h1>
        </div>

        <div className="reset-request-table-container">
          <div className="reset-request-table-header">
            <div className="reset-request-header-cell">Email</div>
            <div className="reset-request-header-cell">Thời gian yêu cầu</div>
            <div className="reset-request-header-cell">Trạng thái</div>
            <div className="reset-request-header-cell">Hành động</div>
          </div>

          {loading ? (
            <div style={{ textAlign: "center", marginTop: "20px" }}>
              Đang tải...
            </div>
          ) : requests.length === 0 ? (
            <div style={{ textAlign: "center", marginTop: "20px" }}>
              Không có yêu cầu nào chờ duyệt.
            </div>
          ) : (
            requests.map((req) => (
              <div
                key={req.id}
                className="reset-request-table-row"
              >
                <div className="reset-request-table-cell">{req.email}</div>
                <div className="reset-request-table-cell">
                  {new Date(req.requestedAt).toLocaleString()}
                </div>
                <div className="reset-request-table-cell">
                  {req.approved ? "Đã duyệt" : "Chưa duyệt"}
                </div>
                <div className="reset-request-table-cell">
                  <button
                    className="reset-request-approve-button"
                    onClick={() => handleApprove(req.email)}
                    disabled={approvingEmail === req.email}
                  >
                    {approvingEmail === req.email
                      ? "Đang xử lý..."
                      : "Phê duyệt"}
                  </button>
                </div>
              </div>
            ))
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="reset-pagination-container">
              <button
                className="reset-pagination-btn"
                onClick={() => setPage(0)}
                disabled={page === 0}
              >
                «
              </button>
              <button
                className="reset-pagination-btn"
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
              >
                ‹
              </button>

              {getPageNumbers().map((p) => (
                <button
                  key={p}
                  onClick={() => setPage(p)}
                  className={`reset-pagination-btn ${
                    p === page ? "reset-pagination-active" : ""
                  }`}
                >
                  {p + 1}
                </button>
              ))}

              <button
                className="reset-pagination-btn"
                onClick={() => setPage(page + 1)}
                disabled={page === totalPages - 1}
              >
                ›
              </button>
              <button
                className="reset-pagination-btn"
                onClick={() => setPage(totalPages - 1)}
                disabled={page === totalPages - 1}
              >
                »
              </button>
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
}

export default ResetPasswordRequestsPage;
