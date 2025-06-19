import React from "react";
import { useNavigate } from "react-router-dom"; // ✅
import MainLayout from "../components/MainLayout";
import EmployeeTable from "../components/EmployeeTable";
import "../styles/ManagementLayout.css";
import { Plus, Download } from "lucide-react";

function EmployeeManagement() {
  const navigate = useNavigate(); // ✅

  const handleCreate = () => {
    navigate("/employee-create"); // ✅
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Employee Management</h1>
          <div className="employee-page-actions">
            {/* Button Create */}
            <button
              className="employee-add-button"
              onClick={handleCreate}
            >
              <Plus
                size={16}
                style={{ marginRight: "6px" }}
              />
              <span className="employee-create-text">Create</span>
            </button>

            {/* Button Export */}
            <button className="employee-export-button">
              <Download
                size={16}
                style={{ marginRight: "6px" }}
              />
              <span className="employee-export-text">Export</span>
            </button>
          </div>
        </div>
        <EmployeeTable />
      </div>
    </MainLayout>
  );
}

export default EmployeeManagement;
