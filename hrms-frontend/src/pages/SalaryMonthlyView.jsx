import React, { useEffect, useState } from "react";
import salaryService from "../services/salaryService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";

const SalaryMonthlyView = () => {
  const [salaries, setSalaries] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());

  const fetchSalaries = async () => {
    try {
      const res = await salaryService.getMonthlySalaries(month, year);
      setSalaries(res.data);
    } catch (err) {
      console.error("Lỗi khi tải dữ liệu lương:", err);
    }
  };

  useEffect(() => {
    if (month && year) fetchSalaries();
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
                <th rowSpan="2">Tháng</th>
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
                  <td>{emp.salaryMonth?.slice(0, 7)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </MainLayout>
  );
};

export default SalaryMonthlyView;
