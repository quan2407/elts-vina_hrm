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
import { getAllLines } from "../services/linesService";
import SuccessModal from "../components/popup/SuccessModal";
import axios from "../services/axiosClient";
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
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [importModalOpen, setImportModalOpen] = useState(false);
  const [file, setFile] = useState(null);
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "success",
  });

  const getLocalToday = () => {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth(), now.getDate());
  };
  const [localToday, setLocalToday] = useState(getLocalToday());
  useEffect(() => {
    const interval = setInterval(() => {
      const now = getLocalToday();
      setLocalToday((prev) => {
        if (prev.getTime() !== now.getTime()) {
          console.log(
            "✅ Forced localToday update:",
            now.toLocaleDateString("sv-SE")
          );
          return now;
        }
        return prev;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const showSuccess = (title, message) =>
    setModal({ open: true, title, message, type: "success" });

  const showError = (title, message) =>
    setModal({ open: true, title, message, type: "error" });
  const canGenerateFor = (m, y) => {
    const mm = Number(m),
      yy = Number(y);
    if (!mm || !yy) return false;

    const cutoffLocal = new Date(yy, mm, 1);

    const fmt = (d) => d.toLocaleDateString("sv-SE");
    // console.log("🕒 Hôm nay (localToday):", fmt(localToday));
    // console.log("🔑 Cutoff (local):", fmt(cutoffLocal));

    return localToday.getTime() >= cutoffLocal.getTime();
  };

  const [salaryExists, setSalaryExists] = useState(false);

  const checkSalaryExists = async (m, y) => {
    const mm = Number(m),
      yy = Number(y);
    if (!mm || !yy) {
      setSalaryExists(false);
      return;
    }
    try {
      const res = await salaryService.getMonthlySalaries(
        mm,
        yy,
        0,
        1,
        "",
        null,
        null,
        null
      );
      const rows = res?.data?.content ?? [];
      console.log("So luong" + rows);
      setSalaryExists(rows.length > 0);
    } catch (e) {
      console.error("Kiểm tra tồn tại bảng lương lỗi:", e);
      setSalaryExists(false);
    }
  };
  useEffect(() => {
    if (import.meta?.env?.DEV) {
      console.log("⟳ Hard reload at:", new Date().toString());
    }
  }, []);

  const params = new URLSearchParams(location.search);
  const empId = params.get("focusEmployee");
  const focusDate = params.get("focusDate");
  const targetCellId =
    empId && focusDate ? `attendance-cell-${empId}-${focusDate}` : null;
  console.log("🔍 targetCellId:", targetCellId);
  const handleGenerateSalary = async () => {
    if (!month || !year) return;

    try {
      await salaryService.regenerateMonthlySalaries(month, year);
      showSuccess(
        "Tạo bảng lương",
        `Tạo bảng lương cho ${month}/${year} thành công.`
      );
      await checkSalaryExists(month, year);
    } catch (error) {
      console.error("Tạo bảng lương thất bại:", error);
      showError("Tạo bảng lương", "Tạo bảng lương thất bại!");
    }
  };
  const fetchAttendanceParams = async (m, y, p, q, depId, posId, liId) => {
    try {
      const res = await attendanceService.getMonthlyAttendance(
        m,
        y,
        p,
        size,
        q,
        depId,
        posId,
        liId
      );
      setData(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error("Lỗi khi tải dữ liệu chấm công:", err);
    }
  };

  const handleImportAttendance = async (file, date) => {
    try {
      await attendanceService.importAttendanceFromExcel(file, date);
      showSuccess("Import chấm công", "Import thành công.");
      setImportModalOpen(false);
      fetchAttendance();
    } catch (error) {
      console.error("Import lỗi:", error);
      showError("Import chấm công", "Import thất bại!");
    }
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
      showError("Cập nhật nghỉ phép", "Cập nhật nghỉ phép thất bại!");
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
        const list = Array.isArray(res.data) ? res.data : [];
        const sorted = [...list].sort((a, b) => {
          if (a.year !== b.year) return b.year - a.year;
          return b.month - a.month;
        });

        setAvailableMonths(sorted);

        const focusDate = params.get("focusDate");
        let defaultMonth = null;
        let defaultYear = null;

        if (sorted.length > 0) {
          if (focusDate) {
            const [fy, fm] = focusDate.split("-").map(Number);
            const exists = sorted.some((d) => d.month === fm && d.year === fy);
            if (exists) {
              defaultMonth = fm;
              defaultYear = fy;
            }
          }
          if (defaultMonth === null || defaultYear === null) {
            defaultMonth = sorted[0].month;
            defaultYear = sorted[0].year;
          }
        }

        setMonth(defaultMonth);
        setYear(defaultYear);
      } catch (error) {
        console.error("Lỗi khi tải danh sách tháng/năm:", error);
        setAvailableMonths([]);
        setMonth(null);
        setYear(null);
      }
    };

    fetchAvailableMonths();
  }, []);

  useEffect(() => {
    if (month && year) {
      fetchAttendance();
      checkSalaryExists(month, year);
    }
  }, [month, year, page, size]);

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
        showError("Cập nhật giờ vào/ra", "Cập nhật thất bại!");
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
    setTimeout(tryScroll, 500);
  }, [data, targetCellId]);
  const [canGenerateSalary, setCanGenerateSalary] = useState(false);

  useEffect(() => {
    const checkAttendanceCoverage = async () => {
      try {
        const res = await axios.get("/attendances/check-schedule-coverage", {
          params: { month, year },
        });
        const data = res.data;

        const valid =
          data.totalScheduleCount === data.totalAttendanceCount &&
          data.okToGenerate; // backend cũng đảm bảo thời gian
        console.log("Result" + data.totalAttendanceCount);
        setCanGenerateSalary(valid);
      } catch (e) {
        console.error("❌ Failed to check attendance coverage:", e);
        setCanGenerateSalary(false);
      }
    };

    checkAttendanceCoverage();
  }, [month, year]);
  return (
    <MainLayout>
      <div className="attendance-container">
        <div className="attendance-controls">
          <div className="salary-filters">
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

          <div className="salary-actions">
            <div className="leave-code-popover-wrapper">
              <button
                className="leave-code-toggle-btn"
                onClick={() =>
                  document
                    .getElementById("leave-code-popover")
                    ?.classList.toggle("show")
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
              className="btn-update"
              onClick={handleGenerateSalary}
              disabled={
                !(canGenerateFor(month, year) || canGenerateSalary) ||
                salaryExists
              }
              title={
                salaryExists
                  ? "Đã tạo bảng lương cho tháng/năm này."
                  : canGenerateFor(month, year) || canGenerateSalary
                  ? "Tạo bảng lương cho tháng đã chọn"
                  : "Chỉ tạo được từ ngày 01 hoặc khi công đã đầy đủ"
              }
            >
              {salaryExists ? "Đã tạo bảng lương" : "Tạo bảng lương"}
            </button>

            <button
              className="btn-update"
              style={{ backgroundColor: "#2563eb" }}
              onClick={async () => {
                if (!month || !year) {
                  showError(
                    "Xuất báo cáo",
                    "Vui lòng chọn tháng và năm trước khi xuất báo cáo."
                  );
                  return;
                }

                try {
                  const res = await attendanceService.exportAttendanceToExcel(
                    month,
                    year
                  );
                  const blob = new Blob([res.data], {
                    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                  });
                  const url = URL.createObjectURL(blob);
                  const a = document.createElement("a");
                  a.href = url;
                  a.download = `baocao_chamcong_thang_${String(month).padStart(
                    2,
                    "0"
                  )}_${year}.xlsx`;
                  document.body.appendChild(a);
                  a.click();
                  a.remove();
                } catch (e) {
                  console.error(e);
                  showError(
                    "Xuất báo cáo",
                    "Không thể xuất báo cáo chấm công."
                  );
                }
              }}
            >
              Xuất Excel
            </button>

            <button
              className="btn-update"
              style={{ backgroundColor: "#22c55e" }}
              onClick={() => setImportModalOpen(true)}
            >
              Import Excel
            </button>
            {importModalOpen && (
              <div className="import-popup-overlay">
                <div className="import-popup-content">
                  <h3>Nhập file chấm công Excel</h3>

                  <div style={{ marginBottom: "12px" }}>
                    <label style={{ marginRight: "8px" }}>
                      Chọn ngày áp dụng:
                    </label>
                    <DatePicker
                      selected={
                        selectedDate
                          ? parse(selectedDate, "dd-MM-yyyy", new Date())
                          : null
                      }
                      onChange={(date) => {
                        const day = String(date.getDate()).padStart(2, "0");
                        const month = String(date.getMonth() + 1).padStart(
                          2,
                          "0"
                        );
                        const year = date.getFullYear();
                        const formatted = `${day}-${month}-${year}`;
                        setSelectedDate(formatted);
                        console.log("Ngày được chọn:", formatted);
                      }}
                      dateFormat="dd-MM-yyyy"
                      placeholderText="Chọn ngày áp dụng"
                      minDate={new Date(year, month - 1, 1)}
                      maxDate={
                        new Date(
                          localToday.getFullYear(),
                          localToday.getMonth(),
                          localToday.getDate() - 1
                        )
                      }
                      className="attendance-search-input"
                    />
                  </div>

                  <div style={{ marginBottom: "12px" }}>
                    <label>Chọn file Excel (.xlsx): </label>
                    <input
                      type="file"
                      accept=".xlsx"
                      onChange={(e) => setFile(e.target.files[0])}
                    />
                  </div>

                  <div className="import-popup-actions">
                    <button
                      onClick={() => {
                        if (!selectedDate || !file) {
                          alert("Vui lòng chọn ngày và file Excel.");
                          return;
                        }

                        // ✅ Chuyển từ dd-MM-yyyy => yyyy-MM-dd
                        const [day, month, year] = selectedDate.split("-");
                        const apiFormattedDate = `${year}-${month}-${day}`;
                        console.log("📤 Gửi API với ngày:", apiFormattedDate);

                        handleImportAttendance(file, apiFormattedDate);
                      }}
                      style={{
                        padding: "8px 14px",
                        backgroundColor: "#22c55e",
                        color: "#fff",
                        border: "none",
                        borderRadius: "4px",
                      }}
                    >
                      Import
                    </button>
                    <button
                      onClick={() => setImportModalOpen(false)}
                      style={{
                        padding: "8px 14px",
                        backgroundColor: "#ccc",
                        border: "none",
                        borderRadius: "4px",
                      }}
                    >
                      Hủy
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        <div className="attendance-controls">
          <div className="page-size-control">
            <label htmlFor="pageSize">Hiển thị</label>
            <select
              id="pageSize"
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value));
                setPage(0);
              }}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
            <span>/ trang</span>
          </div>

          <div className="salary-filters">
            <input
              type="text"
              className="attendance-search-input"
              placeholder="Tìm mã hoặc tên nhân viên..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            <select
              value={departmentId ?? ""}
              onChange={(e) => setDepartmentId(e.target.value)}
            >
              <option value="">-- Chọn Bộ Phận --</option>
              {departments.map((d) => (
                <option
                  key={d.id}
                  value={d.id}
                >
                  {d.name}
                </option>
              ))}
            </select>

            <select
              value={lineId ?? ""}
              onChange={(e) => setLineId(e.target.value)}
            >
              <option value="">-- Chọn Chuyền --</option>
              {lines.map((l) => (
                <option
                  key={l.id}
                  value={l.id}
                >
                  {l.name}
                </option>
              ))}
            </select>

            <select
              value={positionId ?? ""}
              onChange={(e) => setPositionId(e.target.value)}
            >
              <option value="">-- Chọn Chức Vụ --</option>
              {positions.map((p) => (
                <option
                  key={p.id}
                  value={p.id}
                >
                  {p.name}
                </option>
              ))}
            </select>

            <button
              onClick={() => {
                setSearchTerm("");
                setDepartmentId(null);
                setLineId(null);
                setPositionId(null);
                setPage(0);
                fetchAttendanceParams(month, year, 0, "", null, null, null);
              }}
            >
              Xóa bộ lọc
            </button>

            <button
              onClick={() => {
                setPage(0);
                fetchAttendance();
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
                      const dayKey1 = String(d + 1);
                      const dayKey2 = dayKey1.padStart(2, "0");
                      const cell =
                        emp.attendanceByDate?.[dayKey1] ??
                        emp.attendanceByDate?.[dayKey2] ??
                        {};

                      if (type.key === "checkInOut") {
                        if (cell.hasScheduleDetail) {
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
                                          hasOt: parseFloat(cell.overtime) > 0,
                                        }
                                      )
                                    }
                                    title="Chọn loại nghỉ phép"
                                  >
                                    🛏️
                                  </button>
                                </div>
                              )}
                            </td>
                          );
                        } else {
                          return <td key={d}></td>;
                        }
                      } else {
                        // Công ngày / Tăng ca / Cuối tuần / Ngày lễ
                        if (cell.hasScheduleDetail) {
                          const v = cell?.[type.key];
                          return <td key={d}>{v ?? ""}</td>;
                        } else {
                          return <td key={d}></td>;
                        }
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
                  showError("Xóa giờ vào/ra", "Xóa thất bại!");
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
      {modal.open && (
        <SuccessModal
          title={modal.title}
          message={modal.message}
          type={modal.type}
          onClose={() => setModal((m) => ({ ...m, open: false }))}
        />
      )}
    </MainLayout>
  );
};

export default AttendanceMonthlyView;
