import React, { useState } from "react";
import "../styles/WorkScheduleModal.css";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { format, parseISO } from "date-fns";
import { vi } from "date-fns/locale";

function CustomRangeModal({
  isOpen,
  onClose,
  onSubmit,
  departments,
  lines,
  onDepartmentChange,
  month,
  year,
}) {
  const [departmentId, setDepartmentId] = useState("");
  const [lineId, setLineId] = useState("");
  const [startDate, setStartDate] = useState(""); // yyyy-MM-dd
  const [endDate, setEndDate] = useState(""); // yyyy-MM-dd
  const [workType, setWorkType] = useState("normal");
  const [endTime, setEndTime] = useState("17:00");
  const [serverError, setServerError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState({});

  // ====== Range limits ======
  const rawToday = new Date();
  const today = new Date(
    rawToday.getFullYear(),
    rawToday.getMonth(),
    rawToday.getDate()
  );
  const selectedMonthStart = new Date(year, month - 1, 1);
  const selectedMonthEnd = new Date(year, month, 0); // ví dụ: 31/7/2025
  const minDate =
    today.getFullYear() === year && today.getMonth() + 1 === month
      ? today
      : selectedMonthStart;
  const maxDate = selectedMonthEnd;

  // ====== Helpers ======
  const generateEndTimes = () => {
    const times = [];
    for (let h = 8; h <= 22; h++) {
      for (let m = 0; m < 60; m += 30) {
        const timeStr = `${h.toString().padStart(2, "0")}:${m
          .toString()
          .padStart(2, "0")}`;
        if (timeStr > "08:00" && timeStr <= "22:00") times.push(timeStr);
      }
    }
    return times;
  };
  const validEndTimes = generateEndTimes();

  // ====== Submit ======
  const handleSubmit = async () => {
    const newErrors = {};

    if (!departmentId) newErrors.departmentId = "Vui lòng chọn phòng ban";
    if (!startDate) newErrors.startDate = "Vui lòng chọn ngày bắt đầu";
    if (!endDate) newErrors.endDate = "Vui lòng chọn ngày kết thúc";

    if (startDate && endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      if (end < start)
        newErrors.endDate = "Ngày kết thúc không được trước ngày bắt đầu";
      if (
        start.getMonth() + 1 !== month ||
        end.getMonth() + 1 !== month ||
        start.getFullYear() !== year ||
        end.getFullYear() !== year
      ) {
        newErrors.startDate = "Ngày phải nằm trong tháng đã chọn";
        newErrors.endDate = "Ngày phải nằm trong tháng đã chọn";
      }
    }

    if (workType === "custom" && !(endTime > "08:00" && endTime <= "22:00")) {
      newErrors.endTime = "Giờ kết thúc phải sau 08:00 và không quá 22:00";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
    let finalEndTime = endTime;
    if (workType === "normal") finalEndTime = "17:00";
    else if (workType === "overtime") finalEndTime = "20:00";

    setErrors({});
    setServerError(null);
    setSubmitting(true);
    try {
      await onSubmit({
        departmentId: parseInt(departmentId, 10),
        lineId: lineId ? parseInt(lineId, 10) : null,
        startDate,
        endDate,
        startTime: "08:00",
        endTime: finalEndTime,
      });
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err?.message ||
        "Có lỗi xảy ra. Vui lòng thử lại.";
      setServerError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div
      className="work-schedule-modal-overlay"
      style={{ display: isOpen ? "flex" : "none" }}
      aria-hidden={!isOpen}
    >
      <div className="work-schedule-modal-container">
        {serverError && <div className="error-banner">{serverError}</div>}

        <h2 className="work-schedule-modal-title">Dải lịch theo khoảng</h2>

        {/* Phòng ban */}
        <div className="work-schedule-modal-field">
          <label>Phòng ban:</label>
          <select
            value={departmentId}
            onChange={(e) => {
              const newDeptId = e.target.value;
              setDepartmentId(newDeptId);
              setLineId("");
              if (onDepartmentChange && newDeptId) {
                onDepartmentChange(parseInt(newDeptId, 10));
              }
            }}
          >
            <option value="">-- Chọn phòng ban --</option>
            {departments.map((dept) => (
              <option
                key={dept.id}
                value={dept.id}
              >
                {dept.name}
              </option>
            ))}
          </select>
          {errors.departmentId && (
            <div className="error-message">{errors.departmentId}</div>
          )}
        </div>

        {/* Chuyền */}
        <div className="work-schedule-modal-field">
          <label>Chuyền (nếu có):</label>
          <select
            value={lineId}
            onChange={(e) => setLineId(e.target.value)}
          >
            <option value="">-- Không chọn --</option>
            {lines.map((line) => (
              <option
                key={line.id}
                value={line.id}
              >
                {line.name}
              </option>
            ))}
          </select>
        </div>

        {/* Ngày bắt đầu / Ngày kết thúc */}
        <div className="range-schedule-field-row">
          <div className="range-schedule-half-field">
            <label>Ngày bắt đầu:</label>
            <DatePicker
              selected={startDate ? parseISO(startDate) : null}
              onChange={(date) =>
                setStartDate(date ? format(date, "yyyy-MM-dd") : "")
              }
              dateFormat="dd/MM/yyyy"
              locale={vi}
              minDate={minDate}
              maxDate={maxDate}
              placeholderText="dd/mm/yyyy"
            />
            {errors.startDate && (
              <div className="error-message">{errors.startDate}</div>
            )}
          </div>

          <div className="range-schedule-half-field">
            <label>Ngày kết thúc:</label>
            <DatePicker
              selected={endDate ? parseISO(endDate) : null}
              onChange={(date) =>
                setEndDate(date ? format(date, "yyyy-MM-dd") : "")
              }
              dateFormat="dd/MM/yyyy"
              locale={vi}
              minDate={minDate}
              maxDate={maxDate}
              placeholderText="dd/mm/yyyy"
            />
            {errors.endDate && (
              <div className="error-message">{errors.endDate}</div>
            )}
          </div>
        </div>

        {/* Loại ca */}
        <div className="work-schedule-modal-field">
          <label>Loại ca làm việc:</label>
          <select
            value={workType}
            onChange={(e) => setWorkType(e.target.value)}
          >
            <option value="normal">Ca thường (08:00 - 17:00)</option>
            <option value="overtime">Ca tăng ca (08:00 - 20:00)</option>
            <option value="custom">Tự chọn giờ</option>
          </select>
        </div>

        {/* Giờ custom */}
        {workType === "custom" && (
          <>
            <div className="work-schedule-modal-field">
              <label>Giờ bắt đầu:</label>
              <input
                type="text"
                value="08:00"
                disabled
              />
            </div>
            <div className="work-schedule-modal-field">
              <label>Giờ kết thúc:</label>
              <select
                value={endTime}
                onChange={(e) => setEndTime(e.target.value)}
              >
                {validEndTimes.map((time) => (
                  <option
                    key={time}
                    value={time}
                  >
                    {time}
                  </option>
                ))}
              </select>
              {errors.endTime && (
                <div className="error-message">{errors.endTime}</div>
              )}
            </div>
          </>
        )}

        {/* Actions */}
        <div className="work-schedule-modal-actions">
          <button
            className="work-schedule-modal-save-btn"
            onClick={handleSubmit}
            disabled={submitting}
          >
            {submitting ? "Đang xử lý..." : "Dải lịch"}
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

export default CustomRangeModal;
