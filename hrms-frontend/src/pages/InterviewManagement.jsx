import React, { useRef, useState } from "react";
import MainLayout from "../components/MainLayout";
import InterviewScheduleTable from "../components/InterviewScheduleTable";
import "../styles/ManagementLayout.css";

function InterviewManagement() {
  const tableRef = useRef();
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("desc");      // sắp xếp
  const [statusFilter, setStatusFilter] = useState("ALL"); // lọc trạng thái

  const handleExportClick = () => {
    if (tableRef.current) {
      tableRef.current.exportToExcel();
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách phỏng vấn</h1>
          <div className="page-actions">
            <form className="form-floating" onSubmit={(e) => e.preventDefault()}>
              <input
                type="text"
                className="form-control"
                style={{ width: "240px" }}
                id="floatingInputValue"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <label htmlFor="floatingInputValue">
                Tìm kiếm theo tên ứng viên, tuyển dụng
              </label>
            </form>

            {/* Sắp xếp */}
            <select
              className="form-select"
              style={{ height: "55px", width: "180px" }}
              value={sortOrder}
              onChange={(e) => setSortOrder(e.target.value)}
            >
              <option value="desc">Tên ứng viên A-Z</option>
              <option value="asc">Tên ứng viên Z-A</option>
            </select>

            {/* Lọc theo trạng thái */}
            <select
              className="form-select"
              style={{ height: "55px", width: "200px" }}
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="ALL">Tất cả trạng thái</option>
              <option value="WAITING_INTERVIEW">Chờ phỏng vấn</option>
              <option value="CANCEL">Hủy</option>
              <option value="INTERVIEWED">Đã phỏng vấn</option>
            </select>

            <button
              type="button"
              onClick={handleExportClick}
              style={{
                backgroundColor: "#22c55e",
                color: "#fff",
                padding: "10px 20px",
                borderRadius: "8px",
                border: "none",
                fontSize: "16px",
                fontWeight: 500,
                cursor: "pointer",
                height: "55px",
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                transition: "background-color 0.2s ease",
              }}
              onMouseOver={(e) =>
                (e.currentTarget.style.backgroundColor = "#15803d")
              }
              onMouseOut={(e) =>
                (e.currentTarget.style.backgroundColor = "#22c55e")
              }
            >
              Xuất excel
            </button>
          </div>
        </div>

        <InterviewScheduleTable
          ref={tableRef}
          searchTerm={searchTerm}
          sortOrder={sortOrder}
          statusFilter={statusFilter} // 👈 truyền xuống để bảng lọc
        />
      </div>
    </MainLayout>
  );
}

export default InterviewManagement;
