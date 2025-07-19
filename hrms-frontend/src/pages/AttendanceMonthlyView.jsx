import React, { useEffect, useState } from "react";
import attendanceService from "../services/attendanceService";
import MainLayout from "../components/MainLayout";
import AttendanceModal from "../components/AttendanceModal";
import LeaveCodeModal from "../components/LeaveCodeModal";

import "../styles/AttendanceMonthlyView.css";
import { Pencil } from "lucide-react";

const AttendanceMonthlyView = () => {
  const [data, setData] = useState([]);
  const [availableMonths, setAvailableMonths] = useState([]);
  const [month, setMonth] = useState(null);
  const [year, setYear] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [selectedEmployee, setSelectedEmployee] = useState("");
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [leaveModalOpen, setLeaveModalOpen] = useState(false);
  const [leaveRecordId, setLeaveRecordId] = useState(null);
  const [leaveDate, setLeaveDate] = useState(null);
  const [leaveCellMeta, setLeaveCellMeta] = useState({});
  const [validationErrors, setValidationErrors] = useState({});
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const today = new Date();

  const isBeforeYesterday = (date) => {
    const d1 = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const d2 = new Date(
      today.getFullYear(),
      today.getMonth(),
      today.getDate() - 1
    );
    return d1 <= d2;
  };

  const handleOpenLeaveModal = (recordId, dateStr, cellMeta) => {
    setLeaveRecordId(recordId);
    setLeaveDate(dateStr);
    setLeaveCellMeta(cellMeta);
    setLeaveModalOpen(true);
  };
  const getPageNumbers = () => {
    const delta = 2;
    const range = [];
    for (
      let i = Math.max(0, page - delta);
      i <= Math.min(totalPages - 1, page + delta);
      i++
    ) {
      range.push(i);
    }
    return range;
  };

  const handleSaveLeaveCode = async (id, code, targetField) => {
    console.log("Leave update:", id, code, targetField);
    try {
      await attendanceService.updateLeaveCode(id, {
        leaveCode: code,
        targetField: targetField,
      });
      await fetchAttendance();
      setLeaveModalOpen(false);
    } catch (error) {
      console.error("Cập nhật nghỉ phép thất bại:", error);
      alert("Cập nhật nghỉ phép thất bại!");
    }
  };

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
  }, [month, year, page]);

  const fetchAttendance = async () => {
    try {
      const response = await attendanceService.getMonthlyAttendance(
        month,
        year,
        page,
        size
      );
      setData(response.data.content);
      setTotalPages(response.data.totalPages);
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

  const handleOpenModal = (emp, day, record) => {
    setSelectedDate(
      `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(
        2,
        "0"
      )}`
    );
    setSelectedEmployee(emp.employeeName);
    setSelectedRecord(record?.attendanceRecordId || null);
    setCheckIn(record?.checkIn || "");
    setCheckOut(record?.checkOut || "");
    setModalOpen(true);
  };

  const handleSave = async () => {
    try {
      await attendanceService.updateCheckInOut(selectedRecord, {
        checkIn,
        checkOut,
      });
      await fetchAttendance();
      setModalOpen(false);
      setValidationErrors({});
    } catch (error) {
      console.error("Cập nhật giờ vào/ra thất bại:", error);
      if (
        error.response &&
        error.response.status === 400 &&
        typeof error.response.data === "object"
      ) {
        setValidationErrors(error.response.data);
      } else {
        alert("Cập nhật thất bại!");
      }
    }
  };

  return (
    <MainLayout>
      <div className="attendance-container">
        <div className="attendance-controls">
          <div className="attendance-filters">
            <select
              value={month || ""}
              onChange={(e) => {
                setMonth(Number(e.target.value));
                setPage(0);
              }}
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
              onChange={(e) => {
                setYear(Number(e.target.value));
                setPage(0);
              }}
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

          <div className="leave-code-popover-wrapper">
            <button
              className="leave-code-toggle-btn"
              onClick={() =>
                document
                  .getElementById("leave-code-popover")
                  .classList.toggle("show")
              }
            >
              🛈 Ghi chú mã nghỉ
            </button>
            <div
              id="leave-code-popover"
              className="leave-code-popover"
            >
              <div className="leave-code-columns">
                <ul>
                  <li>
                    <strong>NL</strong>: Nghỉ nguyên lương
                  </li>
                  <li>
                    <strong>VPHĐ</strong>: Nghỉ vi phạm HĐ
                  </li>
                  <li>
                    <strong>VR</strong>: Nghỉ việc riêng
                  </li>
                </ul>
                <ul>
                  <li>
                    <strong>KL</strong>: Nghỉ không lương
                  </li>
                  <li>
                    <strong>KH</strong>: Kết hôn
                  </li>
                  <li>
                    <strong>CKH</strong>: Con kết hôn
                  </li>
                </ul>
                <ul>
                  <li>
                    <strong>NT</strong>: Nghỉ tang
                  </li>
                  <li>
                    <strong>P</strong>: Nghỉ phép
                  </li>
                  <li>
                    <strong>P_4</strong>: Nghỉ phép nửa ngày
                  </li>
                </ul>
                <ul>
                  <li>
                    <strong>NTS</strong>: Nghỉ thai sản
                  </li>
                  <li>
                    <strong>NDB</strong>: Dịch bệnh hưởng công nguyên ngày
                  </li>
                  <li>
                    <strong>NDB_4</strong>: Dịch bệnh hưởng công 4h
                  </li>
                </ul>
                <ul>
                  <li>
                    <strong>NDB_1_5</strong>: Dịch bệnh hưởng công 1.5h
                  </li>
                </ul>
              </div>
            </div>
          </div>
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
                {[...Array(daysInMonth)].map((_, i) => {
                  return <th key={i + 1}>{i + 1}</th>;
                })}
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
                      const dayDate = new Date(year, month - 1, d + 1);
                      const cell = emp.attendanceByDate[day] || {};

                      if (type.key === "checkInOut") {
                        if (cell.hasScheduleDetail) {
                          return (
                            <td key={d}>
                              {cell.checkIn || cell.checkOut ? (
                                <span
                                  className="attendance-edit-icon"
                                  onClick={() =>
                                    handleOpenModal(emp, day, cell)
                                  }
                                >
                                  {`${cell.checkIn || "--"} - ${
                                    cell.checkOut || "--"
                                  }`}
                                </span>
                              ) : (
                                <span className="attendance-empty-cell">
                                  --
                                </span>
                              )}

                              <div className="attendance-buttons">
                                {isBeforeYesterday(dayDate) && (
                                  <>
                                    <button
                                      className="attendance-action-btn edit"
                                      onClick={() =>
                                        handleOpenModal(emp, day, cell)
                                      }
                                      title="Chỉnh sửa giờ vào/ra"
                                    >
                                      <Pencil size={14} />
                                    </button>
                                    <button
                                      className="attendance-action-btn leave"
                                      onClick={() =>
                                        handleOpenLeaveModal(
                                          cell.attendanceRecordId,
                                          `${year}-${String(month).padStart(
                                            2,
                                            "0"
                                          )}-${String(day).padStart(2, "0")}`,
                                          {
                                            holidayFlag: cell.holidayFlag,
                                            weekendFlag: cell.weekendFlag,
                                            hasOt:
                                              parseFloat(cell.overtime) > 0,
                                          }
                                        )
                                      }
                                      title="Chọn loại nghỉ phép"
                                    >
                                      🛏️
                                    </button>
                                  </>
                                )}
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
        <div className="attendance-pagination-container">
          <button
            className="attendance-pagination-btn"
            onClick={() => setPage(0)}
            disabled={page === 0}
          >
            «
          </button>
          <button
            className="attendance-pagination-btn"
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
          >
            ‹
          </button>

          {getPageNumbers().map((p) => (
            <button
              key={p}
              onClick={() => setPage(p)}
              className={`attendance-pagination-btn ${
                p === page ? "attendance-pagination-active" : ""
              }`}
            >
              {p + 1}
            </button>
          ))}

          <button
            className="attendance-pagination-btn"
            onClick={() => setPage(page + 1)}
            disabled={page === totalPages - 1}
          >
            ›
          </button>
          <button
            className="attendance-pagination-btn"
            onClick={() => setPage(totalPages - 1)}
            disabled={page === totalPages - 1}
          >
            »
          </button>
        </div>
      </div>

      <AttendanceModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSave={handleSave}
        errorMessages={validationErrors}
        data={{
          employeeName: selectedEmployee,
          date: selectedDate,
          checkIn,
          checkOut,
          onChange: (field, value) => {
            if (field === "checkIn") setCheckIn(value);
            if (field === "checkOut") setCheckOut(value);
          },
          attendanceRecordId: selectedRecord,
          onDelete: async (id) => {
            try {
              await attendanceService.updateCheckInOut(id, {
                checkIn: null,
                checkOut: null,
              });
              await fetchAttendance();
              setModalOpen(false);
            } catch (error) {
              console.error("Xóa giờ vào/ra thất bại:", error);
              alert("Xóa thất bại!");
            }
          },
        }}
      />
      <LeaveCodeModal
        isOpen={leaveModalOpen}
        onClose={() => setLeaveModalOpen(false)}
        onSave={handleSaveLeaveCode}
        recordId={leaveRecordId}
        dateInfo={leaveDate}
        dateMeta={leaveCellMeta}
      />
    </MainLayout>
  );
};

export default AttendanceMonthlyView;
