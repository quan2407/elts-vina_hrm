import React, { useEffect, useMemo, useState } from "react";
import salaryService from "../services/salaryService";
import MainLayout from "../components/MainLayout";
import "../styles/SalaryMonthlyView.css";

const EmpSalaryView = () => {
  const [salaries, setSalaries] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [availableMonths, setAvailableMonths] = useState([]);
  const [loading, setLoading] = useState(false);
  const pad2 = (n) => String(n).padStart(2, "0");
  const formatCurrency = (val) => {
    if (val == null) return "--";
    const num = typeof val === "number" ? val : Number(val);
    if (!Number.isFinite(num)) return "--";
    return num.toLocaleString("vi-VN");
  };
  const fmt1Dec = (v) => (v == null || v === "" ? "--" : Number(v).toFixed(1));
  const allowanceTitles = useMemo(() => {
    return Array.from(
      new Set(
        (salaries || []).flatMap((s) =>
          (s.appliedBenefits || [])
            .filter((b) => b.type === "PHU_CAP")
            .map((b) => b.title)
        )
      )
    );
  }, [salaries]);

  const deductionTitles = useMemo(() => {
    return Array.from(
      new Set(
        (salaries || []).flatMap((s) =>
          (s.appliedBenefits || [])
            .filter((b) => b.type === "KHAU_TRU")
            .map((b) => b.title)
        )
      )
    );
  }, [salaries]);
  const uniqueMonths = useMemo(
    () =>
      Array.from(
        new Set((availableMonths || []).map((m) => m.split("-")[0]))
      ).map((m) => Number(m)),
    [availableMonths]
  );
  const uniqueYears = useMemo(
    () =>
      Array.from(
        new Set((availableMonths || []).map((m) => m.split("-")[1]))
      ).map((y) => Number(y)),
    [availableMonths]
  );
  useEffect(() => {
    const fetchAvailable = async () => {
      try {
        const res = await salaryService.getAvailableSalaryMonths();
        const list = res?.data || [];
        setAvailableMonths(list);

        if (list.length > 0) {
          const [m, y] = list[list.length - 1].split("-");
          const newMonth = Number(m);
          const newYear = Number(y);
          setMonth(newMonth);
          setYear(newYear);
          setLoading(true);
          try {
            const sRes = await salaryService.getEmpMonthlySalaries(
              newMonth,
              newYear
            );
            setSalaries(sRes?.data || []);
          } finally {
            setLoading(false);
          }
        } else {
          setSalaries([]);
        }
      } catch (err) {
        console.error("Không thể tải danh sách tháng có lương:", err);
        setAvailableMonths([]);
        setSalaries([]);
      }
    };
    fetchAvailable();
  }, []);

  useEffect(() => {
    const loadEmpSalaries = async () => {
      if (!month || !year) return;
      setLoading(true);
      try {
        const res = await salaryService.getEmpMonthlySalaries(month, year);
        setSalaries(res?.data || []);
      } catch (err) {
        console.error("Không thể tải lương nhân viên:", err);
        setSalaries([]);
      } finally {
        setLoading(false);
      }
    };
    loadEmpSalaries();
  }, [month, year]);

  const renderRow = (label, keyName, formatter, className) => (
    <tr key={keyName}>
      <th className={className}>{label}</th>
      {salaries.map((emp, index) => (
        <td
          key={`${keyName}-${index}`}
          className={formatter ? "right-align" : ""}
        >
          {formatter ? formatter(emp[keyName]) : emp[keyName] || "--"}
        </td>
      ))}
    </tr>
  );

  const renderBenefitRow = (title) => (
    <tr key={title}>
      <th>{title}</th>
      {salaries.map((emp, index) => {
        const b = (emp.appliedBenefits || []).find((x) => x.title === title);
        const val = b?.amount ?? 0;
        return (
          <td
            key={`${title}-${index}`}
            className="right-align"
          >
            {formatCurrency(val)}
          </td>
        );
      })}
    </tr>
  );

  const hasData = salaries && salaries.length > 0;

  return (
    <MainLayout>
      <div className="salary-container">
        <h1 className="salary-title">Bảng lương tháng</h1>

        <div className="salary-controls">
          <select
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
            disabled={uniqueMonths.length === 0}
          >
            {uniqueMonths.map((m) => (
              <option
                key={m}
                value={m}
              >
                Tháng {m}
              </option>
            ))}
          </select>

          <select
            value={year}
            onChange={(e) => setYear(Number(e.target.value))}
            disabled={uniqueYears.length === 0}
          >
            {uniqueYears.map((y) => (
              <option
                key={y}
                value={y}
              >
                Năm {y}
              </option>
            ))}
          </select>
        </div>

        {loading ? (
          <div className="salary-empty">Đang tải dữ liệu...</div>
        ) : hasData ? (
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
                {renderRow("Chức vụ", "positionName")}
                {renderRow("Lương cơ bản", "basicSalary", formatCurrency)}
                <tr>
                  <th
                    colSpan={salaries.length + 1}
                    className="group-header"
                    style={{ backgroundColor: "#cbcbcb" }}
                  >
                    Phụ cấp
                  </th>
                </tr>
                {allowanceTitles.length > 0 ? (
                  allowanceTitles.map((t) => renderBenefitRow(t))
                ) : (
                  <tr>
                    <th>--</th>
                    {salaries.map((_, idx) => (
                      <td key={`allowance-empty-${idx}`}>--</td>
                    ))}
                  </tr>
                )}
                <tr>
                  <th
                    colSpan={salaries.length + 1}
                    className="group-header"
                    style={{ backgroundColor: "#cbcbcb" }}
                  >
                    Lương sản xuất
                  </th>
                </tr>
                {renderRow("Số công", "workingDays", fmt1Dec)}
                {renderRow("Tiền lương", "productionSalary", formatCurrency)}
                <tr>
                  <th
                    colSpan={salaries.length + 1}
                    className="group-header"
                    style={{ backgroundColor: "#cbcbcb" }}
                  >
                    Lương thêm giờ
                  </th>
                </tr>
                {renderRow("Số giờ", "overtimeHours", fmt1Dec)}
                {renderRow("Tiền lương", "overtimeSalary", formatCurrency)}
                <tr>
                  <th
                    colSpan={salaries.length + 1}
                    className="group-header"
                    style={{ backgroundColor: "#cbcbcb" }}
                  >
                    Các khoản khấu trừ
                  </th>
                </tr>
                {deductionTitles.length > 0 ? (
                  deductionTitles.map((t) => renderBenefitRow(t))
                ) : (
                  <tr>
                    <th>--</th>
                    {salaries.map((_, idx) => (
                      <td key={`deduction-empty-${idx}`}>--</td>
                    ))}
                  </tr>
                )}

                {renderRow("Tổng trừ", "totalDeduction", formatCurrency)}

                <tr>
                  <th
                    colSpan={salaries.length + 1}
                    className="group-header"
                    style={{ backgroundColor: "#cbcbcb" }}
                  >
                    Tổng thu nhập
                  </th>
                </tr>
                {renderRow(
                  "Thực lãnh",
                  "totalIncome",
                  formatCurrency,
                  "highlight-bold"
                )}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="salary-empty">
            Chưa có bảng lương cho tháng {pad2(month)}/{year}
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default EmpSalaryView;
