import React, { useRef, useState  } from "react";
import MainLayout from "../components/MainLayout";
import InterviewScheduleTable from "../components/InterviewScheduleTable";
import "../styles/ManagementLayout.css";



function InterviewManagement() {
  const tableRef = useRef();
const [searchTerm, setSearchTerm] = useState("");

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

            <form className="form-floating">
              <input type="text" className="form-control" style={{ width: "240px" }} id="floatingInputValue" value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)} />
              <label htmlFor="floatingInputValue">Tìm kiếm theo tên ứng viên</label>
            </form>
{/* 
            <select
              className="form-select"
              style={{ height: "55px", width: "100px" }}
              value={sortOrder}
              onChange={(e) => setSortOrder(e.target.value)}
            >
              <option value="desc">Z - A</option>
              <option value="asc">A - Z</option>
            </select> */}


            <div className="export-button " style={{ height: "55px" }} onClick={handleExportClick}>
              <span className="export-text">Export</span>
            </div>
          </div>
        </div>
        <InterviewScheduleTable ref={tableRef} searchTerm={searchTerm}/>
      </div>
    </MainLayout>
  );
}

export default InterviewManagement;