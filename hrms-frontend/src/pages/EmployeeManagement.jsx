import React from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import EmployeeTable from "../components/EmployeeTable";
import "../styles/ManagementLayout.css";
import { Plus, Download } from "lucide-react";

function EmployeeManagement() {
  const navigate = useNavigate();

  const handleCreate = () => {
    navigate("/employee-create");
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Employee Management</h1>
          <div className="page-actions">
            <button style={{ height: "55px"}}
              className="add-button"
              onClick={handleCreate}
            >
              <span className="export-text">Thêm nhân viên</span>
            </button>
            <button className="export-button" style={{ height: "55px"}}>
              <span className="export-text">Export</span>
            </button>
          </div>
        </div>
        <EmployeeTable />
      </div>
    </MainLayout>
  );
}

export default EmployeeManagement;
