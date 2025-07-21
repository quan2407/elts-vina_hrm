import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import applicationService from "../services/applicationService";
import MainLayout from "../components/MainLayout";
import "../styles/ApplicationListTable.css";

function ApplicationListPage() {
  const [applications, setApplications] = useState([]);
  const [page, setPage] = useState(0);
  const size = 10;
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    fetchApplications();
  }, [page]);

  const fetchApplications = () => {
    applicationService
      .getMyApplications(page, size)
      .then((res) => {
        setApplications(res.data.content);
        setTotalPages(res.data.totalPages);
      })
      .catch((err) => {
        console.error("Lỗi khi tải danh sách đơn", err);
        alert("Không thể tải danh sách đơn từ server.");
      });
  };

  const handleRowClick = (id) => {
    navigate(`/applications/${id}`);
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
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách đơn đã gửi</h1>
        </div>

        <div className="application-list-wrapper">
          <div className="application-list-table">
            <div className="application-list-header">
              <div className="application-header-cell">Tiêu đề</div>
              <div className="application-header-cell">Từ ngày</div>
              <div className="application-header-cell">Đến ngày</div>
              <div className="application-header-cell">Loại đơn</div>
              <div className="application-header-cell">Trạng thái</div>
            </div>

            {applications.map((app) => (
              <div
                key={app.id}
                className="application-list-row"
                onClick={() => handleRowClick(app.id)}
                style={{ cursor: "pointer" }}
              >
                <div className="application-list-cell">{app.title}</div>
                <div className="application-list-cell">{app.startDate}</div>
                <div className="application-list-cell">{app.endDate}</div>
                <div className="application-list-cell">
                  {app.applicationTypeName}
                </div>
                <div className="application-list-cell">{app.statusLabel}</div>
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
