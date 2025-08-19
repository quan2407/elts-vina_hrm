import React from "react";
import "../styles/EmployeeTable.css";

function EmployeeTable({
  employees,
  page,
  totalPages,
  onPageChange,
  onRowClick,
}) {
  return (
    <div className="employee-table-wrapper">
      <div className="employee-table">
        <div className="employee-table-header">
          <div className="employee-header-cell">Mã nhân viên</div>
          <div className="employee-header-cell">Tài khoản</div>
          <div className="employee-header-cell">Họ và tên</div>
          <div className="employee-header-cell">Giới tính</div>
          <div className="employee-header-cell">Ngày sinh</div>
          <div className="employee-header-cell">Ngày vào công ty</div>
          <div className="employee-header-cell">Số điện thoại</div>
          <div className="employee-header-cell">Phòng ban</div>
          <div className="employee-header-cell">Chuyền sản xuất</div>
          <div className="employee-header-cell">Vị trí</div>
          <div className="employee-header-cell">Hành động</div>
        </div>

        {employees.map((emp) => (
          <div
            key={emp.employeeId}
            className="employee-table-row"
          >
            <div className="employee-table-cell">{emp.employeeCode}</div>
            <div className="employee-table-cell">{emp.accountUsername}</div>
            <div className="employee-table-cell">{emp.employeeName}</div>
            <div className="employee-table-cell">{emp.gender}</div>
            <div className="employee-table-cell">{emp.dob}</div>
            <div className="employee-table-cell">{emp.startWorkAt}</div>
            <div className="employee-table-cell">{emp.phoneNumber}</div>
            <div className="employee-table-cell">{emp.departmentName}</div>
            <div className="employee-table-cell">{emp.lineName}</div>
            <div className="employee-table-cell">{emp.positionName}</div>
            <div className="employee-table-cell">
              <button
                className="employee-detail-btn"
                onClick={() => onRowClick(emp.employeeId)}
              >
                Xem chi tiết
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className="employee-pagination-container">
        <button
          className="employee-pagination-btn"
          onClick={() => onPageChange(0)}
          disabled={page === 0}
        >
          «
        </button>
        <button
          className="employee-pagination-btn"
          onClick={() => onPageChange(page - 1)}
          disabled={page === 0}
        >
          ‹
        </button>

        {Array.from({ length: totalPages }).map((_, p) => (
          <button
            key={p}
            onClick={() => onPageChange(p)}
            className={`employee-pagination-btn ${
              p === page ? "employee-pagination-active" : ""
            }`}
          >
            {p + 1}
          </button>
        ))}

        <button
          className="employee-pagination-btn"
          onClick={() => onPageChange(page + 1)}
          disabled={page === totalPages - 1}
        >
          ›
        </button>
        <button
          className="employee-pagination-btn"
          onClick={() => onPageChange(totalPages - 1)}
          disabled={page === totalPages - 1}
        >
          »
        </button>
      </div>
    </div>
  );
}

export default EmployeeTable;
