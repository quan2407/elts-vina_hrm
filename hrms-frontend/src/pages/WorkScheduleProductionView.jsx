import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import WorkScheduleTable from "../components/WorkScheduleTable";
import "../styles/WorkScheduleManagement.css";
import { Plus, Download } from "lucide-react";
import workScheduleService from "../services/workScheduleService";

function WorkScheduleProductionView({ canApprove = true }) {
  const today = new Date();
  const [month, setMonth] = useState(today.getMonth() + 1);
  const [year, setYear] = useState(today.getFullYear());
  const [status, setStatus] = useState("not-submitted");
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectReason, setRejectReason] = useState("");
  const [showRequestModal, setShowRequestModal] = useState(false);
  const [requestReason, setRequestReason] = useState("");
  const [needRevision, setNeedRevision] = useState(false);

  const getStatusLabel = () => {
    if (needRevision) {
      return <span className="status revision">Cần chỉnh sửa lại</span>;
    }
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

  const handleApprove = () => {
    if (
      !window.confirm(
        "Bạn có chắc chắn muốn duyệt toàn bộ lịch làm việc cho tháng này?"
      )
    )
      return;

    workScheduleService
      .acceptWorkSchedules(month, year)
      .then(() => {
        alert("Duyệt lịch làm việc thành công!");
        setStatus("approved");
      })
      .catch((err) => {
        console.error("Lỗi duyệt lịch:", err);
        alert("Đã xảy ra lỗi khi duyệt lịch làm việc.");
      });
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <div>
            <h1 className="page-title">
              Lịch làm việc theo tháng (chế độ xem)
            </h1>
            <div className="work-schedule-status-bar">
              {getStatusLabel()}
              {status === "not-submitted" && rejectReason && (
                <p className="reject-reason">Lý do từ chối: {rejectReason}</p>
              )}
            </div>
          </div>
          <div className="work-schedule-page-actions">
            {canApprove && (
              <>
                <button
                  className="work-schedule-add-button"
                  onClick={() => setShowRejectModal(true)}
                  disabled={status !== "submitted"}
                  style={{
                    backgroundColor:
                      status === "submitted" ? "#dc2626" : "#999",
                    cursor: status === "submitted" ? "pointer" : "not-allowed",
                    marginLeft: "8px",
                  }}
                >
                  <Plus
                    size={16}
                    style={{ marginRight: "6px" }}
                  />
                  <span>Từ chối</span>
                </button>
                {showRejectModal && (
                  <div className="work-schedule-reject-overlay">
                    <div className="work-schedule-reject-modal">
                      <h3>
                        Từ chối lịch làm việc tháng {month}/{year}
                      </h3>
                      <textarea
                        value={rejectReason}
                        onChange={(e) => setRejectReason(e.target.value)}
                        rows={4}
                        placeholder="Nhập lý do từ chối..."
                      />
                      <div className="work-schedule-reject-actions">
                        <button onClick={() => setShowRejectModal(false)}>
                          Hủy
                        </button>
                        <button
                          onClick={() => {
                            workScheduleService
                              .rejectWorkSchedules(month, year, rejectReason)
                              .then(() => {
                                alert("Từ chối lịch làm việc thành công!");
                                setStatus("not-submitted");
                                setShowRejectModal(false);
                              })
                              .catch(() => {
                                alert("Lỗi khi từ chối lịch");
                              });
                          }}
                          disabled={!rejectReason.trim()}
                        >
                          Gửi lý do
                        </button>
                      </div>
                    </div>
                  </div>
                )}

                <button
                  className="work-schedule-add-button"
                  onClick={handleApprove}
                  disabled={status !== "submitted"}
                  style={{
                    backgroundColor:
                      status === "submitted" ? "#16a34a" : "#999", // Xanh lá khi được bấm, xám khi disable
                    cursor: status === "submitted" ? "pointer" : "not-allowed",
                  }}
                >
                  <Plus
                    size={16}
                    style={{ marginRight: "6px" }}
                  />
                  <span>Duyệt</span>
                </button>
                <button
                  className="work-schedule-add-button"
                  onClick={() => setShowRequestModal(true)}
                  disabled={status !== "approved"}
                  style={{
                    backgroundColor: status === "approved" ? "#eab308" : "#999",
                    cursor: status === "approved" ? "pointer" : "not-allowed",
                    marginLeft: "8px",
                  }}
                >
                  <Plus
                    size={16}
                    style={{ marginRight: "6px" }}
                  />
                  <span>Yêu cầu chỉnh sửa</span>
                </button>
                {showRequestModal && (
                  <div className="work-schedule-reject-overlay">
                    <div className="work-schedule-reject-modal">
                      <h3>
                        Yêu cầu chỉnh sửa lịch tháng {month}/{year}
                      </h3>
                      <textarea
                        value={requestReason}
                        onChange={(e) => setRequestReason(e.target.value)}
                        rows={4}
                        placeholder="Nhập lý do cần sửa..."
                      />
                      <div className="work-schedule-reject-actions">
                        <button onClick={() => setShowRequestModal(false)}>
                          Hủy
                        </button>
                        <button
                          disabled={!requestReason.trim()}
                          onClick={() => {
                            workScheduleService
                              .requestRevision(month, year, requestReason)
                              .then(() => {
                                alert("Yêu cầu chỉnh sửa lịch thành công!");
                                setShowRequestModal(false);
                                setStatus("not-submitted");
                                setNeedRevision(true);
                              })
                              .catch(() =>
                                alert("Lỗi khi gửi yêu cầu chỉnh sửa")
                              );
                          }}
                        >
                          Gửi yêu cầu
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        <WorkScheduleTable
          month={month}
          year={year}
          setMonth={setMonth}
          setYear={setYear}
          canEdit={false}
          onRejectReasonChange={setRejectReason}
          onNeedRevisionChange={setNeedRevision}
          onStatusChange={setStatus}
          onMonthYearChange={(m, y) => {
            setMonth(m);
            setYear(y);
          }}
        />
      </div>
    </MainLayout>
  );
}

export default WorkScheduleProductionView;
