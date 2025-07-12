import React from "react";
import "../styles/AttendanceModal.css";

function AttendanceModal({
  isOpen,
  onClose,
  onSave,
  data,
  errorMessages = {},
}) {
  const {
    employeeName,
    date,
    checkIn,
    checkOut,
    onChange,
    attendanceRecordId,
    onDelete,
  } = data;

  const formattedDate = new Date(date).toLocaleDateString("vi-VN");

  const generateTimeOptions = () => {
    const options = [];
    for (let h = 0; h < 24; h++) {
      for (let m = 0; m < 60; m += 15) {
        const hour = h.toString().padStart(2, "0");
        const minute = m.toString().padStart(2, "0");
        options.push(`${hour}:${minute}`);
      }
    }
    return options;
  };

  const timeOptions = generateTimeOptions();

  return (
    <div
      className="attendance-modal-overlay"
      style={{ display: isOpen ? "flex" : "none" }}
    >
      <div className="attendance-modal-container">
        <h2 className="attendance-modal-title">
          {attendanceRecordId ? "Cập nhật giờ vào/ra" : "Thêm giờ vào/ra"}
        </h2>

        <div className="attendance-modal-field">
          <label>Nhân viên:</label>
          <span>{employeeName}</span>
        </div>

        <div className="attendance-modal-field">
          <label>Ngày:</label>
          <span>{formattedDate}</span>
        </div>

        <div className="attendance-modal-field">
          <label>Giờ vào:</label>
          <select
            value={checkIn}
            onChange={(e) => onChange("checkIn", e.target.value)}
          >
            <option value="">-- Chọn --</option>
            {timeOptions.map((t) => (
              <option
                key={t}
                value={t}
              >
                {t}
              </option>
            ))}
          </select>
          {errorMessages.checkIn && (
            <div className="error-message">{errorMessages.checkIn[0]}</div>
          )}
        </div>

        <div className="attendance-modal-field">
          <label>Giờ ra:</label>
          <select
            value={checkOut}
            onChange={(e) => onChange("checkOut", e.target.value)}
          >
            <option value="">-- Chọn --</option>
            {timeOptions.map((t) => (
              <option
                key={t}
                value={t}
              >
                {t}
              </option>
            ))}
          </select>
          {errorMessages.checkOut && (
            <div className="error-message">{errorMessages.checkOut[0]}</div>
          )}
        </div>

        <div className="attendance-modal-actions">
          <button
            className="attendance-modal-save-btn"
            onClick={onSave}
          >
            Lưu
          </button>
          <button
            className="attendance-modal-cancel-btn"
            onClick={onClose}
          >
            Hủy
          </button>
          {attendanceRecordId && onDelete && (
            <button
              className="attendance-modal-delete-btn"
              onClick={() => {
                if (
                  window.confirm("Bạn có chắc chắn muốn xóa giờ vào/ra này?")
                ) {
                  onDelete(attendanceRecordId);
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

export default AttendanceModal;
