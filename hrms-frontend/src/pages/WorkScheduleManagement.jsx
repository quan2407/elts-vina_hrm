import React, { useState, useEffect } from "react";
import MainLayout from "../components/MainLayout";
import WorkScheduleTable from "../components/WorkScheduleTable";
import "../styles/WorkScheduleManagement.css";
import CustomRangeModal from "../components/CustomRangeModal";
import { Plus, Download } from "lucide-react";
import workScheduleService from "../services/workScheduleService";
import departmentService from "../services/departmentService";
import SuccessModal from "../components/popup/SuccessModal";
function WorkScheduleManagement() {
  const today = new Date();
  const [month, setMonth] = useState(today.getMonth() + 1);
  const [year, setYear] = useState(today.getFullYear());
  const [status, setStatus] = useState("not-submitted");
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const [rejectReason, setRejectReason] = useState("");
  const [showRangeModal, setShowRangeModal] = useState(false);
  const [departments, setDepartments] = useState([]);
  const [lines, setLines] = useState([]);
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "success", // "success" hoặc "error"
  });
  const [hasData, setHasData] = useState(false);
  const showSuccess = (title, message) => {
    setModal({ open: true, title, message, type: "success" });
  };

  const showError = (title, message) => {
    setModal({ open: true, title, message, type: "error" });
  };

  const handleDepartmentChange = (deptId) => {
    departmentService
      .getLinesByDepartment(deptId)
      .then((res) => setLines(res.data));
  };

  const handleSubmit = () => {
    workScheduleService
      .submitWorkSchedules(month, year)
      .then(() => {
        showSuccess("Gửi lịch làm việc", "Gửi lịch làm việc thành công!");
        setReloadTrigger((prev) => prev + 1);
      })
      .catch((err) => {
        console.error("Lỗi gửi lịch:", err);
        showError("Gửi lịch làm việc", "Có lỗi xảy ra khi gửi lịch làm việc.");
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
  useEffect(() => {
    if (showRangeModal) {
      departmentService
        .getAllDepartments()
        .then((res) => setDepartments(res.data));
    }
  }, [showRangeModal]);

  useEffect(() => {
    if (departments.length > 0) {
      departmentService
        .getLinesByDepartment(departments[0].departmentId)
        .then((res) => setLines(res.data));
    }
  }, [departments]);

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <div>
            <h1 className="page-title">Lịch làm việc theo tháng</h1>
            <div className="work-schedule-status-bar">
              {getStatusLabel()}
              {status === "not-submitted" && rejectReason && (
                <p className="reject-reason">Lý do từ chối: {rejectReason}</p>
              )}
            </div>
          </div>

          {hasData && (
            <div className="work-schedule-page-actions">
              <button
                className="work-schedule-add-button"
                onClick={() => setShowRangeModal(true)}
                disabled={status === "approved"}
              >
                <Plus
                  size={16}
                  style={{ marginRight: "6px" }}
                />
                Dải lịch theo khoảng
              </button>

              <button
                className="work-schedule-add-button"
                onClick={handleSubmit}
                disabled={status === "approved"}
              >
                <Plus
                  size={16}
                  style={{ marginRight: "6px" }}
                />
                <span>Gửi</span>
              </button>

              <button
                className="work-schedule-add-button"
                onClick={async () => {
                  try {
                    const response =
                      await workScheduleService.exportWorkSchedule(month, year);
                    const blob = new Blob([response.data], {
                      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    });
                    const url = window.URL.createObjectURL(blob);
                    const link = document.createElement("a");
                    link.href = url;
                    link.setAttribute(
                      "download",
                      `kehoach_lichsanxuat_thang_${month
                        .toString()
                        .padStart(2, "0")}_${year}.xlsx`
                    );
                    document.body.appendChild(link);
                    link.click();
                    link.remove();
                  } catch (err) {
                    console.error("Export lỗi:", err);
                    alert("Không thể xuất file Excel.");
                  }
                }}
                style={{
                  backgroundColor: "#2563eb",
                  color: "white",
                  marginRight: "8px",
                }}
              >
                <Download
                  size={16}
                  style={{ marginRight: "6px" }}
                />
                Xuất Excel
              </button>
            </div>
          )}
        </div>
        <CustomRangeModal
          isOpen={showRangeModal}
          onClose={() => setShowRangeModal(false)}
          departments={departments}
          lines={lines}
          month={month}
          year={year}
          onDepartmentChange={handleDepartmentChange}
          onSubmit={async (payload) => {
            await workScheduleService.createCustomWorkSchedule(payload);
            alert("Dải lịch thành công!");
            setShowRangeModal(false);
            setReloadTrigger((prev) => prev + 1);
          }}
        />

        <WorkScheduleTable
          month={month}
          year={year}
          setMonth={setMonth}
          setYear={setYear}
          reloadTrigger={reloadTrigger}
          onStatusChange={(newStatus) => {
            console.log("Trạng thái cập nhật từ WorkScheduleTable:", newStatus);
            if (
              newStatus === "approved" ||
              newStatus === "submitted" ||
              newStatus === "not-submitted"
            ) {
              setStatus(newStatus);
            }
          }}
          onRejectReasonChange={setRejectReason}
          onMonthYearChange={(m, y) => {
            setMonth(m);
            setYear(y);
          }}
          onHasDataChange={setHasData}
        />
      </div>
      {modal.open && (
        <SuccessModal
          title={modal.title}
          message={modal.message}
          type={modal.type}
          onClose={() => setModal((m) => ({ ...m, open: false }))}
        />
      )}
    </MainLayout>
  );
}

export default WorkScheduleManagement;
