import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import employeeService from "../services/employeeService";
import "../styles/EmployeeTable.css";

function EmployeeTable() {
  const [employees, setEmployees] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    employeeService
      .getAllEmployees()
      .then((response) => {
        setEmployees(response.data);
      })
      .catch((error) => {
        console.error("Failed to fetch employees", error);
      });
  }, []);

  const handleRowClick = (id) => {
    navigate(`/employees/${id}`);
  };

  return (
    <div className="employee-table-wrapper">
      <div className="employee-table">
        <div className="employee-table-header">
          <div className="employee-header-cell">Mã nhân viên</div>
          <div className="employee-header-cell">Tài khoản</div>
          <div className="employee-header-cell">Họ và tên</div>
          <div className="employee-header-cell">Giới tính</div>
          <div className="employee-header-cell">Ngày sinh</div>
          <div className="employee-header-cell">Nơi sinh</div>
          <div className="employee-header-cell">Quốc tịch</div>
          <div className="employee-header-cell">Địa chỉ</div>
          <div className="employee-header-cell">Ngày vào công ty</div>
          <div className="employee-header-cell">Số điện thoại</div>
          <div className="employee-header-cell">Phòng ban</div>
          <div className="employee-header-cell">Chuyền sản xuất</div>
          <div className="employee-header-cell">Vị trí</div>
        </div>

        {employees.map((emp) => (
          <div
            key={emp.employeeId}
            className="employee-table-row"
            onClick={() => handleRowClick(emp.employeeId)}
            style={{ cursor: "pointer" }}
          >
            <div className="employee-table-cell">{emp.employeeCode}</div>
            <div className="employee-table-cell">{emp.accountUsername}</div>
            <div className="employee-table-cell">{emp.employeeName}</div>
            <div className="employee-table-cell">{emp.gender}</div>
            <div className="employee-table-cell">{emp.dob}</div>
            <div className="employee-table-cell">{emp.placeOfBirth}</div>
            <div className="employee-table-cell">{emp.nationality}</div>
            <div className="employee-table-cell">{emp.address}</div>
            <div className="employee-table-cell">{emp.startWorkAt}</div>
            <div className="employee-table-cell">{emp.phoneNumber}</div>
            <div className="employee-table-cell">{emp.departmentName}</div>
            <div className="employee-table-cell">{emp.lineName}</div>
            <div className="employee-table-cell">{emp.positionName}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default EmployeeTable;
