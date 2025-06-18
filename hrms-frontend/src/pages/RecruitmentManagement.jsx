import React,  { useRef } from "react";
import MainLayout from "../components/MainLayout";
import JobsTable from "../components/JobsTable";
import "../styles/ManagementLayout.css";



function JobsManagement() {
    const tableRef = useRef();

  const handleExportClick = () => {
    if (tableRef.current) {
      tableRef.current.exportToExcel();
    }
  };
  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách tuyển dụng</h1>
          <div className="page-actions">
            <div className="filter-button">
              <svg
                className="filter-icon"
                width="26"
                height="30"
                viewBox="0 0 26 30"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g clipPath="url(#clip0_2_1918)">
                  <path
                    d="M24.6877 0.544922H1.75414C0.70036 0.544922 0.168626 2.01455 0.915288 2.87277L10.0572 13.3822V25.0898C10.0572 25.5348 10.2461 25.9517 10.5633 26.207L14.5178 29.3876C15.2979 30.0153 16.3845 29.3791 16.3845 28.2704V13.3822L25.5266 2.87277C26.2718 2.01625 25.7437 0.544922 24.6877 0.544922Z"
                    fill="#1A1A1A"
                  />
                </g>
                <defs>
                  <clipPath id="clip0_2_1918">
                    <rect
                      width="25.3089"
                      height="29.0903"
                      fill="white"
                      transform="translate(0.566406 0.544922)"
                    />
                  </clipPath>
                </defs>
              </svg>
            </div>
            <div className="export-button" onClick={handleExportClick}>
              <span className="export-text">Export</span>

            </div>
          </div>
        </div>
        <JobsTable ref={tableRef} />
      </div>
    </MainLayout>
  );
}

export default JobsManagement;
