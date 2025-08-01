import React, { useEffect, useState } from "react";
import salaryService from "../services/salaryService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";

const SalaryMonthlyView = () => {
  const [salaries, setSalaries] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [availableMonths, setAvailableMonths] = useState([]);
  const [isLocked, setIsLocked] = useState(false);
  const roles = JSON.parse(localStorage.getItem("role") || "[]");
  const isHrManager = roles.includes("ROLE_HR_MANAGER");
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  let debounceTimeout;

  const fetchSalaries = async () => {
    try {
      const res = await salaryService.getMonthlySalaries(
        month,
        year,
        page,
        size,
        searchTerm
      );
      setSalaries(res.data.content);
      setTotalPages(res.data.totalPages);
      setIsLocked(res.data.content.length > 0 && res.data.content[0].locked);
    } catch (err) {
      console.error("Lỗi khi tải dữ liệu lương:", err);
    }
  };
  useEffect(() => {
    clearTimeout(debounceTimeout);
    debounceTimeout = setTimeout(() => {
      fetchSalaries();
    }, 100);
    return () => clearTimeout(debounceTimeout);
  }, [searchTerm]);

  useEffect(() => {
    if (month && year) fetchSalaries();
  }, [month, year, page]);

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
            .getMonthlySalaries(newMonth, newYear)
            .then((res) => {
              setSalaries(res.data);
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
          <div className="attendance-filters">
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

        <div className="attendance-table-wrapper">
          <div className="attendance-search-wrapper">
            <input
              type="text"
              className="attendance-search-input"
              placeholder="Tìm mã hoặc tên nhân viên..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPage(0);
              }}
            />
          </div>

          <table className="attendance-table">
            <thead>
              <tr>
                <th rowSpan="2">STT</th>
                <th rowSpan="2">Mã NV</th>
                <th rowSpan="2">Họ và Tên</th>
                <th rowSpan="2">Chức vụ</th>
                <th rowSpan="2">Lương cơ bản</th>
                <th colSpan="4">Phụ cấp</th>
                <th colSpan="2">Lương sản xuất</th>
                <th colSpan="2">Lương thêm giờ</th>
                <th colSpan="4">Các khoản khấu trừ</th>
                <th rowSpan="2">Tổng trừ</th>
                <th rowSpan="2">Thực lãnh</th>
              </tr>
              <tr>
                <th>Điện thoại</th>
                <th>Nhà ở</th>
                <th>Chuyên cần</th>
                <th>Đi lại</th>
                <th>Số công</th>
                <th>Tiền lương</th>
                <th>Số giờ</th>
                <th>Tiền lương</th>
                <th>BHXH</th>
                <th>BHYT</th>
                <th>BHTN</th>
                <th>Đoàn phí</th>
              </tr>
            </thead>
            <tbody>
              {salaries.map((emp, index) => (
                <tr key={emp.employeeCode}>
                  <td>{index + 1}</td>
                  <td>{emp.employeeCode}</td>
                  <td>{emp.employeeName}</td>
                  <td>{emp.positionName || "--"}</td>
                  <td>{emp.basicSalary?.toLocaleString("vi-VN")}</td>
                  <td>{emp.allowancePhone?.toLocaleString("vi-VN")}</td>
                  <td>{emp.allowanceMeal?.toLocaleString("vi-VN")}</td>
                  <td>{emp.allowanceAttendance?.toLocaleString("vi-VN")}</td>
                  <td>{emp.allowanceTransport?.toLocaleString("vi-VN")}</td>
                  <td>{emp.workingDays}</td>
                  <td>{emp.productionSalary?.toLocaleString("vi-VN")}</td>
                  <td>{emp.overtimeHours}</td>
                  <td>{emp.overtimeSalary?.toLocaleString("vi-VN")}</td>
                  <td>{emp.socialInsurance?.toLocaleString("vi-VN")}</td>
                  <td>{emp.healthInsurance?.toLocaleString("vi-VN")}</td>
                  <td>{emp.unemploymentInsurance?.toLocaleString("vi-VN")}</td>
                  <td>{emp.unionFee?.toLocaleString("vi-VN")}</td>
                  <td>{emp.totalDeduction?.toLocaleString("vi-VN")}</td>
                  <td className="highlight-bold">
                    {emp.totalIncome?.toLocaleString("vi-VN")}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
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
