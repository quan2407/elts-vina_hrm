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

  useEffect(() => {
    const fetchHoliday = async () => {
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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editId) {
        await holidayService.updateHoliday(editId, formData);
      } else {
        await holidayService.createHoliday(formData);
      }
      onSuccess();
    } catch (err) {
      alert("Lỗi khi lưu ngày nghỉ!");
      console.error(err);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="holiday-modal-overlay">
      <div className="holiday-modal">
        <h2>{editId ? "Cập nhật ngày nghỉ" : "Thêm ngày nghỉ"}</h2>
        <form onSubmit={handleSubmit}>
          <label>
            Tên ngày nghỉ:
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Từ ngày:
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            Đến ngày:
            <input
              type="date"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              required
            />
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
