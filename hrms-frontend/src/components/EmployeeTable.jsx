import React, { useState } from "react";
import "../assets/styles/EmployeeTable.css";

function EmployeeTable() {
  const [showDropdown, setShowDropdown] = useState(null);

  const employees = [
    {
      employeeId: 1,
      employeeCode: "EMP001",
      employeeName: "Yeabsire Abebe",
      gender: "Female",
      dob: "1995-04-28",
      placeOfBirth: "Addis Ababa",
      nationality: "Ethiopian",
      address: "123 Main Street",
      startWorkAt: "2022-04-28",
      phoneNumber: "0900000001",
      citizenId: "123456789",
      departmentName: "Design",
      positionName: "UI UX Designer",
      accountUsername: "yeabsire.abebe",
    },
    {
      employeeId: 2,
      employeeCode: "EMP002",
      employeeName: "Feven Tesfaye",
      gender: "Female",
      dob: "1996-06-15",
      placeOfBirth: "Mekelle",
      nationality: "Ethiopian",
      address: "456 Side Street",
      startWorkAt: "2022-04-28",
      phoneNumber: "0900000002",
      citizenId: "987654321",
      departmentName: "IT",
      positionName: "Backend Engineer",
      accountUsername: "feven.tesfaye",
    },
  ];

  const toggleDropdown = (id) => {
    setShowDropdown(showDropdown === id ? null : id);
  };

  return (
    <div className="employee-table-wrapper">
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
          <div className="employee-header-cell">Position</div>
          <div className="employee-header-cell">Account</div>
          <div className="employee-header-cell">Actions</div>
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
            <div className="employee-table-cell">{emp.positionName}</div>
            <div className="employee-table-cell">{emp.accountUsername}</div>
            <div className="employee-table-cell">
              <div className="employee-actions-wrapper">
                <div
                  className="employee-actions-button"
                  onClick={() => toggleDropdown(emp.employeeId)}
                >
                  Actions
                </div>
                {showDropdown === emp.employeeId && (
                  <div className="employee-dropdown-menu">
                    <div className="employee-dropdown-item">View Profile</div>
                    <div className="employee-dropdown-divider"></div>
                    <div className="employee-dropdown-item">Edit Profile</div>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default EmployeeTable;
