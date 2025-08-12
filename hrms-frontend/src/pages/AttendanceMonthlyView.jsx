import React, { useEffect, useState } from "react";
import attendanceService from "../services/attendanceService";
import MainLayout from "../components/MainLayout";
import AttendanceModal from "../components/AttendanceModal";
import LeaveCodeModal from "../components/LeaveCodeModal";
import { useLocation } from "react-router-dom";
import salaryService from "../services/salaryService";
import "../styles/AttendanceMonthlyView.css";
import { Pencil } from "lucide-react";
import DatePicker from "react-datepicker";
import { parse } from "date-fns";
import "react-datepicker/dist/react-datepicker.css";
import departmentService from "../services/departmentService";
import positionService from "../services/positionService";
import { getAllLines } from "../services/linesService"; // bạn đã có
const AttendanceMonthlyView = ({ readOnly = false }) => {
  const location = useLocation();
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
  const [searchTerm, setSearchTerm] = useState("");
  const [importModalOpen, setImportModalOpen] = useState(false);
  const [file, setFile] = useState(null);

  const today = new Date();
  const params = new URLSearchParams(location.search);
  const empId = params.get("focusEmployee");
  const focusDate = params.get("focusDate"); // yyyy-MM-dd
  const targetCellId =
    empId && focusDate ? `attendance-cell-${empId}-${focusDate}` : null;
  console.log("🔍 targetCellId:", targetCellId);
  const handleGenerateSalary = async () => {
    if (!month || !year) return;

    try {
      await salaryService.regenerateMonthlySalaries(month, year);
      alert(`Tạo bảng lương cho ${month}/${year} thành công.`);
    } catch (error) {
      console.error("Tạo bảng lương thất bại:", error);
      alert("Tạo bảng lương thất bại!");
    }
  };
  const handleImportAttendance = async (file, date) => {
    try {
      await attendanceService.importAttendanceFromExcel(file, date);
      alert("Import thành công.");
      setImportModalOpen(false);
      fetchAttendance();
    } catch (error) {
      console.error("Import lỗi:", error);
      alert("Import thất bại!");
    }
  };

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
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);

  const [departmentId, setDepartmentId] = useState(null);
  const [positionId, setPositionId] = useState(null);
  const [lineId, setLineId] = useState(null);
  useEffect(() => {
    (async () => {
      try {
        const depRes = await departmentService.getAllDepartments();
        setDepartments(depRes.data || []);

        const posRes = await positionService.getAll();
        setPositions(posRes.data || []);

        const lineRes = await getAllLines();
        setLines(lineRes || []);
      } catch (e) {
        console.error("Load danh mục lỗi:", e);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        if (departmentId) {
          const [posByDep, lineByDep] = await Promise.all([
            departmentService.getPositionsByDepartment(departmentId),
            departmentService.getLinesByDepartment(departmentId),
          ]);
          setPositions(posByDep.data || []);
          setLines(lineByDep.data || []);
        } else {
          const posRes = await positionService.getAll();
          setPositions(posRes.data || []);
          const lineRes = await getAllLines();
          setLines(lineRes || []);
        }

        setPositionId(null);
        setLineId(null);
      } catch (e) {
        console.error("Load positions/lines theo department lỗi:", e);
      }
    })();
  }, [departmentId]);

  useEffect(() => {
    const fetchAvailableMonths = async () => {
      try {
        const res = await attendanceService.getAvailableMonths();
        setAvailableMonths(res.data);

        // ✅ Nếu có focusDate trên URL → parse ra year & month
        const focusDate = params.get("focusDate");
        let defaultMonth = res.data[0].month;
        let defaultYear = res.data[0].year;

        if (focusDate) {
          const [y, m] = focusDate.split("-").map(Number);
          const exists = res.data.some((d) => d.month === m && d.year === y);
          if (exists) {
            defaultMonth = m;
            defaultYear = y;
          }
        }

        setMonth(defaultMonth);
        setYear(defaultYear);
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
      console.log("🎯 Calling API with:", {
        month,
        year,
        page,
        size,
        searchTerm,
      });
      const response = await attendanceService.getMonthlyAttendance(
        month,
        year,
        page,
        size,
        searchTerm,
        departmentId,
        positionId,
        lineId
      );
      console.log("✅ RESPONSE DATA:", response.data);
      setData(response.data.content);
      console.log(
        "👤 Employees:",
        response.data.content.map((e) => e.employeeId)
      );
      setTotalPages(response.data.totalPages);
      console.log("📦 response.data =", response.data);
    } catch (error) {
      console.error("Fetch attendance failed:", error);
    }
  };
  // useEffect(() => {
  //   const debounceTimeout = setTimeout(() => {
  //     if (month && year) {
  //       fetchAttendance();
  //     }
  //   }, 100);

  //   return () => clearTimeout(debounceTimeout);
  // }, [searchTerm]);

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
  useEffect(() => {
    if (!targetCellId) return;

    const tryScroll = () => {
      const el = document.getElementById(targetCellId);
      if (el) {
        console.log("🎯 Found element:", targetCellId);
        el.scrollIntoView({ behavior: "smooth", block: "center" });
        el.classList.add("highlight-scroll-target");
        setTimeout(() => el.classList.remove("highlight-scroll-target"), 3000);
      } else {
        console.log("❌ Element not found yet, retrying...");
        setTimeout(tryScroll, 200);
      }
    };

    // Delay đầu tiên sau khi render toàn bộ bảng
    setTimeout(tryScroll, 500);
  }, [data, targetCellId]);

  return (
    <MainLayout>
      <div className="attendance-container">
        <div className="attendance-controls">
          {/* Các nút hành động */}
          <div className="attendance-actions">
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
                      <strong>P_2</strong>: Nghỉ phép nửa ngày
                    </li>
                  </ul>
                  <ul>
                    <li>
                      <strong>NTS</strong>: Nghỉ thai sản
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <button
              className="generate-salary-btn"
              onClick={handleGenerateSalary}
            >
              Tạo bảng lương
            </button>
            <button
              className="generate-salary-btn"
              style={{ backgroundColor: "#2563eb", marginLeft: "8px" }}
              onClick={async () => {
                if (!month || !year) {
                  alert("Vui lòng chọn tháng và năm trước khi xuất báo cáo.");
                  return;
                }

                try {
                  const response =
                    await attendanceService.exportAttendanceToExcel(
                      month,
                      year
                    );
                  const blob = new Blob([response.data], {
                    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                  });

                  const url = window.URL.createObjectURL(blob);
                  const link = document.createElement("a");
                  link.href = url;
                  link.setAttribute(
                    "download",
                    `baocao_chamcong_thang_${month
                      .toString()
                      .padStart(2, "0")}_${year}.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  link.remove();
                } catch (error) {
                  console.error("Xuất Excel thất bại:", error);
                  alert("Không thể xuất báo cáo chấm công.");
                }
              }}
            >
              Xuất Excel
            </button>
            <button
              className="generate-salary-btn"
              style={{ backgroundColor: "#22c55e", marginLeft: "8px" }}
              onClick={() => setImportModalOpen(true)}
            >
              Import Excel
            </button>
          </div>

          {/* Chuyển bộ lọc tháng/năm xuống dưới */}
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

          {/* Di chuyển các bộ lọc còn lại xuống dưới */}
          <div className="attendance-filters">
            <input
              type="text"
              className="attendance-search-input"
              placeholder="Tìm mã hoặc tên nhân viên..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value); // Chỉ cập nhật giá trị searchTerm khi gõ
              }}
            />

            {/* Bộ lọc Department */}
            <select
              value={departmentId || ""}
              onChange={(e) => setDepartmentId(e.target.value)}
            >
              <option value="">-- Chọn Bộ Phận --</option>
              {departments.map((department) => (
                <option
                  key={department.id}
                  value={department.id}
                >
                  {department.name}
                </option>
              ))}
            </select>

            {/* Bộ lọc Line */}
            <select
              value={lineId || ""}
              onChange={(e) => setLineId(e.target.value)}
            >
              <option value="">-- Chọn Chuyền --</option>
              {lines.map((line) => (
                <option
                  key={line.id}
                  value={line.id}
                >
                  {line.name}
                </option>
              ))}
            </select>

            {/* Bộ lọc Position */}
            <select
              value={positionId || ""}
              onChange={(e) => setPositionId(e.target.value)}
            >
              <option value="">-- Chọn Chức Vụ --</option>
              {positions.map((position) => (
                <option
                  key={position.id}
                  value={position.id}
                >
                  {position.name}
                </option>
              ))}
            </select>

            {/* Nút Tìm kiếm */}
            <button
              onClick={() => {
                setPage(0); // Reset về trang đầu tiên khi tìm kiếm
                fetchAttendance(); // Gọi lại hàm fetch dữ liệu dựa trên bộ lọc
              }}
            >
              Tìm kiếm
            </button>
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
                          const isPastDay = isBeforeYesterday(dayDate);
                          return (
                            <td
                              key={d}
                              id={`attendance-cell-${
                                emp.employeeId
                              }-${year}-${String(month).padStart(
                                2,
                                "0"
                              )}-${String(day).padStart(2, "0")}`}
                            >
                              {isPastDay ? (
                                <>
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

                                  {!readOnly && (
                                    <div className="attendance-buttons">
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
                                    </div>
                                  )}
                                </>
                              ) : (
                                <span className="attendance-empty-cell">
                                  --
                                </span>
                              )}
                            </td>
                          );
                        } else {
                          return <td key={d}></td>;
                        }
                      } else {
                        if (
                          !isBeforeYesterday(dayDate) ||
                          !cell.hasScheduleDetail
                        ) {
                          return <td key={d}></td>;
                        }

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

      {!readOnly && (
        <>
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
        </>
      )}
    </MainLayout>
  );
};

export default AttendanceMonthlyView;
