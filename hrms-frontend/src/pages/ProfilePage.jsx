import React, { useState, useEffect, useRef } from "react";
import MainLayout from "../components/MainLayout";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import "../styles/EmployeeDetails.css";
import employeeService from "../services/employeeService";
import { Save } from "lucide-react";

function ProfilePage() {
  const [employeeCode, setEmployeeCode] = useState("");
  const [fullName, setFullName] = useState("");
  const [gender, setGender] = useState("");
  const [birthDate, setBirthDate] = useState(null);
  const [birthPlace, setBirthPlace] = useState("");
  const [originPlace, setOriginPlace] = useState("");
  const [nationality, setNationality] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [issueDate, setIssueDate] = useState(null);
  const [expiryDate, setExpiryDate] = useState(null);
  const [address, setAddress] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [currentAddress, setCurrentAddress] = useState("");
  const [ethnicity, setEthnicity] = useState("");
  const [religion, setReligion] = useState("");
  const [educationLevel, setEducationLevel] = useState("");
  const [specializedLevel, setSpecializedLevel] = useState("");
  const [foreignLanguages, setForeignLanguages] = useState("");
  const [trainingType, setTrainingType] = useState("");
  const [trainingMajor, setTrainingMajor] = useState("");

  const [startWorkAt, setStartWorkAt] = useState(null);
  const [departmentId, setDepartmentId] = useState("");
  const [positionId, setPositionId] = useState("");
  const [lineId, setLineId] = useState("");
  const [endWorkAt, setEndWorkAt] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);
  const [errors, setErrors] = useState({});
  const [activeSection, setActiveSection] = useState("basic-info");
  const isClickScrolling = useRef(false);

  const handleSubmit = async () => {
    const payload = {
      employeeName: fullName?.trim() || null,
      phoneNumber: phone?.trim() || null,
      email: email?.trim() || null,
      address: address?.trim() || null,
    };

    console.log("📌 Payload gửi đi:", payload);

    try {
      await employeeService.updateOwnProfile(payload);
      alert("Cập nhật thông tin cá nhân thành công!");

      const res = await employeeService.getOwnProfile();
      const data = res.data;

      setFullName(data.employeeName || "");
      setPhone(data.phoneNumber || "");
      setEmail(data.email || "");
      setAddress(data.address || "");
      setEmployeeCode(data.employeeCode || "");
      setGender(data.gender || "");
      setBirthDate(data.dob ? new Date(data.dob) : null);
      setBirthPlace(data.placeOfBirth || "");
      setOriginPlace(data.originPlace || "");
      setNationality(data.nationality || "");
      setIdNumber(data.citizenId || "");
      setIssueDate(
        data.citizenIssueDate ? new Date(data.citizenIssueDate) : null
      );
      setExpiryDate(
        data.citizenExpiryDate ? new Date(data.citizenExpiryDate) : null
      );

      setErrors({});
    } catch (err) {
      console.error("❌ Lỗi cập nhật profile:", err);
      if (err.response && err.response.data) {
        const serverData = err.response.data;
        if (serverData.errors) {
          setErrors(serverData.errors);
        } else if (serverData.message) {
          const fieldErrorMap = [
            { keyword: "CMND", field: "citizenId" },
            { keyword: "CCCD", field: "citizenId" },
            { keyword: "Email", field: "email" },
            { keyword: "Số điện thoại", field: "phoneNumber" },
          ];
          const matched = fieldErrorMap.find((rule) =>
            serverData.message.includes(rule.keyword)
          );
          if (matched) {
            setErrors((prev) => ({
              ...prev,
              [matched.field]: [serverData.message],
            }));
          } else {
            alert(serverData.message);
          }
        }
      } else {
        alert("Có lỗi xảy ra khi cập nhật thông tin cá nhân!");
      }
    }
  };

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await employeeService.getOwnProfile();
        const data = res.data;

        setEmployeeCode(data.employeeCode || "");
        setFullName(data.employeeName || "");
        setGender(data.gender || "");
        setBirthDate(data.dob ? new Date(data.dob) : null);
        setBirthPlace(data.placeOfBirth || "");
        setOriginPlace(data.originPlace || "");
        setNationality(data.nationality || "");
        setIdNumber(data.citizenId || "");
        setIssueDate(
          data.citizenIssueDate ? new Date(data.citizenIssueDate) : null
        );
        setExpiryDate(
          data.citizenExpiryDate ? new Date(data.citizenExpiryDate) : null
        );
        setAddress(data.address || "");
        setEthnicity(data.ethnicity || "");
        setPhone(data.phoneNumber || "");
        setEmail(data.email || "");
        setStartWorkAt(data.startWorkAt ? new Date(data.startWorkAt) : null);
        setDepartmentId(data.departmentId || "");
        setPositionId(data.positionId || "");
        setLineId(data.lineId || "");
        setEndWorkAt(data.endWorkAt ? new Date(data.endWorkAt) : null);
      } catch (err) {
        console.error("❌ Lỗi load profile:", err);
      }
    };

    fetchProfile();
  }, []);

  useEffect(() => {
    const contentEl = document.querySelector(".employeedetail-form-content");
    const handleScroll = () => {
      if (isClickScrolling.current) return;
      const sections = ["basic-info", "contact-info"];
      for (let id of sections) {
        const el = document.getElementById(id);
        if (el) {
          const rect = el.getBoundingClientRect();
          if (rect.top >= 150 && rect.top < window.innerHeight / 2) {
            setActiveSection(id);
            break;
          }
        }
      }
    };
    if (contentEl) {
      contentEl.addEventListener("scroll", handleScroll);
      return () => contentEl.removeEventListener("scroll", handleScroll);
    }
  }, []);

  const scrollToSection = (id) => {
    const el = document.getElementById(id);
    if (el) {
      isClickScrolling.current = true;
      el.scrollIntoView({ behavior: "smooth", block: "start" });
      setActiveSection(id);
      setTimeout(() => {
        isClickScrolling.current = false;
      }, 500);
    }
  };

  const CustomInput = React.forwardRef(
    ({ value, onClick, placeholder }, ref) => (
      <input
        className="employeedetail-input-field"
        onClick={onClick}
        value={value || ""}
        placeholder={placeholder}
        readOnly
        ref={ref}
      />
    )
  );

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Thông tin cá nhân</h1>
        </div>

        <div className="employeedetail-form-container">
          <div className="employeedetail-form-navigation">
            <div
              className={`employeedetail-nav-item ${
                activeSection === "basic-info" ? "employeedetail-active" : ""
              }`}
              onClick={() => scrollToSection("basic-info")}
            >
              Thông tin cơ bản
            </div>
            <div
              className={`employeedetail-nav-item ${
                activeSection === "contact-info" ? "employeedetail-active" : ""
              }`}
              onClick={() => scrollToSection("contact-info")}
            >
              Thông tin liên hệ
            </div>
          </div>

          <div className="employeedetail-form-content">
            <div
              id="basic-info"
              className="employeedetail-basic-information"
            >
              <div className="employeedetail-form-title">Thông tin cơ bản</div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Mã nhân viên<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field disabled-input"
                    type="text"
                    value={employeeCode}
                    disabled
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Họ và tên<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={fullName}
                    readOnly
                    onChange={(e) => setFullName(e.target.value)}
                  />
                  {errors.employeeName && (
                    <div className="error-message">
                      {errors.employeeName.join(", ")}
                    </div>
                  )}
                </div>
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Giới tính</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={gender}
                    disabled
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Ngày sinh</div>
                  <DatePicker
                    selected={birthDate}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    readOnly
                    customInput={<CustomInput />}
                    disabled
                  />
                </div>
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Nơi sinh<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={birthPlace}
                    readOnly
                    placeholder="Nhập nơi sinh"
                    onChange={(e) => setBirthPlace(e.target.value)}
                  />
                  {errors.placeOfBirth && (
                    <div className="error-message">
                      {errors.placeOfBirth.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Nguyên quán<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={originPlace}
                    readOnly
                    placeholder="Nhập nguyên quán"
                    onChange={(e) => setOriginPlace(e.target.value)}
                  />
                  {errors.originPlace && (
                    <div className="error-message">
                      {errors.originPlace.join(", ")}
                    </div>
                  )}
                </div>
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Quốc tịch<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={nationality}
                    readOnly
                    placeholder="Nhập quốc tịch"
                    onChange={(e) => setNationality(e.target.value)}
                  />
                  {errors.nationality && (
                    <div className="error-message">
                      {errors.nationality.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Số CCCD<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={idNumber}
                    readOnly
                    placeholder="Nhập số CCCD"
                    onChange={(e) => setIdNumber(e.target.value)}
                  />
                  {errors.citizenId && (
                    <div className="error-message">
                      {errors.citizenId.join(", ")}
                    </div>
                  )}
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Ngày cấp<span className="required-star">*</span>
                  </div>
                  <DatePicker
                    selected={issueDate}
                    onChange={(date) => setIssueDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    readOnly
                    customInput={<CustomInput />}
                    placeholderText="Chọn ngày"
                    showMonthDropdown
                    showYearDropdown
                    dropdownMode="select"
                  />
                  {errors.citizenIssueDate && (
                    <div className="error-message">
                      {errors.citizenIssueDate.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Ngày hết hạn<span className="required-star">*</span>
                  </div>
                  <DatePicker
                    selected={expiryDate}
                    onChange={(date) => setExpiryDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    readOnly
                    customInput={<CustomInput />}
                    placeholderText="Chọn ngày"
                    showMonthDropdown
                    showYearDropdown
                    dropdownMode="select"
                  />
                  {errors.citizenExpiryDate && (
                    <div className="error-message">
                      {errors.citizenExpiryDate.join(", ")}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div
              id="contact-info"
              className="employeedetail-contact-information"
            >
              <div className="employeedetail-form-title">Thông tin liên hệ</div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Số điện thoại<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  />
                  {errors.phoneNumber && (
                    <div className="error-message">
                      {errors.phoneNumber.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Email<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                  {errors.email && (
                    <div className="error-message">
                      {errors.email.join(", ")}
                    </div>
                  )}
                </div>
              </div>
              <div className="employeedetail-form-row">
                <div
                  className="employeedetail-input-group"
                  style={{ width: "100%" }}
                >
                  <div className="employeedetail-input-label">Địa chỉ</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                  {errors.address && (
                    <div className="error-message">
                      {errors.address.join(", ")}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="employeedetail-form-actions">
              <button
                className="submit-button"
                onClick={handleSubmit}
              >
                <Save
                  size={16}
                  style={{ marginRight: "8px" }}
                />
                Lưu thông tin
              </button>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default ProfilePage;
