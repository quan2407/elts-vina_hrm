import React, { useEffect, useState } from "react";
import salaryService from "../services/salaryService";
import MainLayout from "../components/MainLayout";
import "../styles/SalaryMonthlyView.css";

const EmpSalaryView = () => {
    const [salaries, setSalaries] = useState([]);
    const [month, setMonth] = useState(new Date().getMonth() + 1);
    const [year, setYear] = useState(new Date().getFullYear());
    const [availableMonths, setAvailableMonths] = useState([]);

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

                    const salaryRes = await salaryService.getEmpMonthlySalaries(newMonth, newYear);
                    setSalaries(salaryRes.data);
                }
            } catch (err) {
                console.error("Không thể tải danh sách tháng có lương:", err);
            }
        };

        fetchAvailableMonths();
    }, []);

    const renderRow = (label, keyName, formatter, className) => (
        <tr key={keyName}>
            <th className={className}>{label}</th>
            {salaries.map((emp, index) => (
                <td key={`${keyName}-${index}`} className={formatter ? "right-align" : ""}>
                    {formatter ? formatter(emp[keyName]) : emp[keyName] || "--"}
                </td>
            ))}
        </tr>
    );


    const formatCurrency = (val) => val?.toLocaleString("vi-VN");

    return (
        <MainLayout>
            <div className="salary-container">
                <h1 className="salary-title">Bảng lương tháng</h1>

                <div className="salary-controls">
                    <select value={month} onChange={(e) => setMonth(Number(e.target.value))}>
                        {Array.from(new Set(availableMonths.map((m) => m.split("-")[0]))).map((m) => (
                            <option key={m} value={Number(m)}>Tháng {m}</option>
                        ))}
                    </select>
                    <select value={year} onChange={(e) => setYear(Number(e.target.value))}>
                        {Array.from(new Set(availableMonths.map((m) => m.split("-")[1]))).map((y) => (
                            <option key={y} value={Number(y)}>Năm {y}</option>
                        ))}
                    </select>
                </div>


                {salaries.length > 0 ? (

                    <div className="salary-table-wrapper">
                        <table className="salary-table">
                            <thead>
                                <tr>
                                    <th>Nhân viên</th>
                                    {salaries.map((emp, index) => (
                                        <th key={`emp-${index}`}>
                                            {emp.employeeCode} <br /> {emp.employeeName}
                                        </th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {/* Thông tin cơ bản */}
                                {renderRow("Chức vụ", "positionName")}
                                {renderRow("Lương cơ bản", "basicSalary", formatCurrency)}

                                {/* Nhóm: Phụ cấp */}
                                <tr>
                                    <th colSpan={salaries.length + 1} className="group-header" style={{ backgroundColor: "#cbcbcb" }}>Phụ cấp</th>
                                </tr>
                                {renderRow("Điện thoại", "allowancePhone", formatCurrency)}
                                {renderRow("Nhà ở", "allowanceMeal", formatCurrency)}
                                {renderRow("Chuyên cần", "allowanceAttendance", formatCurrency)}
                                {renderRow("Đi lại", "allowanceTransport", formatCurrency)}

                                {/* Nhóm: Lương sản xuất */}
                                <tr><th colSpan={salaries.length + 1} className="group-header" style={{ backgroundColor: "#cbcbcb" }}>Lương sản xuất</th></tr>
                                {renderRow("Số công", "workingDays",formatCurrency)}
                                {renderRow("Tiền lương", "productionSalary", formatCurrency)}

                                {/* Nhóm: Lương thêm giờ */}
                                <tr><th colSpan={salaries.length + 1} className="group-header" style={{ backgroundColor: "#cbcbcb" }}>Lương thêm giờ</th></tr>
                                {renderRow("Số giờ", "overtimeHours", formatCurrency)}
                                {renderRow("Tiền lương", "overtimeSalary", formatCurrency)}

                                {/* Nhóm: Các khoản khấu trừ */}
                                <tr><th colSpan={salaries.length + 1} className="group-header" style={{ backgroundColor: "#cbcbcb" }}>Các khoản khấu trừ</th></tr>
                                {renderRow("BHXH", "socialInsurance", formatCurrency)}
                                {renderRow("BHYT", "healthInsurance", formatCurrency)}
                                {renderRow("BHTN", "unemploymentInsurance", formatCurrency)}
                                {renderRow("Đoàn phí", "unionFee", formatCurrency)}

                                {renderRow("Tổng trừ", "totalDeduction", formatCurrency)}
                                <tr><th colSpan={salaries.length + 1} className="group-header" style={{ backgroundColor: "#cbcbcb" }}>Tổng thu nhập</th></tr>
                                {renderRow("Thực lãnh", "totalIncome", formatCurrency, "highlight-bold")}
                            </tbody>
                        </table>
                    </div>

                ) : (
                    <div className="salary-empty">Chưa có bảng lương cho tháng {month < 10 ? `0${month}` : month}/{year}</div>
                )}
            </div>
        </MainLayout>
    );
};

export default EmpSalaryView;
