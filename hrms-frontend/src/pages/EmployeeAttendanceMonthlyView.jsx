import React, { useEffect, useState } from "react";
import attendanceService from "../services/attendanceService";
import MainLayout from "../components/MainLayout";

import "../styles/AttendanceMonthlyView.css";
import { Pencil } from "lucide-react";

const EmployeeAttendanceMonthlyView = () => {
  const [data, setData] = useState([]);
  const [availableMonths, setAvailableMonths] = useState([]);
  const [month, setMonth] = useState(null);
  const [year, setYear] = useState(null);

  useEffect(() => {
    const fetchAvailableMonths = async () => {
      try {
        const res = await attendanceService.getAvailableMonths();
        setAvailableMonths(res.data);
        if (res.data.length > 0) {
          setMonth(res.data[0].month);
          setYear(res.data[0].year);
        }
      } catch (error) {
        console.error("Lỗi khi tải danh sách tháng/năm:", error);
      }
    };

    fetchAvailableMonths();
  }, []);

  useEffect(() => {
    if (month && year) {
      fetchAttendance();
    }
  }, [month, year]);

  const fetchAttendance = async () => {
    try {
      const response = await attendanceService.getEmployeeMonthlyAttendanceById(
        month,
        year
      );
      setData(response.data);
    } catch (error) {
      console.error("Fetch attendance failed:", error);
    }
  };

  const daysInMonth = month && year ? new Date(year, month, 0).getDate() : 0;

  const types = [
    { key: "shift", label: "Công ngày", totalKey: "totalDayShiftHours" },
    { key: "overtime", label: "Tăng ca", totalKey: "totalOvertimeHours" },
    { key: "weekend", label: "Cuối tuần", totalKey: "totalWeekendHours" },
    { key: "holiday", label: "Ngày lễ", totalKey: "totalHolidayHours" },
    { key: "checkInOut", label: "Giờ vào/ra", totalKey: null },
  ];

  return (
    <MainLayout>
      <div className="attendance-container">
        <h1 className="attendance-title">Xem bảng công theo tháng</h1>

        <div className="attendance-controls">
          <select
            value={month || ""}
            onChange={(e) => setMonth(Number(e.target.value))}
          >
            <option
              value=""
              disabled
            >
              -- Chọn tháng --
            </option>
            {Array.from(new Set(availableMonths.map((m) => m.month))).map(
              (m) => (
                <option
                  key={m}
                  value={m}
                >
                  Tháng {m < 10 ? `0${m}` : m}
                </option>
              )
            )}
          </select>

          <select
            value={year || ""}
            onChange={(e) => setYear(Number(e.target.value))}
          >
            <option
              value=""
              disabled
            >
              -- Chọn năm --
            </option>
            {Array.from(new Set(availableMonths.map((m) => m.year))).map(
              (y) => (
                <option
                  key={y}
                  value={y}
                >
                  Năm {y}
                </option>
              )
            )}
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
                        <td rowSpan="5">{index + 1}</td>
                        <td rowSpan="5">{emp.employeeCode}</td>
                        <td rowSpan="5">{emp.employeeName}</td>
                        <td rowSpan="5">{emp.departmentName}</td>
                        <td rowSpan="5">{emp.positionName}</td>
                        <td rowSpan="5">{emp.lineName || ""}</td>
                      </>
                    )}
                    <td>{type.label}</td>
                    {Array.from({ length: daysInMonth }, (_, d) => {
                      const day = (d + 1).toString();
                      const cell = emp.attendanceByDate[day] || {};

                      if (type.key === "checkInOut") {
                        if (cell.hasScheduleDetail) {
                          const formattedDate = `${year}-${String(
                            month
                          ).padStart(2, "0")}-${String(d + 1).padStart(
                            2,
                            "0"
                          )}`;
                          return (
                            <td key={d}>
                              <div
                                style={{
                                  display: "flex",
                                  flexDirection: "column",
                                  alignItems: "center",
                                }}
                              >
                                {cell.checkIn || cell.checkOut ? (
                                  <span className="attendance-edit-icon">
                                    {`${cell.checkIn || "--"} - ${
                                      cell.checkOut || "--"
                                    }`}
                                  </span>
                                ) : (
                                  <span className="attendance-empty-cell">
                                    --
                                  </span>
                                )}
                                <button
                                  style={{
                                    marginTop: "4px",
                                    fontSize: "10px",
                                    padding: "2px 6px",
                                    backgroundColor: "#1976d2",
                                    color: "white",
                                    border: "none",
                                    borderRadius: "4px",
                                    cursor: "pointer",
                                  }}
                                  onClick={() => {
                                    window.location.href = `/create-application?type=makeup&date=${formattedDate}`;
                                  }}
                                >
                                  Tạo đơn
                                </button>
                              </div>
                            </td>
                          );
                        } else {
                          return <td key={d}></td>;
                        }
                      } else {
                        const value = cell[type.key] || "";
                        return <td key={d}>{value}</td>;
                      }
                    })}
                    <td className="highlight-bold">{emp[type.totalKey]}</td>
                    {i === 0 && (
                      <td
                        rowSpan="5"
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

export default EmployeeAttendanceMonthlyView;
