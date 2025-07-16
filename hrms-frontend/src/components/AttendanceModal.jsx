import React from "react";
import "../styles/AttendanceModal.css";
import TimePicker from "react-time-picker";
import "react-time-picker/dist/TimePicker.css";

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
          <TimePicker
            onChange={(value) => onChange("checkIn", value)}
            value={checkIn}
            format="HH:mm"
            disableClock={true}
            clearIcon={null}
            hourPlaceholder="HH"
            minutePlaceholder="mm"
          />
          {errorMessages.checkIn && (
            <div className="error-message">{errorMessages.checkIn[0]}</div>
          )}
          {errorMessages.checkInOut && (
            <div className="error-message">{errorMessages.checkInOut[0]}</div>
          )}
        </div>

        <div className="attendance-modal-field">
          <label>Giờ ra:</label>
          <TimePicker
            onChange={(value) => onChange("checkOut", value)}
            value={checkOut}
            format="HH:mm"
            disableClock={true}
            clearIcon={null}
            hourPlaceholder="HH"
            minutePlaceholder="mm"
          />
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
