import React, { useEffect, useState } from "react";
import employeeService from "../services/employeeService";
import "../styles/EmployeeTable.css";
import { useParams } from "react-router-dom";
import { updateLineLeader, getLineById } from "../services/linesService";
import SuccessModal from "../components/popup/SuccessModal";

function EmployeeTableInLine({ searchTerm, reloadFlag }) {
    const [employees, setEmployees] = useState([]);
    const [leaderId, setLeaderId] = useState(null);
    const [pendingLeaderId, setPendingLeaderId] = useState(null);
    const [showConfirm, setShowConfirm] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    const { id } = useParams();
    const [line, setLine] = useState([]);

    useEffect(() => {
        const fetchCandidates = async () => {
            try {
                const data = await getLineById(id);
                setLine(data);
                setLeaderId(data.leaderId);
            } catch (error) {
                console.error("Lỗi khi load line:", error);
            }
        };
        fetchCandidates();
    }, [id]);

    const fetchEmployees = async () => {
        try {
            const response = await employeeService.getEmployeeByLineId(id);
            setEmployees(response.data);
        } catch (error) {
            console.error("Failed to fetch employees", error);
        }
    };

    useEffect(() => {
        fetchEmployees();
    }, [id, reloadFlag]);

    // const handleCheckboxChange = (employeeId) => {
    //     if (employeeId !== leaderId) {
    //         setPendingLeaderId(employeeId);
    //         setShowConfirm(true);
    //     }
    // };

    const confirmNewLeader = async () => {
        try {
            await updateLineLeader(id, pendingLeaderId);
            setLeaderId(pendingLeaderId);
            setShowConfirm(false);
            setShowSuccess(true);
        } catch (error) {
            console.error("Cập nhật tổ trưởng thất bại", error);
        }
    };

    const cancelChange = () => {
        setPendingLeaderId(null);
        setShowConfirm(false);
    };

    const filteredEmployees = employees.filter(emp =>
        emp.employeeName.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="employee-table-wrapper">
            <div className="employee-table">
                <div className="employee-table-header">
                    <div className="employee-header-cell">Mã nhân viên</div>
                    <div className="employee-header-cell">Tài khoản</div>
                    <div className="employee-header-cell">Họ và tên</div>
                    <div className="employee-header-cell">Giới tính</div>

                    <div className="employee-header-cell">Số điện thoại</div>
                    <div className="employee-header-cell">Vị trí</div>
                    <div className="employee-header-cell">Tổ trưởng</div>
                </div>

                {filteredEmployees.length === 0 && (
                    <div className="employee-table-empty" style={{ color: 'red' , marginLeft: '45px'}}>Không tìm thấy nhân viên nào phù hợp.</div>
                )}

                {filteredEmployees.map((emp) => (
                    <div key={emp.employeeId} className="employee-table-row">
                        <div className="employee-table-cell">{emp.employeeCode}</div>
                        <div className="employee-table-cell">{emp.accountUsername}</div>
                        <div className="employee-table-cell">{emp.employeeName}</div>
                        <div className="employee-table-cell">{emp.gender}</div>
                        <div className="employee-table-cell">{emp.phoneNumber}</div>

                        <div className="employee-table-cell">{emp.positionName}</div>
                        <div className="employee-table-cell">
                            <input
                                type="checkbox"
                                checked={emp.employeeId === leaderId}
                                // onChange={() => handleCheckboxChange(emp.employeeId)}
                            />
                        </div>
                    </div>
                ))}
            </div>

            {showConfirm && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <h3 className="modal-title">Xác nhận bổ nhiệm tổ trưởng</h3>
                        <p className="modal-content">
                            Bạn có chắc chắn muốn bổ nhiệm nhân viên này làm tổ trưởng cho {line.name}?
                        </p>
                        <div className="modal-actions">
                            <button className="btn-confirm" onClick={confirmNewLeader}>Lưu</button>
                            <button className="btn-cancel" onClick={cancelChange}>Hủy</button>
                        </div>
                    </div>
                </div>
            )}

            {showSuccess && (
                <SuccessModal
                    title="Bổ nhiệm thành công"
                    message={`Bổ nhiệm tổ trưởng mới cho ${line.name} thành công!`}
                    onClose={() => setShowSuccess(false)}
                />
            )}
        </div>
    );
}

export default EmployeeTableInLine;
