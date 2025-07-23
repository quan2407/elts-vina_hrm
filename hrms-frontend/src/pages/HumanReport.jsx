import React, { useEffect, useState } from "react";
import departmentService from "../services/departmentService";
import MainLayout from "../components/MainLayout";
import "../styles/AttendanceMonthlyView.css";
import { getAllLines } from "../services/linesService";
import { getFullEmp } from "../services/humanReportService";

const HumanReport = () => {
    const [line, setLine] = useState([]);
    const [department, setDepartments] = useState([]);
    const [reportData, setReportData] = useState({});

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
                <div className="attendance-controls">


                </div>

                <div className="attendance-table-wrapper">
                    <table className="attendance-table">
                        <thead>
                            <tr>
                                <th rowSpan={2}>Phòng ban</th>

                                {/* Render phòng ban "Sản Xuất" với rowSpan */}
                                {department
                                    .filter(dep => dep.name === "Sản Xuất")
                                    .map(dep => (
                                        <th key={dep.id} colSpan={line.length}>
                                            {dep.name}
                                        </th>
                                    ))}

                                {/* Các phòng ban khác */}
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
                                                let value = "";

                                                if (row.key === "workRate") {
                                                    // Tính tỷ lệ làm việc
                                                    const total = employees.length;
                                                    const working = employees.filter(e => e.status === "Đang làm").length;
                                                    value = total > 0 ? `${Math.round((working / total) * 100)}%` : "0%";
                                                } else if (row.key === "currentStaff") {
                                                    value = employees.length;
                                                } else if (row.key === "presentToday") {
                                                    value = employees.filter(e => e.isPresent).length;
                                                } else if (row.key === "onLeave") {
                                                    value = employees.filter(e => e.status === "Nghỉ phép").length;
                                                } else if (row.key === "noSalary") {
                                                    value = employees.filter(e => e.status === "Nghỉ không lương").length;
                                                }

                                                return <td key={key}>{value}</td>;
                                            })
                                        )}

                                    {/* Dữ liệu cho các phòng ban khác */}
                                    {department
                                        .filter(dep => dep.name !== "Sản Xuất")
                                        .map(dep => {
                                            const key = dep.name;
                                            const employees = reportData[key] || [];
                                            let value = "";

                                            if (row.key === "workRate") {
                                                const total = employees.length;
                                                const working = employees.filter(e => e.status === "Đang làm").length;
                                                value = total > 0 ? `${Math.round((working / total) * 100)}%` : "0%";
                                            } else if (row.key === "currentStaff") {
                                                value = employees.length;
                                            } else if (row.key === "presentToday") {
                                                value = employees.filter(e => e.isPresent).length;
                                            } else if (row.key === "onLeave") {
                                                value = employees.filter(e => e.status === "Nghỉ phép").length;
                                            } else if (row.key === "noSalary") {
                                                value = employees.filter(e => e.status === "Nghỉ không lương").length;
                                            }

                                            return <td key={key}>{value}</td>;
                                        })}

                                    {/* Cột "Danh sách nghỉ" – ví dụ hiển thị tên nhân viên nghỉ phép */}
                                    <td>
                                        {Object.values(reportData)
                                            .flat()
                                            .filter(e => ["Nghỉ phép", "Nghỉ không lương"].includes(e.status))
                                            .map(e => e.employeeName)
                                            .join(", ")}
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
