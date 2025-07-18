import React, { useEffect, useState } from "react";
import employeeService from "../services/employeeService";
import "../styles/EmployeeLineTable.css";
import { useParams } from "react-router-dom";
import { updateLineLeader, getLineById } from "../services/linesService";
import SuccessModal from "../components/popup/SuccessModal";
import { jwtDecode } from "jwt-decode";

function EmployeeTableInLine({ searchTerm, reloadFlag }) {
    const [employees, setEmployees] = useState([]);
    const [leaderId, setLeaderId] = useState(null);
    const [pendingLeaderId, setPendingLeaderId] = useState(null);
    const [showConfirm, setShowConfirm] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    const { id } = useParams();
    const [line, setLine] = useState([]);
    const [userRole, setUserRole] = useState("");

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
        const token = localStorage.getItem('accessToken');

        if (token) {
            try {
                const decodedToken = jwtDecode(token);
                console.log("Decoded token:", decodedToken);

                if (decodedToken.roles) {
                    const roles = decodedToken.roles;

                    if (roles.includes('ROLE_PMC')) {
                        setUserRole('pmc');
                    } else if (roles.includes('ROLE_HR')) {
                        setUserRole('hr');
                    }
                }
            } catch (error) {
                console.error('Error decoding token:', error);
                setUserRole('employee');
            }
        } else {
            setUserRole('employee');
        }
    }, []);
    useEffect(() => {
        fetchEmployees();
    }, [id, reloadFlag]);

    const handleCheckboxChange = (employeeId) => {
        if (employeeId !== leaderId) {
            setPendingLeaderId(employeeId);
            setShowConfirm(true);
        }
    };

    const confirmNewLeader = async () => {
        try {
            await updateLineLeader(id, pendingLeaderId);
            setLeaderId(pendingLeaderId);
            setShowConfirm(false);
            setShowSuccess(true);
            await fetchEmployees();
            const updatedLine = await getLineById(id);
            setLine(updatedLine);
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
        <div className="employeeLine-table-wrapper">
            <div className="employeeLine-table">
                <div className="employeeLine-table-header">
                    <div className="employeeLine-header-cell">Mã nhân viên</div>
                    <div className="employeeLine-header-cell">Tài khoản</div>
                    <div className="employeeLine-header-cell">Họ và tên</div>
                    <div className="employeeLine-header-cell">Giới tính</div>

                    <div className="employeeLine-header-cell">Số điện thoại</div>
                    <div className="employeeLine-header-cell">Vị trí</div>
                    <div className="employeeLine-header-cell">Tổ trưởng</div>
                </div>

                {filteredEmployees.length === 0 && (
                    <div className="employeeLine-table-empty" style={{ color: 'red', marginLeft: '45px' }}>Không tìm thấy nhân viên nào phù hợp.</div>
                )}

                {filteredEmployees.map((emp) => (
                    <div key={emp.employeeId} className="employeeLine-table-row">
                        <div className="employeeLine-table-cell">{emp.employeeCode}</div>
                        <div className="employeeLine-table-cell">{emp.accountUsername}</div>
                        <div className="employeeLine-table-cell">{emp.employeeName}</div>
                        <div className="employeeLine-table-cell">{emp.gender}</div>
                        <div className="employeeLine-table-cell">{emp.phoneNumber}</div>

                        <div className="employeeLine-table-cell">{emp.positionName}</div>
                        <div className="employeeLine-table-cell">
                            <input
                                type="checkbox"
                                checked={emp.employeeId === leaderId}
                                onChange={() => handleCheckboxChange(emp.employeeId)}
                                disabled={userRole !== 'hr'}

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
