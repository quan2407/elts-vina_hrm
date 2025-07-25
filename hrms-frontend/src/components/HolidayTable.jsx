import React, { useEffect, useState, forwardRef } from "react";
import "../styles/HolidayTable.css";
import holidayService from "../services/holidayService ";

const HolidayTable = forwardRef(({ searchTerm, onEdit }, ref) => {
  const [holidays, setHolidays] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await holidayService.getAllHolidays();
      setHolidays(response.data);
    } catch (error) {
      console.error("Lỗi khi load ngày nghỉ:", error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ngày nghỉ này?")) {
      try {
        await holidayService.deleteHoliday(id);
        fetchData();
      } catch (err) {
        console.error("Lỗi khi xóa ngày nghỉ:", err);
      }
    }
  };

  const filteredHolidays = holidays.filter((holiday) =>
    holiday.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const formatDate = (isoDate, isRecurring) => {
    if (!isoDate) return "";
    const date = new Date(isoDate);
    return isRecurring
      ? date.toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit" })
      : date.toLocaleDateString("vi-VN");
  };

  return (
    <div className="holiday-table-wrapper">
      <div className="holiday-table">
        <div className="holiday-table-header">
          <div className="holiday-header-cell">ID</div>
          <div className="holiday-header-cell">Tên ngày nghỉ</div>
          <div className="holiday-header-cell">Từ ngày</div>
          <div className="holiday-header-cell">Đến ngày</div>
          <div className="holiday-header-cell">Lặp lại</div>
          <div className="holiday-header-cell">Hành động</div>
        </div>

        {filteredHolidays.map((holiday) => (
          <div
            key={holiday.id}
            className="holiday-table-row"
          >
            <div className="holiday-table-cell">{holiday.id}</div>
            <div className="holiday-table-cell">{holiday.name}</div>
            <div className="holiday-table-cell">
              {formatDate(holiday.startDate, holiday.recurring)}
            </div>
            <div className="holiday-table-cell">
              {formatDate(holiday.endDate, holiday.recurring)}
            </div>
            <div className="holiday-table-cell">
              {holiday.recurring ? "Có" : "Không"}
            </div>
            <div className="holiday-table-cell">
              <button onClick={() => onEdit(holiday.id)}>Sửa</button>
              <button onClick={() => handleDelete(holiday.id)}>Xóa</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
});

export default HolidayTable;
