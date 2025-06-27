import React from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import EmployeeTable from "../components/EmployeeTable";
import "../styles/ManagementLayout.css";
import { Plus, Download } from "lucide-react";
import employeeService from "../services/employeeService";

function EmployeeManagement() {
  const navigate = useNavigate();

  const handleCreate = () => {
    navigate("/employee-create");

  };
  const handleExport = async () => {
    try {
      const response = await employeeService.exportFile();

      const url = window.URL.createObjectURL(new Blob([response.data]));

      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "danhsachtuyendung.xlsx"); // Tên file khi tải về
      document.body.appendChild(link);
      link.click();

      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Export failed:", error);
      alert("Export failed. Please try again.");
    }

  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">

          <h1 className="page-title"> Danh sách nhân viên</h1>

          <div className="page-actions">
            {/* Button Create */}

            <button
              className="add-button"
              onClick={handleCreate}
            >
              <span className="export-text">Thêm nhân viên</span>
            </button>
            <button
              className="export-button"
              onClick={handleExport}
            >
              <Download
                size={16}
                style={{ marginRight: "6px" }}
              />
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
