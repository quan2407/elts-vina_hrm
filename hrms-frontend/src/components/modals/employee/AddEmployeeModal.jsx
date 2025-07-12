import React, { useEffect, useState } from "react";
import employeeService from "../../../services/employeeService";
import "../../../styles/AddEmployeeModal.css";
import "../../../styles/EmployeeTable.css";
import SuccessModal from "../../popup/SuccessModal";

const AddEmployeeModal = ({ lineId, onClose, onSuccess }) => {
    const [employees, setEmployees] = useState([]);
    const [selectedEmployeeIds, setSelectedEmployeeIds] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [showSuccess, setShowSuccess] = useState(false);

    useEffect(() => {
        const fetchAvailableEmployees = async () => {
            try {
                const response = await employeeService.getEmployeeNotInLine(lineId, searchTerm);
                setEmployees(response.data);
            } catch (error) {
                console.error("Lỗi khi lấy danh sách nhân viên chưa vào line:", error);
            }
        };
        fetchAvailableEmployees();
    }, [lineId, searchTerm]);

    const handleCheckboxChange = (employeeId) => {
        setSelectedEmployeeIds((prev) =>
            prev.includes(employeeId)
                ? prev.filter((id) => id !== employeeId)
                : [...prev, employeeId]
        );
    };

    const handleAddEmployees = async () => {
        try {
            await employeeService.addEmployeesToLine(lineId, selectedEmployeeIds);

            setShowSuccess(true);
        } catch (error) {
            console.error("Lỗi khi thêm nhân viên vào line", error);
        }
    };


    return (
        <div className="modal-overlay">
            <div className="modal-box large">
                <h3 className="modal-title sticky-title">Thêm nhân viên vào line</h3>

                <div className="search-bar-wrapper">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Tìm kiếm theo tên nhân viên..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ width: "300px", marginBottom: "16px" }}
                    />
                </div>

                <div className="employee-table-container">
                    <div className="employee-table">
                        <div className="employee-table-header">
                            <div className="employee-header-cell">Chọn</div>
                            <div className="employee-header-cell">Mã NV</div>
                            <div className="employee-header-cell">Họ tên</div>
                            <div className="employee-header-cell">Chuyền</div>
                            <div className="employee-header-cell">Giới tính</div>
                            <div className="employee-header-cell">Ngày sinh</div>
                            <div className="employee-header-cell">Ngày vào</div>
                            <div className="employee-header-cell">Phòng ban</div>
                            <div className="employee-header-cell">Vị trí</div>
                        </div>
                        {employees.length === 0 && (
                            <div className="employee-table-empty" style={{ color: 'red' }}>Không tìm thấy nhân viên nào phù hợp.</div>
                        )}

                        {employees.map((emp) => (
                            <div key={emp.employeeId} className="employee-table-row">
                                <div className="employee-table-cell">
                                    <input
                                        type="checkbox"
                                        checked={selectedEmployeeIds.includes(emp.employeeId)}
                                        onChange={() => handleCheckboxChange(emp.employeeId)}
                                    />
                                </div>
                                <div className="employee-table-cell">{emp.employeeCode}</div>
                                <div className="employee-table-cell">{emp.employeeName}</div>
                                <div className="employee-table-cell">{emp.lineName}</div>
                                <div className="employee-table-cell">{emp.gender}</div>
                                <div className="employee-table-cell">{emp.dob}</div>
                                <div className="employee-table-cell">{emp.startWorkAt}</div>
                                <div className="employee-table-cell">{emp.departmentName}</div>
                                <div className="employee-table-cell">{emp.positionName}</div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="modal-actions">
                    <button className="btn-confirm" onClick={handleAddEmployees}>
                        Thêm
                    </button>
                    <button className="btn-cancel" onClick={onClose}>
                        Hủy
                    </button>
                </div>
            </div>

            {showSuccess && (
                <SuccessModal
                    title="Thêm nhân viên thành công"
                    message={`Thêm nhân viên mới thành công!`}
                    onClose={() => {
                        setShowSuccess(false);
                        onSuccess();
                        onClose();
                    }}
                />
            )}

        </div>
    );
};

export default AddEmployeeModal;
