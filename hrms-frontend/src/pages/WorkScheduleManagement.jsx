import React from "react";
import MainLayout from "../components/MainLayout";
import WorkScheduleTable from "../components/WorkScheduleTable";
import "../styles/WorkScheduleManagement.css";
import { Plus, Download } from "lucide-react";

function WorkScheduleManagement() {
  const handleCreate = () => {
    // Tạm thời chưa xử lý chức năng tạo lịch
    alert("Chức năng tạo lịch sẽ được cập nhật sau");
  };

  const handleExport = () => {
    // Tạm thời chưa xử lý export
    alert("Chức năng xuất file chưa được hỗ trợ");
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Lịch làm việc theo tháng</h1>

          <div className="work-schedule-page-actions">
            <button
              className="work-schedule-add-button"
              onClick={handleCreate}
            >
              <Plus
                size={16}
                style={{ marginRight: "6px" }}
              />
              <span>Tạo mới</span>
            </button>

            <button
              className="work-schedule-export-button"
              onClick={handleExport}
            >
              <Download
                size={16}
                style={{ marginRight: "6px" }}
              />
              <span>Xuất file</span>
            </button>
          </div>
        </div>

        <WorkScheduleTable />
      </div>
    </MainLayout>
  );
}

export default WorkScheduleManagement;
