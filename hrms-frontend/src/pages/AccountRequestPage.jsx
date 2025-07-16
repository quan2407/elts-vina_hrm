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

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const res = await accountRequestService.getRequestsByStatus(activeTab);
      const data = res.data;

      if (Array.isArray(data)) {
        setRequests(data);
      } else if (Array.isArray(data?.content)) {
        setRequests(data.content);
      } else {
        setRequests([]);
      }
    } catch (err) {
      console.error("Lỗi tải yêu cầu:", err);
      alert("Không thể tải yêu cầu.");
      setRequests([]);
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
  }, [activeTab]);

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
              onClick={() => setActiveTab(tab.key)}
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
        </div>
      </div>
    </MainLayout>
  );
}

export default AccountRequestPage;
