import React, { useEffect, useState } from "react";
import departmentService from "../services/departmentService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";
import { getAllLines } from "../services/linesService";
import { getAbsentEmp, getAbsentEmpKL, getFullEmp } from "../services/humanReportService";

const HumanReport = () => {
    const [line, setLine] = useState([]);
    const [department, setDepartments] = useState([]);
    const [reportData, setReportData] = useState({});
    const [absentEmp, setAbsentEmp] = useState([]);
    const [absentEmpKL, setAbsentEmpKL] = useState([]);

    const getYesterdayLocalDate = () => {
        const now = new Date();
        now.setDate(now.getDate() - 1);

        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, "0");
        const day = String(now.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`; // Format yyyy-MM-dd
    };

    const [selectedDate, setSelectedDate] = useState(getYesterdayLocalDate());


    useEffect(() => {
        const fetchDepartments = async () => {
            try {
                const res = await departmentService.getAllDepartments();
                setDepartments(res.data);
            } catch (err) {
                console.error("Lỗi load phòng ban:", err);
            }
        };
        fetchDepartments();
    }, []);
    useEffect(() => {
        const fetchLines = async () => {
            try {
                const data = await getAllLines();
                setLine(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách line:", error);
            }
        };
        fetchLines();
    }, []);

    useEffect(() => {
        const fetchHumanReport = async () => {
            try {
                const data = await getFullEmp();
                console.log("Human Report Data:", data);
                setReportData(data);
            } catch (error) {
                console.error("Lỗi khi load báo cáo nhân sự:", error);
            }
        }
        fetchHumanReport();
    }, []);

    useEffect(() => {
        const fetchAbsentEmp = async () => {
            try {
                const data = await getAbsentEmp(selectedDate);
                console.log("Absent Employees Data:", data);
                setAbsentEmp(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách nhân viên vắng mặt:", error);
            }
        }
        fetchAbsentEmp();
    }, [selectedDate]);

    useEffect(() => {
        const fetchAbsentEmpKL = async () => {
            try {
                const data = await getAbsentEmpKL(selectedDate);
                console.log("Absent Employees by Line Data:", data);
                setAbsentEmpKL(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách nhân viên vắng mặt theo line:", error);
            }
        }
        fetchAbsentEmpKL();
    }, [selectedDate]);

    const rows = [
        { label: "Tỷ lệ công nhân viên đi làm", key: "workRate" },
        { label: "Nhân lực hiện có", key: "currentStaff" },
        { label: "Số lượng công nhân viên đi làm", key: "presentToday" },
        { label: "Số lượng công nhân viên nghỉ phép", key: "onLeave" },
        { label: "Số lượng công nhân viên nghỉ không lương", key: "noSalary" }
    ];

    return (
        <MainLayout>
            <div className="attendance-container">
                <div className="page-header">
                    <h1 className="page-title">Báo cáo nhân lực</h1>
                </div>
                <div className="attendance-controls">

                    <input
                        type="date"
                        value={selectedDate}
                        onChange={(e) => setSelectedDate(e.target.value)}
                        className="form-control"
                        style={{ width: "240px" }}
                        id="floatingInputValue"
                        max={getYesterdayLocalDate()}
                    />

                </div>

                <div className="attendance-table-wrapper">
                    <table className="attendance-table">
                        <thead>
                            <tr>
                                <th rowSpan={2}>Phòng ban</th>

                                {department
                                    .filter(dep => dep.name === "Sản Xuất")
                                    .map(dep => (
                                        <th key={dep.id} colSpan={line.length}>
                                            {dep.name}
                                        </th>
                                    ))}

                                {department
                                    .filter(dep => dep.name !== "Sản Xuất")
                                    .map(dep => (
                                        <th key={dep.id} rowSpan={2}>{dep.name}</th>
                                    ))}

                                <th rowSpan={2}>Chú thích</th>
                            </tr>

                            <tr>
                                {line.map(l => (
                                    <th key={l.id} style={{ backgroundColor: "#f3f4f6" }}>{l.name}</th>
                                ))}
                            </tr>
                        </thead>

                        <tbody>
                            {rows.map((row, rowIndex) => (
                                <tr key={rowIndex}>
                                    <td>{row.label}</td>

                                    {department
                                        .filter(dep => dep.name === "Sản Xuất")
                                        .flatMap(() =>
                                            line.map(l => {
                                                const key = l.name;
                                                const employees = reportData[key] || [];
                                                const absentEmployees = absentEmp[key] || [];
                                                const absentEmployeesKL = absentEmpKL[key] || [];
                                                let value = "";

                                                if (row.key === "workRate") {
                                                    const total = employees.length;
                                                    const working = employees.length - absentEmployees.length - absentEmployeesKL.length;
                                                    value = total > 0 ? `${Math.round((working / total) * 100)}%` : "0%";
                                                } else if (row.key === "currentStaff") {
                                                    value = employees.length;
                                                } else if (row.key === "presentToday") {
                                                    value = employees.length - absentEmployees.length - absentEmployeesKL.length;
                                                } else if (row.key === "onLeave") {
                                                    value = absentEmployees.length;
                                                } else if (row.key === "noSalary") {
                                                    value = absentEmployeesKL.length;
                                                }

                                                return <td key={key}>{value}</td>;
                                            })
                                        )}

                                    {department
                                        .filter(dep => dep.name !== "Sản Xuất")
                                        .map(dep => {
                                            const key = dep.name;
                                            const employees = reportData[key] || [];
                                            const absentEmployees = absentEmp[key] || [];
                                            const absentEmployeesKL = absentEmpKL[key] || [];
                                            let value = "";

                                            if (row.key === "workRate") {
                                                const total = employees.length;
                                                const working = employees.length - absentEmployees.length - absentEmployeesKL.length;
                                                value = total > 0 ? `${Math.round((working / total) * 100)}%` : "0%";
                                                value = total > 0 ? `${Math.round((working / total) * 100)}%` : "0%";
                                            } else if (row.key === "currentStaff") {
                                                value = employees.length;
                                            } else if (row.key === "presentToday") {
                                                value = employees.length - absentEmployees.length - absentEmployeesKL.length;
                                            } else if (row.key === "onLeave") {
                                                value = absentEmployees.length;
                                            } else if (row.key === "noSalary") {
                                                value = absentEmployeesKL.length;
                                            }

                                            return <td key={key}>{value}</td>;
                                        })}

                                    <td>
                                        {row.key === "onLeave"
                                            ? Object.values(absentEmp).flat().map(e => (
                                                <span >
                                                    {e.employeeName}
                                                    <br />
                                                </span>
                                            ))
                                            : row.key === "noSalary"
                                                ? Object.values(absentEmpKL).flat().map(e => (
                                                    <span >
                                                        {e.employeeName}
                                                        <br />
                                                    </span>
                                                ))
                                                : ""}
                                    </td>

                                </tr>
                            ))}
                        </tbody>

                    </table>
                </div>
            </div>
        </MainLayout>
    );
};

export default HumanReport;
