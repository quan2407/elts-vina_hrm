import React, { useEffect, useState } from "react";
import attendanceService from "../services/attendanceService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";

const AttendanceMonthlyView = () => {
  const [data, setData] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());

  useEffect(() => {
    fetchAttendance();
  }, [month, year]);

  const fetchAttendance = async () => {
    try {
      const response = await attendanceService.getMonthlyAttendance(
        month,
        year
      );
      setData(response.data);
    } catch (error) {
      console.error("Fetch attendance failed:", error);
    }
  };

  const daysInMonth = new Date(year, month, 0).getDate();

  const types = [
    { key: "shift", label: "Công ngày", totalKey: "totalDayShiftHours" },
    { key: "overtime", label: "Tăng ca", totalKey: "totalOvertimeHours" },
    { key: "weekend", label: "Cuối tuần", totalKey: "totalWeekendHours" },
    { key: "holiday", label: "Ngày lễ", totalKey: "totalHolidayHours" },
  ];

  const isNumeric = (val) => !isNaN(parseFloat(val)) && isFinite(val);

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
                <th rowSpan="2">No</th>
                <th rowSpan="2">ID</th>
                <th rowSpan="2">Họ và tên</th>
                <th rowSpan="2">Bộ phận</th>
                <th rowSpan="2">Chức vụ</th>
                <th rowSpan="2">Chuyền</th>
                <th rowSpan="2">Phân loại</th>
                <th colSpan={daysInMonth}>Ngày</th>
                <th rowSpan="2">Tổng công</th>
                <th rowSpan="2">Xác nhận</th>
              </tr>
              <tr>
                {[...Array(daysInMonth)].map((_, i) => (
                  <th key={i + 1}>{i + 1}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {data.map((emp, index) =>
                types.map((type, i) => (
                  <tr key={`${emp.employeeCode}-${type.key}`}>
                    {i === 0 && (
                      <>
                        <td rowSpan="4">{index + 1}</td>
                        <td rowSpan="4">{emp.employeeCode}</td>
                        <td rowSpan="4">{emp.employeeName}</td>
                        <td rowSpan="4">{emp.departmentName}</td>
                        <td rowSpan="4">{emp.positionName}</td>
                        <td rowSpan="4">{emp.lineName || ""}</td>
                      </>
                    )}
                    <td>{type.label}</td>
                    {Array.from({ length: daysInMonth }, (_, d) => {
                      const day = (d + 1).toString();
                      const cell = emp.attendanceByDate[day] || {};
                      return <td key={d}>{cell[type.key] || ""}</td>;
                    })}
                    <td className="highlight-bold">{emp[type.totalKey]}</td>
                    {i === 0 && (
                      <td
                        rowSpan="4"
                        className="highlight-bold"
                      >
                        {emp.totalHours}
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </MainLayout>
  );
};

export default AttendanceMonthlyView;
