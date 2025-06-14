import React, { useState } from "react";
import "../assets/styles/AccountTable.css";

function AccountTable() {
  const [showDropdown, setShowDropdown] = useState(null);

  const accounts = [
    {
      id: 1,
      username: "admin123",
      email: "admin@example.com",
      roles: ["ROLE_ADMIN", "ROLE_PMC"],
      isActive: true,
      lastLoginAt: "2024-06-10T10:12:00",
    },
    {
      id: 2,
      username: "user456",
      email: "user@example.com",
      roles: ["ROLE_EMPLOYEE"],
      isActive: false,
      lastLoginAt: "2024-06-01T09:45:00",
    },
  ];

  const toggleDropdown = (id) => {
    setShowDropdown(showDropdown === id ? null : id);
  };

  return (
    <div className="account-table-container">
      <div className="account-table-header">
        <div className="account-header-cell username-column">Username</div>
        <div className="account-header-cell email-column">Email</div>
        <div className="account-header-cell role-column">Roles</div>
        <div className="account-header-cell active-column">Active</div>
        <div className="account-header-cell login-column">Last Login</div>
        <div className="account-header-cell actions-column">Actions</div>
      </div>

      {accounts.map((acc) => (
        <div
          key={acc.id}
          className="account-table-row"
        >
          <div className="account-table-cell username-column">
            {acc.username}
          </div>
          <div className="account-table-cell email-column">{acc.email}</div>
          <div className="account-table-cell role-column">
            {acc.roles.join(", ")}
          </div>
          <div className="account-table-cell active-column">
            {acc.isActive ? "Yes" : "No"}
          </div>
          <div className="account-table-cell login-column">
            {acc.lastLoginAt}
          </div>
          <div className="account-table-cell actions-column">
            <div className="account-actions-wrapper">
              <div
                className="account-actions-button"
                onClick={() => toggleDropdown(acc.id)}
              >
                <span className="account-actions-text">Actions</span>
                <svg
                  className="account-actions-dropdown"
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
              {showDropdown === acc.id && (
                <div className="account-dropdown-menu">
                  <div className="account-dropdown-item">View Details</div>
                  <div className="account-dropdown-divider"></div>
                  <div className="account-dropdown-item">Disable Account</div>
                </div>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default AccountTable;
