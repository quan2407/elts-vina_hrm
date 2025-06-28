import React from "react";
import "../styles/WorkScheduleModal.css";

function WorkScheduleModal({ isOpen, onClose, onSave, data }) {
  if (!isOpen) return null;

  const { departmentName, lineName, date, startTime, endTime, onChange } = data;

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

  return (
    <div className="work-schedule-modal-overlay">
      <div className="work-schedule-modal-container">
        <h2 className="work-schedule-modal-title">Thêm lịch làm việc</h2>

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
          <label>Giờ bắt đầu:</label>
          <select
            value={startTime}
            onChange={(e) => onChange("startTime", e.target.value)}
          >
            {timeOptions.map((t) => (
              <option
                key={t}
                value={t}
              >
                {t}
              </option>
            ))}
          </select>
        </div>

        <div className="work-schedule-modal-field">
          <label>Giờ kết thúc:</label>
          <select
            value={endTime}
            onChange={(e) => onChange("endTime", e.target.value)}
          >
            {timeOptions.map((t) => (
              <option
                key={t}
                value={t}
              >
                {t}
              </option>
            ))}
          </select>
        </div>

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
        </div>
      </div>
    </div>
  );
}

export default WorkScheduleModal;
