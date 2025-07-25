import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import applicationService from "../services/applicationService";
import MainLayout from "../components/MainLayout";
import "../styles/ApplicationListTable.css";

const STATUS_FILTERS = [
  { label: "Tất cả", value: null },
  { label: "Chờ quản lý duyệt", value: "PENDING_MANAGER_APPROVAL" },
  { label: "Quản lý duyệt", value: "MANAGER_APPROVED" },
  { label: "Quản lý từ chối", value: "MANAGER_REJECTED" },
  { label: "HR duyệt", value: "HR_APPROVED" },
  { label: "HR từ chối", value: "HR_REJECTED" },
  { label: "Đã hủy", value: "CANCELLED_BY_EMPLOYEE" },
];

function ApplicationListPage() {
  const [applications, setApplications] = useState([]);
  const [page, setPage] = useState(0);
  const size = 10;
  const [totalPages, setTotalPages] = useState(0);
  const [selectedStatus, setSelectedStatus] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchApplications();
  }, [page, selectedStatus]);

  const fetchApplications = () => {
    applicationService
      .getMyApplications(page, size, selectedStatus)
      .then((res) => {
        setApplications(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch((err) => {
        console.error("Lỗi khi tải danh sách đơn", err);
        alert("Không thể tải danh sách đơn từ server.");
      });
  };

  const handleCancel = async (id) => {
    const confirm = window.confirm("Bạn chắc chắn muốn hủy đơn này?");
    if (!confirm) return;

    try {
      await applicationService.cancelApplication(id);
      alert("Hủy đơn thành công.");
      fetchApplications();
    } catch (err) {
      console.error("Lỗi khi hủy đơn", err);
      alert("Không thể hủy đơn.");
    }
  };

  const getPageNumbers = () => {
    const delta = 2;
    const range = [];
    for (
      let i = Math.max(0, page - delta);
      i <= Math.min(totalPages - 1, page + delta);
      i++
    ) {
      range.push(i);
    }
    return range;
  };

  return (
    <MainLayout>
      <div className="application-layout-container">
        <div className="application-status-sidebar">
          {STATUS_FILTERS.map((item) => (
            <button
              key={item.label}
              className={`application-filter-btn ${
                selectedStatus === item.value ? "active" : ""
              }`}
              onClick={() => {
                setSelectedStatus(item.value);
                setPage(0);
              }}
            >
              {item.label}
            </button>
          ))}
        </div>

        <div className="application-list-wrapper">
          <div className="application-list-table">
            <div className="application-list-header">
              <div className="application-header-cell">Tiêu đề</div>
              <div className="application-header-cell">Từ ngày</div>
              <div className="application-header-cell">Đến ngày</div>
              <div className="application-header-cell">Loại đơn</div>
              <div className="application-header-cell">Trạng thái</div>
              <div className="application-header-cell">Hành động</div>
            </div>

            {applications.map((app) => (
              <div
                key={app.id}
                className="application-list-row"
              >
                <div className="application-list-cell">{app.title}</div>
                <div className="application-list-cell">{app.startDate}</div>
                <div className="application-list-cell">{app.endDate}</div>
                <div className="application-list-cell">
                  {app.applicationTypeName}
                </div>
                <div className="application-list-cell">{app.statusLabel}</div>
                <div className="application-list-cell">
                  <button
                    onClick={() => navigate(`/applications/${app.id}`)}
                    className="application-list-action-button application-list-view-button"
                  >
                    Xem
                  </button>
                  {app.status === "PENDING_MANAGER_APPROVAL" && (
                    <button
                      onClick={() => handleCancel(app.id)}
                      className="application-list-action-button application-list-cancel-button"
                      style={{ marginLeft: "8px" }}
                    >
                      Hủy
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>

          <div className="application-list-pagination-container">
            <button
              className="application-list-pagination-btn"
              onClick={() => setPage(0)}
              disabled={page === 0}
            >
              «
            </button>
            <button
              className="application-list-pagination-btn"
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
            >
              ‹
            </button>
            {getPageNumbers().map((p) => (
              <button
                key={p}
                onClick={() => setPage(p)}
                className={`application-list-pagination-btn ${
                  p === page ? "application-list-pagination-active" : ""
                }`}
              >
                {p + 1}
              </button>
            ))}
            <button
              className="application-list-pagination-btn"
              onClick={() => setPage(page + 1)}
              disabled={page === totalPages - 1}
            >
              ›
            </button>
            <button
              className="application-list-pagination-btn"
              onClick={() => setPage(totalPages - 1)}
              disabled={page === totalPages - 1}
            >
              »
            </button>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default ApplicationListPage;
