import React, { useState } from "react";
import "../assets/styles/EmployeeTable.css";

function EmployeeTable() {
  const [showDropdown, setShowDropdown] = useState(null);

  const employees = [
    {
      id: 1,
      name: "yeabsire abebe",
      department: "Design",
      jobTitle: "UI UX Designer",
      startDate: "28/04/2022",
      category: "Full time",
      gender: "Female",
    },
    {
      id: 2,
      name: "feven tesfaye",
      department: "IT",
      jobTitle: "Backend Engineer",
      startDate: "28/04/2022",
      category: "Remote",
      gender: "Female",
    },
    {
      id: 3,
      name: "AMANUEL BEYENE",
      department: "Design",
      jobTitle: "UI UX Designer",
      startDate: "28/04/2022",
      category: "Full time",
      gender: "Male",
    },
    {
      id: 4,
      name: "tedla atalay",
      department: "Design",
      jobTitle: "UI UX Designer",
      startDate: "28/04/2022",
      category: "Full time",
      gender: "Male",
    },
    {
      id: 5,
      name: "redwan husein",
      department: "Design",
      jobTitle: "UI UX Designer",
      startDate: "28/04/2022",
      category: "Full time",
      gender: "Male",
    },
    {
      id: 6,
      name: "abel beyene",
      department: "Design",
      jobTitle: "UI UX Designer",
      startDate: "28/04/2022",
      category: "Full time",
      gender: "Male",
    },
    {
      id: 7,
      name: "temesgen melak",
      department: "Design",
      jobTitle: "UI UX Designer",
      startDate: "28/04/2022",
      category: "Full time",
      gender: "Male",
    },
  ];

  const toggleDropdown = (id) => {
    setShowDropdown(showDropdown === id ? null : id);
  };

  return (
    <div className="employee-table-container">
      <div className="table-header">
        <div className="header-cell name-column">Name(s)</div>
        <div className="header-cell dept-column">Dept</div>
        <div className="header-cell job-title-column">Job Title</div>
        <div className="header-cell start-date-column">Start Date</div>
        <div className="header-cell category-column">Category</div>
        <div className="header-cell gender-column">Gender</div>
        <div className="header-cell actions-column">Actions</div>
      </div>

      {employees.map((employee) => (
        <div key={employee.id} className="table-row">
          <div className="table-cell name-column">{employee.name}</div>
          <div className="table-cell dept-column">{employee.department}</div>
          <div className="table-cell job-title-column">{employee.jobTitle}</div>
          <div className="table-cell start-date-column">
            {employee.startDate}
          </div>
          <div className="table-cell category-column">{employee.category}</div>
          <div className="table-cell gender-column">{employee.gender}</div>
          <div className="table-cell actions-column">
            <div className="actions-wrapper">
              <div
                className="actions-button"
                onClick={() => toggleDropdown(employee.id)}
              >
                <span className="actions-text">Actions</span>
                <svg
                  className="actions-dropdown"
                  width="14"
                  height="15"
                  viewBox="0 0 14 15"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M13.7812 7.5C13.7812 11.5137 10.7461 14.7656 7 14.7656C3.25391 14.7656 0.21875 11.5137 0.21875 7.5C0.21875 3.48633 3.25391 0.234375 7 0.234375C10.7461 0.234375 13.7812 3.48633 13.7812 7.5ZM7.46484 10.8369L11.1699 6.86719C11.427 6.5918 11.427 6.14648 11.1699 5.87402L10.7051 5.37598C10.448 5.10059 10.0324 5.10059 9.77812 5.37598L7 8.35254L4.22187 5.37598C3.96484 5.10059 3.54922 5.10059 3.29492 5.37598L2.83008 5.87402C2.57305 6.14941 2.57305 6.59473 2.83008 6.86719L6.53516 10.8369C6.79219 11.1123 7.20781 11.1123 7.46484 10.8369Z"
                    fill="white"
                  />
                </svg>
              </div>
              {showDropdown === employee.id && employee.id === 2 && (
                <div className="dropdown-menu">
                  <div className="dropdown-item">View Profile</div>
                  <div className="dropdown-divider"></div>
                  <div className="dropdown-item">Edit Profile</div>
                </div>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default EmployeeTable;
