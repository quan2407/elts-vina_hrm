import React, { useRef, useState } from "react";
import MainLayout from "../components/MainLayout";
import "../styles/ManagementLayout.css";
import CandidateTable from "../components/CandidateTable";



function CandidateManagement() {
  const tableRef = useRef();
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("desc"); // mặc định mới -> cũ

  const handleExportClick = () => {
    if (tableRef.current) {
      tableRef.current.exportToExcel();
    }
  };


  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách ứng viên</h1>
          <div className="page-actions">

            <form className="form-floating">
              <input type="text" className="form-control" style={{ width: "240px" }} id="floatingInputValue" value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)} />
              <label htmlFor="floatingInputValue">Tìm kiếm theo tên ứng viên</label>
            </form>

            <select
              className="form-select"
              style={{ height: "55px", width: "180px" }}
              value={sortOrder}
              onChange={(e) => setSortOrder(e.target.value)}
            >
              <option value="desc">Mới - cũ</option>
              <option value="asc">Cũ - mới</option>
            </select>
            <div className="export-button " style={{ height: "55px"}} onClick={handleExportClick}>
              <span className="export-text">Export</span>
            </div>
          </div>
        </div>
        <CandidateTable ref={tableRef} searchTerm={searchTerm} sortOrder={sortOrder} />
      </div>
    </MainLayout>
  );
}

export default CandidateManagement;
