import React, { useState } from "react";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import "../assets/styles/EmployeeDetails.css";

function EmployeeDetails() {
  const [birthDate, setBirthDate] = useState(new Date("1990-01-01"));
  const [issueDate, setIssueDate] = useState(new Date("2015-01-01"));
  const [expiryDate, setExpiryDate] = useState(new Date("2030-01-01"));

  const CustomInput = React.forwardRef(({ value, onClick }, ref) => (
    <input
      className="employeedetail-input-field"
      onClick={onClick}
      value={value}
      readOnly
      ref={ref}
    />
  ));

  return (
    <div className="employeedetail-page">
      <Sidebar />
      <div className="employeedetail-main-content">
        <Header />
        <div className="employeedetail-form-container">
          <div className="employeedetail-form-navigation">
            <div className="employeedetail-nav-item employeedetail-active">
              Thông tin cơ bản
            </div>
            <div className="employeedetail-nav-item">Thông tin liên hệ</div>
            <div className="employeedetail-nav-item">Thông tin công việc</div>
            <div className="employeedetail-nav-item">Thông tin tài khoản</div>
          </div>

          <div className="employeedetail-form-content">
            <div className="employeedetail-basic-information">
              <div className="employeedetail-form-title">Thông tin cơ bản</div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Mã nhân viên</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="NV001"
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Họ và tên</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="Nguyễn Văn A"
                  />
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Giới tính</div>
                  <select
                    className="employeedetail-input-field"
                    defaultValue="Nam"
                  >
                    <option>Nam</option>
                    <option>Nữ</option>
                    <option>Khác</option>
                  </select>
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Ngày sinh</div>
                  <DatePicker
                    selected={birthDate}
                    onChange={(date) => setBirthDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                  />
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Nơi sinh</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="Hà Nội"
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Nguyên quán</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="Nam Định"
                  />
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Quốc tịch</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="Việt Nam"
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Số CCCD</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="0123456789"
                  />
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Ngày cấp</div>
                  <DatePicker
                    selected={issueDate}
                    onChange={(date) => setIssueDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Ngày hết hạn</div>
                  <DatePicker
                    selected={expiryDate}
                    onChange={(date) => setExpiryDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                  />
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Nơi cấp</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    defaultValue="Cục Cảnh sát QLHC"
                  />
                </div>
              </div>
            </div>

            <div className="employeedetail-contact-information">
              <div className="employeedetail-form-title">Thông tin liên hệ</div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Số điện thoại
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="tel"
                    defaultValue="0123 456 789"
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Email</div>
                  <input
                    className="employeedetail-input-field"
                    type="email"
                    defaultValue="email@example.com"
                  />
                </div>
              </div>
            </div>

            <div className="employeedetail-account-information">
              <div className="employeedetail-form-title">
                Thông tin tài khoản
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Email</div>
                  <input
                    className="employeedetail-input-field"
                    type="email"
                    defaultValue="email@example.com"
                    readOnly
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Trạng thái tài khoản
                  </div>
                  <div className="employeedetail-input-field">
                    Đang hoạt động
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EmployeeDetails;
