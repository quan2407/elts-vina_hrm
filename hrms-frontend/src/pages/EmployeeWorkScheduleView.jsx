import React, { useEffect, useState } from "react";
import workScheduleService from "../services/workScheduleService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";

const EmployeeWorkScheduleView = () => {
  const [workSchedule, setWorkSchedule] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());

  const fetchSchedule = async () => {
    try {
      const res = await workScheduleService.getWorkScheduleForCurrentEmployee(
        month,
        year
      );
      setWorkSchedule(res.data);
    } catch (err) {
      console.error("Lỗi khi tải lịch làm việc:", err);
    }
  };

  useEffect(() => {
    if (month && year) fetchSchedule();
  }, [month, year]);

  return (
    <MainLayout>
      <div className="attendance-container">
        <div className="attendance-controls">
          <select
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
          >
            {[...Array(12)].map((_, i) => (
              <option
                key={i + 1}
                value={i + 1}
              >
                Tháng {i + 1}
              </option>
            ))}
          </select>
          <select
            value={year}
            onChange={(e) => setYear(Number(e.target.value))}
          >
            {[2024, 2025, 2026].map((y) => (
              <option
                key={y}
                value={y}
              >
                Năm {y}
              </option>
            ))}
          </select>
        </div>

        <div className="attendance-table-wrapper">
          <table className="attendance-table">
            <thead>
              <tr>
                <th>Ngày</th>
                <th>Thời gian làm</th>
                <th>Tăng ca</th>
                <th>Tổ</th>
                <th>Phòng ban</th>
              </tr>
            </thead>
            <tbody>
              {workSchedule.length > 0 ? (
                workSchedule.map((item, index) => (
                  <tr key={index}>
                    <td>{item.date}</td>
                    <td>
                      {item.startTime && item.endTime
                        ? `${item.startTime} - ${item.endTime}`
                        : "--"}
                    </td>
                    <td>{item.overtime ? "Có" : "Không"}</td>
                    <td>{item.lineName || "Chưa phân tổ"}</td>
                    <td>{item.departmentName}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="5"
                    className="text-center"
                  >
                    Không có lịch làm việc
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </MainLayout>
  );
};

export default EmployeeWorkScheduleView;
