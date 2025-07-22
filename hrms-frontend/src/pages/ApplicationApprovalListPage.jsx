import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import applicationApprovalService from "../services/applicationApprovalService";
import MainLayout from "../components/MainLayout";
import "../styles/ApplicationApprovalListPage.css";

function ApplicationApprovalListPage({ step = 1 }) {
  const [applications, setApplications] = useState([]);
  const [page, setPage] = useState(0);
  const size = 10;
  const [totalPages, setTotalPages] = useState(0);
  const [selectedStatus, setSelectedStatus] = useState(null);
  const navigate = useNavigate();
  const ALL_STATUS_FILTERS = [
    { label: "Tất cả", value: null },
    { label: "Chờ quản lý duyệt", value: "PENDING_MANAGER_APPROVAL" },
    { label: "Quản lý đã duyệt", value: "MANAGER_APPROVED" },
    { label: "Quản lý từ chối", value: "MANAGER_REJECTED" },
    { label: "HR đã duyệt", value: "HR_APPROVED" },
    { label: "HR từ chối", value: "HR_REJECTED" },
    { label: "Nhân viên hủy", value: "CANCELLED_BY_EMPLOYEE" },
  ];

  const STATUS_FILTERS =
    step === 1
      ? ALL_STATUS_FILTERS.slice(0, 4) // Tất cả + QL duyệt
      : [
          ALL_STATUS_FILTERS[0],
          ...ALL_STATUS_FILTERS.filter((item) =>
            ["MANAGER_APPROVED", "HR_APPROVED", "HR_REJECTED"].includes(
              item.value
            )
          ),
        ];
  useEffect(() => {
    fetchApplications();
  }, [page, selectedStatus]);

  const fetchApplications = () => {
    const serviceMethod =
      step === 1
        ? applicationApprovalService.getStep1Applications
        : applicationApprovalService.getStep2Applications;

    serviceMethod(page, size, selectedStatus)
      .then((res) => {
        setApplications(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch((err) => {
        console.error("Lỗi khi tải danh sách đơn duyệt", err);
        alert("Không thể tải danh sách.");
      });
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
      <div className="approval-list-container">
        <div className="approval-list-sidebar">
          {STATUS_FILTERS.map((item) => (
            <button
              key={item.label}
              className={`approval-list-filter-btn ${
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

        <div className="approval-list-wrapper">
          <div className="approval-list-table">
            <div className="approval-list-header">
              <div className="approval-list-header-cell">Tiêu đề</div>
              <div className="approval-list-header-cell">Từ ngày</div>
              <div className="approval-list-header-cell">Đến ngày</div>
              <div className="approval-list-header-cell">Loại đơn</div>
              <div className="approval-list-header-cell">Mã NV</div>
              <div className="approval-list-header-cell">Tên NV</div>
              <div className="approval-list-header-cell">Chức vụ</div>
              <div className="approval-list-header-cell">Phòng ban</div>
              <div className="approval-list-header-cell">Line</div>
              <div className="approval-list-header-cell">Trạng thái</div>
              <div className="approval-list-header-cell">Hành động</div>
            </div>

            {applications.map((app) => (
              <div
                key={app.id}
                className="approval-list-row"
              >
                <div className="approval-list-cell">{app.title}</div>
                <div className="approval-list-cell">{app.startDate}</div>
                <div className="approval-list-cell">{app.endDate}</div>
                <div className="approval-list-cell">
                  {app.applicationTypeName}
                </div>
                <div className="approval-list-cell">{app.employeeCode}</div>
                <div className="approval-list-cell">{app.employeeName}</div>
                <div className="approval-list-cell">{app.positionName}</div>
                <div className="approval-list-cell">{app.departmentName}</div>
                <div className="approval-list-cell">{app.lineName}</div>
                <div className="approval-list-cell">{app.statusLabel}</div>
                <div className="approval-list-cell">
                  <button
                    onClick={() => navigate(`/applications/${app.id}`)}
                    className="approval-list-action-btn approval-list-view-btn"
                  >
                    Xem
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="approval-list-pagination">
            <button
              onClick={() => setPage(0)}
              disabled={page === 0}
              className="approval-list-page-btn"
            >
              «
            </button>
            <button
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
              className="approval-list-page-btn"
            >
              ‹
            </button>
            {getPageNumbers().map((p) => (
              <button
                key={p}
                onClick={() => setPage(p)}
                className={`approval-list-page-btn ${
                  p === page ? "active" : ""
                }`}
              >
                {p + 1}
              </button>
            ))}
            <button
              onClick={() => setPage(page + 1)}
              disabled={page === totalPages - 1}
              className="approval-list-page-btn"
            >
              ›
            </button>
            <button
              onClick={() => setPage(totalPages - 1)}
              disabled={page === totalPages - 1}
              className="approval-list-page-btn"
            >
              »
            </button>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default ApplicationApprovalListPage;
