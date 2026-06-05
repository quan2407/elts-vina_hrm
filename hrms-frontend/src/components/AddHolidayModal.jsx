import React, { useEffect, useState } from "react";
import "../styles/holidayModal.css";
import holidayService from "../services/holidayService ";

function AddHolidayModal({ isOpen, onClose, onSuccess, editId = null }) {
  const [formData, setFormData] = useState({
    name: "",
    startDate: "",
    endDate: "",
    recurring: false,
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const fetchHoliday = async () => {
      setErrors({});
      if (editId) {
        try {
          const res = await holidayService.getHolidayById(editId);
          setFormData(res.data);
        } catch (err) {
          console.error("Không lấy được dữ liệu ngày nghỉ:", err);
        }
      } else {
        setFormData({
          name: "",
          startDate: "",
          endDate: "",
          recurring: false,
        });
      }
    };

    if (isOpen) fetchHoliday();
  }, [editId, isOpen]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const eMap = {};
    if (!formData.name?.trim()) eMap.name = ["Vui lòng nhập tên ngày nghỉ"];
    if (!formData.startDate) eMap.startDate = ["Vui lòng chọn ngày bắt đầu"];
    if (!formData.endDate) eMap.endDate = ["Vui lòng chọn ngày kết thúc"];
    if (
      formData.startDate &&
      formData.endDate &&
      formData.endDate < formData.startDate
    ) {
      eMap.endDate = ["Ngày kết thúc không được nhỏ hơn ngày bắt đầu"];
    }
    if (Object.keys(eMap).length) {
      setErrors(eMap);
      return;
    }

    try {
      if (editId) {
        await holidayService.updateHoliday(editId, formData);
      } else {
        await holidayService.createHoliday(formData);
      }
      onSuccess();
    } catch (err) {
      const data = err?.response?.data;
      const mapped = (data && (data.errors || data)) || {
        global: ["Không thể lưu ngày nghỉ"],
      };

      if (typeof mapped === "string") {
        setErrors({ global: [mapped] });
      } else if (data?.message && !mapped.global) {
        setErrors({ ...mapped, global: [data.message] });
      } else {
        setErrors(mapped);
      }
    }
  };

  if (!isOpen) return null;

  return (
    <div className="holiday-modal-overlay">
      <div className="holiday-modal">
        {errors.global && (
          <div className="error-message">
            {[].concat(errors.global).join(", ")}
          </div>
        )}
        <h2>{editId ? "Cập nhật ngày nghỉ" : "Thêm ngày nghỉ"}</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Tên ngày nghỉ:
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
            />
          </label>
          {errors.name && (
            <div className="error-message">{errors.name.join(", ")}</div>
          )}
          <label>
            Từ ngày:
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
            />
          </label>
          {errors.startDate && (
            <div className="error-message">{errors.startDate.join(", ")}</div>
          )}
          <label>
            Đến ngày:
            <input
              type="date"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
            />
            {errors.endDate && (
              <div className="error-message">{errors.endDate.join(", ")}</div>
            )}
          </label>

          <label className="checkbox-label">
            <input
              type="checkbox"
              name="recurring"
              checked={formData.recurring}
              onChange={handleChange}
            />
            Lặp lại hàng năm
          </label>
          {errors.recurring && (
            <div className="error-message">{errors.recurring.join(", ")}</div>
          )}
          <div className="modal-actions">
            <button
              type="submit"
              className="confirm-btn"
            >
              {editId ? "Cập nhật" : "Thêm"}
            </button>
            <button
              type="button"
              className="cancel-btn"
              onClick={onClose}
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddHolidayModal;
