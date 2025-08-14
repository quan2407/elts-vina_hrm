import React, { useEffect, useState } from "react";
import salaryService from "../services/salaryService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";
import departmentService from "../services/departmentService";
import positionService from "../services/positionService";
import { getAllLines } from "../services/linesService";
const SalaryMonthlyView = () => {
  const [salaries, setSalaries] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [availableMonths, setAvailableMonths] = useState([]);
  const [isLocked, setIsLocked] = useState(false);
  const roles = JSON.parse(localStorage.getItem("role") || "[]");
  const isHrManager = roles.includes("ROLE_HR_MANAGER");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  let debounceTimeout;
  const [departmentId, setDepartmentId] = useState(null);
  const [positionId, setPositionId] = useState(null);
  const [lineId, setLineId] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);
  // Gom tất cả loại phụ cấp và khấu trừ từ dữ liệu lương
  const allowanceTitles = Array.from(
    new Set(
      salaries
        .flatMap((s) => s.appliedBenefits)
        .filter((b) => b.type === "PHU_CAP")
        .map((b) => b.title)
    )
  );

  const deductionTitles = Array.from(
    new Set(
      salaries
        .flatMap((s) => s.appliedBenefits)
        .filter((b) => b.type === "KHAU_TRU")
        .map((b) => b.title)
    )
  );

  const fetchSalaries = async () => {
    try {
      const res = await salaryService.getMonthlySalaries(
        month,
        year,
        page,
        size,
        searchTerm,
        departmentId,
        positionId,
        lineId
      );
      setSalaries(res.data.content);
      setTotalPages(res.data.totalPages);
      setIsLocked(res.data.content.length > 0 && res.data.content[0].locked);
    } catch (err) {
      console.error("Lỗi khi tải dữ liệu lương:", err);
    }
  };
  // useEffect(() => {
  //   clearTimeout(debounceTimeout);
  //   debounceTimeout = setTimeout(() => {
  //     fetchSalaries();
  //   }, 100);
  //   return () => clearTimeout(debounceTimeout);
  // }, [searchTerm]);

  // 1) Lấy lương khi đổi tháng/năm/trang
  useEffect(() => {
    if (month && year) fetchSalaries();
  }, [month, year, page, size]);

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

  // 3) Khi đổi department => reload positions/lines, reset positionId/lineId
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

  const fetchSalariesParams = async (m, y, p, q, depId, posId, liId) => {
    try {
      const res = await salaryService.getMonthlySalaries(
        m,
        y,
        p,
        size,
        q,
        depId,
        posId,
        liId
      );
      setSalaries(res.data.content);
      setTotalPages(res.data.totalPages);
      setIsLocked(res.data.content.length > 0 && res.data.content[0].locked);
    } catch (err) {
      console.error("Lỗi khi tải dữ liệu lương:", err);
    }
  };

  useEffect(() => {
    const fetchAvailableMonths = async () => {
      try {
        const res = await salaryService.getAvailableSalaryMonths();
        const list = res.data;
        setAvailableMonths(list);

        if (list.length > 0) {
          const [m, y] = list[list.length - 1].split("-");
          const newMonth = Number(m);
          const newYear = Number(y);
          setMonth(newMonth);
          setYear(newYear);
          await salaryService
            .getMonthlySalaries(newMonth, newYear, page, size, searchTerm)
            .then((res) => {
              setSalaries(res.data.content); // ✅ sửa lại
              setTotalPages(res.data.totalPages); // ✅ đảm bảo phân trang đúng
            });
        }
      } catch (err) {
        console.error("Không thể tải danh sách tháng có lương:", err);
      }
    };

    fetchAvailableMonths();
  }, []);
  return (
    <MainLayout>
      <div className="attendance-container">
        <div className="attendance-controls">
          <div className="salary-filters">
            <select
              value={month}
              onChange={(e) => setMonth(Number(e.target.value))}
            >
              {Array.from(
                new Set(availableMonths.map((m) => m.split("-")[0]))
              ).map((m) => (
                <option
                  key={m}
                  value={Number(m)}
                >
                  Tháng {m}
                </option>
              ))}
            </select>

            <select
              value={year}
              onChange={(e) => setYear(Number(e.target.value))}
            >
              {Array.from(
                new Set(availableMonths.map((m) => m.split("-")[1]))
              ).map((y) => (
                <option
                  key={y}
                  value={Number(y)}
                >
                  Năm {y}
                </option>
              ))}
            </select>
          </div>

          <div className="salary-actions">
            <button
              className="btn-update"
              disabled={isLocked}
              onClick={async () => {
                if (
                  window.confirm(
                    "Bạn có chắc muốn cập nhật lại bảng lương không?"
                  )
                ) {
                  try {
                    await salaryService.regenerateMonthlySalaries(month, year);
                    await fetchSalaries();
                    alert("Đã cập nhật bảng lương thành công!");
                  } catch (err) {
                    console.error("Lỗi khi cập nhật bảng lương:", err);
                    alert("Cập nhật bảng lương thất bại!");
                  }
                }
              }}
            >
              Cập nhật bảng lương
            </button>

            {isHrManager && (
              <button
                className="btn-lock"
                onClick={async () => {
                  const confirmMsg = isLocked
                    ? "Bạn có chắc muốn mở khóa bảng lương này?"
                    : "Bạn có chắc muốn chốt bảng lương này?";
                  if (window.confirm(confirmMsg)) {
                    try {
                      await salaryService.lockSalaryMonth(
                        month,
                        year,
                        !isLocked
                      );
                      await fetchSalaries();
                      alert(
                        isLocked
                          ? "Đã mở khóa bảng lương."
                          : "Đã chốt bảng lương."
                      );
                    } catch (err) {
                      alert("Lỗi khi cập nhật trạng thái chốt lương!");
                    }
                  }
                }}
                disabled={salaries.length === 0}
              >
                {isLocked ? "Đã chốt (mở khóa)" : "Chốt bảng lương"}
              </button>
            )}
            <button
              className="btn-export"
              style={{
                marginLeft: "8px",
                backgroundColor: "#2563eb",
                color: "white",
              }}
              onClick={async () => {
                if (!month || !year) {
                  alert("Vui lòng chọn tháng và năm.");
                  return;
                }
                try {
                  const response = await salaryService.exportMonthlySalaries(
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
                    `bao_cao_luong_${month
                      .toString()
                      .padStart(2, "0")}_${year}.xlsx`
                  );
                  document.body.appendChild(link);
                  link.click();
                  link.remove();
                } catch (err) {
                  console.error("Export lương thất bại:", err);
                  alert("Không thể xuất báo cáo lương.");
                }
              }}
            >
              Xuất Excel
            </button>
          </div>
        </div>
        <div className="attendance-controls">
          <div className="page-size-control">
            <label htmlFor="pageSize">Hiển thị</label>
            <select
              id="pageSize"
              value={size}
              onChange={(e) => {
                const newSize = Number(e.target.value);
                setSize(newSize);
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
              onChange={(e) => {
                setSearchTerm(e.target.value);
              }}
            />
            <select
              value={departmentId ?? ""}
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

            <select
              value={lineId ?? ""}
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

            <select
              value={positionId ?? ""}
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
            <button
              onClick={() => {
                setSearchTerm("");
                setDepartmentId(null);
                setLineId(null);
                setPositionId(null);
                setPage(0);

                fetchSalariesParams(month, year, 0, "", null, null, null);
              }}
            >
              Xóa bộ lọc
            </button>

            <button
              onClick={() => {
                setPage(0);
                fetchSalaries();
              }}
            >
              Tìm kiếm
            </button>
          </div>
        </div>
        <div className="attendance-table-area">
          <div className="attendance-table-wrapper">
            <table className="attendance-table">
              <thead>
                <tr>
                  <th rowSpan="2">STT</th>
                  <th rowSpan="2">Mã NV</th>
                  <th rowSpan="2">Họ và Tên</th>
                  <th rowSpan="2">Chức vụ</th>
                  <th rowSpan="2">Lương cơ bản</th>

                  <th colSpan={allowanceTitles.length || 1}>Phụ cấp</th>
                  <th colSpan="2">Lương sản xuất</th>
                  <th colSpan="2">Lương thêm giờ</th>

                  <th colSpan={deductionTitles.length || 1}>
                    Các khoản khấu trừ
                  </th>
                  <th rowSpan="2">Tổng trừ</th>
                  <th rowSpan="2">Thực lãnh</th>
                </tr>
                <tr>
                  {allowanceTitles.length > 0 ? (
                    allowanceTitles.map((title) => <th key={title}>{title}</th>)
                  ) : (
                    <th>--</th>
                  )}
                  <th>Số công</th>
                  <th>Tiền lương</th>
                  <th>Số giờ</th>
                  <th>Tiền lương</th>
                  {deductionTitles.length > 0 ? (
                    deductionTitles.map((title) => <th key={title}>{title}</th>)
                  ) : (
                    <th>--</th>
                  )}
                </tr>
              </thead>

              <tbody>
                {salaries.map((emp, index) => {
                  const benefitMap = {};
                  emp.appliedBenefits.forEach((b) => {
                    benefitMap[b.title] = b.amount;
                  });

                  return (
                    <tr key={emp.employeeCode}>
                      <td>{index + 1}</td>
                      <td>{emp.employeeCode}</td>
                      <td>{emp.employeeName}</td>
                      <td>{emp.positionName || "--"}</td>
                      <td>{emp.basicSalary?.toLocaleString("vi-VN")}</td>

                      {allowanceTitles.map((title) => (
                        <td key={title}>
                          {(benefitMap[title] || 0).toLocaleString("vi-VN")}
                        </td>
                      ))}

                      <td>{emp.workingDays}</td>
                      <td>{emp.productionSalary?.toLocaleString("vi-VN")}</td>
                      <td>{emp.overtimeHours}</td>
                      <td>{emp.overtimeSalary?.toLocaleString("vi-VN")}</td>

                      {deductionTitles.map((title) => (
                        <td key={title}>
                          {(benefitMap[title] || 0).toLocaleString("vi-VN")}
                        </td>
                      ))}

                      <td>{emp.totalDeduction?.toLocaleString("vi-VN")}</td>
                      <td className="highlight-bold">
                        {emp.totalIncome?.toLocaleString("vi-VN")}
                      </td>
                    </tr>
                  );
                })}
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

            {Array.from({ length: totalPages }).map((_, p) => (
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
      </div>
    </MainLayout>
  );
};

export default SalaryMonthlyView;
