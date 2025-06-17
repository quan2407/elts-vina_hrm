import React, { useEffect, useState } from "react";
import employeeService from "../services/employeeService";
import "../styles/EmployeeTable.css";

function EmployeeTable() {
  const [employees, setEmployees] = useState([]);

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

  return (
    <div className="employee-table-wrapper" >
      <div className="employee-table">
        <div className="employee-table-header">
          <div className="employee-header-cell">Code</div>
          <div className="employee-header-cell">Name</div>
          <div className="employee-header-cell">Gender</div>
          <div className="employee-header-cell">DOB</div>
          <div className="employee-header-cell">Place of Birth</div>
          <div className="employee-header-cell">Nationality</div>
          <div className="employee-header-cell">Address</div>
          <div className="employee-header-cell">Start Work At</div>
          <div className="employee-header-cell">Phone</div>
          <div className="employee-header-cell">Citizen ID</div>
          <div className="employee-header-cell">Department</div>
          <div className="employee-header-cell">Line</div>
          <div className="employee-header-cell">Position</div>
          <div className="employee-header-cell">Account</div>
        </div>

        {employees.map((emp) => (
          <div
            key={emp.employeeId}
            className="employee-table-row"
          >
            <div className="employee-table-cell">{emp.employeeCode}</div>
            <div className="employee-table-cell">{emp.employeeName}</div>
            <div className="employee-table-cell">{emp.gender}</div>
            <div className="employee-table-cell">{emp.dob}</div>
            <div className="employee-table-cell">{emp.placeOfBirth}</div>
            <div className="employee-table-cell">{emp.nationality}</div>
            <div className="employee-table-cell">{emp.address}</div>
            <div className="employee-table-cell">{emp.startWorkAt}</div>
            <div className="employee-table-cell">{emp.phoneNumber}</div>
            <div className="employee-table-cell">{emp.citizenId}</div>
            <div className="employee-table-cell">{emp.departmentName}</div>
            <div className="employee-table-cell">{emp.lineName}</div>
            <div className="employee-table-cell">{emp.positionName}</div>
            <div className="employee-table-cell">{emp.accountUsername}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default EmployeeTable;
