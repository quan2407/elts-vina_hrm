import React, { useState, useEffect } from "react";
import MainLayout from "../components/MainLayout";
import WorkScheduleTable from "../components/WorkScheduleTable";
import "../styles/WorkScheduleManagement.css";
import { Plus, Download } from "lucide-react";
import workScheduleService from "../services/workScheduleService";

function WorkScheduleManagement() {
  const today = new Date();
  const [month, setMonth] = useState(today.getMonth() + 1);
  const [year, setYear] = useState(today.getFullYear());
  const [status, setStatus] = useState("not-submitted");
  const handleSubmit = () => {
    workScheduleService
      .submitWorkSchedules(month, year)
      .then(() => {
        alert("Gửi lịch làm việc thành công!");

        return workScheduleService.getWorkScheduleByMonth(month, year);
      })
      .then((res) => {
        const flatList = res.data.flatMap((dept) => dept.lines);
        const allAccepted = flatList.every((line) => line.isAccepted);
        const allSubmitted = flatList.every((line) => line.isSubmitted);

        if (allAccepted) setStatus("approved");
        else if (allSubmitted) setStatus("submitted");
        else setStatus("not-submitted");
      })
      .catch((err) => {
        console.error("Lỗi gửi lịch:", err);
        alert("Có lỗi xảy ra khi gửi lịch làm việc.");
      });
  };

  const handleExport = () => {
    alert("Chức năng xuất file chưa được hỗ trợ");
  };

  const getStatusLabel = () => {
    switch (status) {
      case "approved":
        return <span className="status approved">Đã duyệt</span>;
      case "submitted":
        return <span className="status submitted">Đã gửi</span>;
      case "not-submitted":
      default:
        return <span className="status not-submitted">Chưa gửi</span>;
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <div>
            <h1 className="page-title">Lịch làm việc theo tháng</h1>
            <div className="work-schedule-status-bar">{getStatusLabel()}</div>
          </div>

          <div className="work-schedule-page-actions">
            <button
              className="work-schedule-add-button"
              onClick={handleSubmit}
            >
              <Plus
                size={16}
                style={{ marginRight: "6px" }}
              />
              <span>Gửi</span>
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
        <WorkScheduleTable
          month={month}
          year={year}
          setMonth={setMonth}
          setYear={setYear}
          onStatusChange={(newStatus) => {
            console.log(
              "📥 Trạng thái cập nhật từ WorkScheduleTable:",
              newStatus
            );
            if (
              newStatus === "approved" ||
              newStatus === "submitted" ||
              newStatus === "not-submitted"
            ) {
              setStatus(newStatus);
            }
          }}
          onMonthYearChange={(m, y) => {
            console.log("📤 Gọi onMonthYearChange với:", m, y);
            setMonth(m);
            setYear(y);
          }}
        />
      </div>
    </MainLayout>
  );
}

export default WorkScheduleManagement;
