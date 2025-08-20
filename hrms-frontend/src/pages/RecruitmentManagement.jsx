import React, { useEffect, useRef, useState } from "react";
import MainLayout from "../components/MainLayout";
import JobsTable from "../components/JobsTable";
import "../styles/ManagementLayout.css";
import { useNavigate } from "react-router-dom";
import { getAllRecruitments } from "../services/recruitmentService";

function JobsManagement() {
  const tableRef = useRef();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("desc");
  const [sortField, setSortField] = useState("createAt");
  const [jobs, setJobs] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const fetchRecruitments = async (targetPage = page, targetSize = size) => {
    try {
      const res = await getAllRecruitments(
        targetPage,
        targetSize,
        searchTerm,
        sortField,
        sortOrder
      );
      setJobs(res?.content || []);
      setTotalPages(res?.totalPages || 0);
    } catch (e) {
      console.error("Failed to fetch recruitments", e);
      setJobs([]);
      setTotalPages(0);
    }
  };

  useEffect(() => {
    fetchRecruitments(page, size);
  }, [page]);
  useEffect(() => {
    setPage(0);
    fetchRecruitments(0, size);
  }, [size]);
  useEffect(() => {
    setPage(0);
    fetchRecruitments(0, size);
  }, [searchTerm, sortField, sortOrder]);

  const handleExportClick = () => {
    if (tableRef.current) tableRef.current.exportToExcel();
  };

  const handleAddClick = () => {
    navigate("/recruitment-create");
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách tuyển dụng</h1>

          <div className="page-actions">
            <form
              className="form-floating"
              onSubmit={(e) => e.preventDefault()}
            >
              <input
                type="text"
                className="form-control"
                style={{ width: "240px" }}
                id="floatingInputValue"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <label htmlFor="floatingInputValue">Tìm kiếm theo tiêu đề</label>
            </form>

            <select
              className="form-select"
              style={{ height: "55px", width: "250px" }}
              value={sortField}
              onChange={(e) => setSortField(e.target.value)}
            >
              <option value="createAt">Sắp xếp theo ngày tạo</option>
              <option value="title">Sắp xếp theo tiêu đề</option>
              <option value="employmentType">
                Sắp xếp theo loại hình công việc
              </option>
              <option value="expiredAt">Sắp xếp theo ngày hết hạn</option>
            </select>

            <select
              className="form-select"
              style={{ height: "55px", width: "100px" }}
              value={sortOrder}
              onChange={(e) => setSortOrder(e.target.value)}
            >
              <option value="desc">Z - A</option>
              <option value="asc">A - Z</option>
            </select>

            <div className="page-actions">
              <button
                type="button"
                onClick={handleAddClick}
                className="btn-big"
              >
                Thêm bài tuyển dụng
              </button>
              <button
                type="button"
                onClick={handleExportClick}
                className="btn-big btn-green"
              >
                Export
              </button>
            </div>
          </div>
        </div>
        <div
          className="employee-search-wrapper"
          style={{
            display: "flex",
            gap: 8,
            flexWrap: "wrap",
            justifyContent: "space-between",
          }}
        >
          <div
            className="page-size-control"
            style={{ display: "flex", alignItems: "center", gap: 6 }}
          >
            <label htmlFor="jobsPageSize">Hiển thị</label>
            <select
              id="jobsPageSize"
              value={size}
              onChange={(e) => setSize(Number(e.target.value))}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
            <span>/ trang</span>
          </div>
        </div>
        <JobsTable
          ref={tableRef}
          jobs={jobs}
        />
        <div className="employee-pagination-container">
          <button
            className="employee-pagination-btn"
            onClick={() => setPage(0)}
            disabled={page === 0}
          >
            «
          </button>
          <button
            className="employee-pagination-btn"
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
          >
            ‹
          </button>

          {Array.from({ length: totalPages }).map((_, p) => (
            <button
              key={p}
              onClick={() => setPage(p)}
              className={`employee-pagination-btn ${
                p === page ? "employee-pagination-active" : ""
              }`}
            >
              {p + 1}
            </button>
          ))}

          <button
            className="employee-pagination-btn"
            onClick={() => setPage(page + 1)}
            disabled={page === totalPages - 1}
          >
            ›
          </button>
          <button
            className="employee-pagination-btn"
            onClick={() => setPage(totalPages - 1)}
            disabled={page === totalPages - 1}
          >
            »
          </button>
        </div>
      </div>
    </MainLayout>
  );
}

export default JobsManagement;
