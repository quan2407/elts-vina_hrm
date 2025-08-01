import React, { useRef, useState } from "react";
import MainLayout from "../components/MainLayout";
import JobsTable from "../components/JobsTable";
import "../styles/ManagementLayout.css";
import { useNavigate } from "react-router-dom";



function JobsManagement() {
  const tableRef = useRef();
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("desc");
  const [sortField, setSortField] = useState("createAt");
  const navigate = useNavigate();

  const handleExportClick = () => {
    if (tableRef.current) {
      tableRef.current.exportToExcel();
    }
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

            <form className="form-floating" onSubmit={(e) => e.preventDefault()} >
              <input type="text" className="form-control" style={{ width: "240px" }} id="floatingInputValue" value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)} />
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
              <option value="employmentType">Sắp xếp theo loại hình công việc</option>
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

            <div className="add-button " style={{ height: "55px" }} onClick={handleAddClick}>
              <span className="export-text">Thêm bài tuyển dụng</span>
            </div>

            <div className="export-button " style={{ height: "55px" }} onClick={handleExportClick}>
              <span className="export-text">Export</span>
            </div>
          </div>
        </div>
        <JobsTable ref={tableRef} searchTerm={searchTerm} sortOrder={sortOrder} sortField={sortField} />
      </div>
    </MainLayout>
  );
}

export default JobsManagement;