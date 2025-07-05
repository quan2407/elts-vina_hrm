import React from "react";
import "../styles/WorkScheduleModal.css";

function WorkScheduleModal({
  isOpen,
  onClose,
  onSave,
  data,
  errorMessages = {},
}) {
  const {
    departmentName,
    lineName,
    date,
    startTime,
    endTime,
    onChange,
    workType,
    id,
    onDelete,
  } = data;

  const formattedDate = new Date(date).toLocaleDateString("vi-VN");

  const generateTimeOptions = () => {
    const options = [];
    for (let h = 0; h < 24; h++) {
      for (let m = 0; m < 60; m += 30) {
        const hour = h.toString().padStart(2, "0");
        const minute = m.toString().padStart(2, "0");
        options.push(`${hour}:${minute}`);
      }
    }
    return options;
  };

  const timeOptions = generateTimeOptions();
  const validEndTimes = timeOptions.filter((t) => t >= "08:30" && t <= "22:00");

  return (
    <div
      className="work-schedule-modal-overlay"
      style={{ display: isOpen ? "flex" : "none" }}
    >
      <div className="work-schedule-modal-container">
        <h2 className="work-schedule-modal-title">
          {id ? "Cập nhật lịch làm việc" : "Thêm lịch làm việc"}
        </h2>

        <div className="work-schedule-modal-field">
          <label>Phòng ban:</label>
          <span>{departmentName}</span>
        </div>

        <div className="work-schedule-modal-field">
          <label>Chuyền:</label>
          <span>{lineName}</span>
        </div>

        <div className="work-schedule-modal-field">
          <label>Ngày làm việc:</label>
          <span>{formattedDate}</span>
        </div>

        <div className="work-schedule-modal-field">
          <label>Loại ca làm việc:</label>
          <select
            value={workType}
            onChange={(e) => onChange("workType", e.target.value)}
          >
            <option value="normal">Ca thường (08:00 - 17:00)</option>
            <option value="overtime">Ca tăng ca (08:00 - 20:00)</option>
            <option value="custom">Tự chọn giờ</option>
          </select>
        </div>

        {workType === "custom" && (
          <>
            <div className="work-schedule-modal-field">
              <label>Giờ bắt đầu:</label>
              <select
                value={startTime}
                disabled
              >
                <option value="08:00">08:00</option>
              </select>
            </div>

            <div className="work-schedule-modal-field">
              <label>Giờ kết thúc:</label>
              <select
                value={endTime}
                onChange={(e) => onChange("endTime", e.target.value)}
              >
                {validEndTimes.map((t) => (
                  <option
                    key={t}
                    value={t}
                  >
                    {t}
                  </option>
                ))}
              </select>
              {errorMessages.endTime && (
                <div className="error-message">{errorMessages.endTime[0]}</div>
              )}
            </div>
          </>
        )}

        <div className="work-schedule-modal-actions">
          <button
            className="work-schedule-modal-save-btn"
            onClick={onSave}
          >
            Lưu
          </button>

          <button
            className="work-schedule-modal-cancel-btn"
            onClick={onClose}
          >
            Hủy
          </button>

          {id && onDelete && (
            <button
              className="work-schedule-modal-delete-btn"
              onClick={() => {
                if (
                  window.confirm("Bạn có chắc chắn muốn xóa lịch làm việc này?")
                ) {
                  onDelete(id);
                }
              }}
            >
              Xóa
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default WorkScheduleModal;
