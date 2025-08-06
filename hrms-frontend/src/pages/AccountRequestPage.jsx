import React, { useEffect, useState } from "react";
import accountRequestService from "../services/accountRequestService";
import MainLayout from "../components/MainLayout";
import "../styles/ManagementLayout.css";
import "../styles/AccountRequestPage.css";

const TABS = [
  { key: "pending", label: "Chờ duyệt" },
  { key: "approved", label: "Đã duyệt" },
  { key: "rejected", label: "Từ chối" },
];

function AccountRequestPage() {
  const [activeTab, setActiveTab] = useState("pending");
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [processingId, setProcessingId] = useState(null);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
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

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const res = await accountRequestService.getRequestsByStatus(
        activeTab,
        page,
        size
      );
      const data = res.data;
      if (Array.isArray(data)) {
        setRequests(data);
        setTotalPages(1);
      } else if (Array.isArray(data?.content)) {
        setRequests(data.content);
        setTotalPages(data.totalPages);
      } else {
        setRequests([]);
        setTotalPages(0);
      }
    } catch (err) {
      console.error("Lỗi tải yêu cầu:", err);
      alert("Không thể tải yêu cầu.");
      setRequests([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    if (!window.confirm("Xác nhận duyệt yêu cầu này?")) return;
    try {
      setProcessingId(id);
      await accountRequestService.approveRequest(id);
      alert("Duyệt thành công!");
      fetchRequests();
    } catch (err) {
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
    } catch (err) {
      alert("Lỗi từ chối yêu cầu.");
    } finally {
      setProcessingId(null);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, [activeTab, page]);

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Yêu cầu tạo tài khoản</h1>
        </div>

        <div className="account-request-tabs">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              className={`account-request-tab ${
                activeTab === tab.key ? "active" : ""
              }`}
              onClick={() => {
                setActiveTab(tab.key);
                setPage(0);
              }}
              style={{ color: activeTab === tab.key ? "#4f46e5" : "black" }}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <div className="account-request-table">
          <div className="account-request-table-header">
            <div>Nhân viên</div>
            <div>Thời gian</div>
            <div>Trạng thái</div>
            <div>Hành động</div>
          </div>

          {loading ? (
            <div className="loading">Đang tải...</div>
          ) : requests.length === 0 ? (
            <div className="empty">Không có yêu cầu nào.</div>
          ) : (
            requests.map((req) => (
              <div
                key={req.id}
                className="account-request-table-row"
              >
                <div>{req.employeeName || "Không có tên"}</div>
                <div>
                  {req.requestedAt
                    ? new Date(req.requestedAt).toLocaleString()
                    : ""}
                </div>
                <div>
                  {req.approved === null
                    ? "Chờ duyệt"
                    : req.approved
                    ? "Đã duyệt"
                    : "Từ chối"}
                </div>
                <div>
                  {req.approved === null && (
                    <>
                      <button
                        onClick={() => handleApprove(req.id)}
                        disabled={processingId === req.id}
                      >
                        {processingId === req.id ? "..." : "Duyệt"}
                      </button>
                      <button
                        onClick={() => handleReject(req.id)}
                        disabled={processingId === req.id}
                      >
                        Từ chối
                      </button>
                    </>
                  )}
                </div>
              </div>
            ))
          )}
          {totalPages > 1 && (
            <div className="account-request-pagination-container">
              <button
                className="account-request-pagination-btn"
                onClick={() => setPage(0)}
                disabled={page === 0}
              >
                «
              </button>
              <button
                className="account-request-pagination-btn"
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
              >
                ‹
              </button>

              {getPageNumbers().map((p) => (
                <button
                  key={p}
                  onClick={() => setPage(p)}
                  className={`account-request-pagination-btn ${
                    p === page ? "account-request-pagination-active" : ""
                  }`}
                >
                  {p + 1}
                </button>
              ))}

              <button
                className="account-request-pagination-btn"
                onClick={() => setPage(page + 1)}
                disabled={page === totalPages - 1}
              >
                ›
              </button>
              <button
                className="account-request-pagination-btn"
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

export default AccountRequestPage;
