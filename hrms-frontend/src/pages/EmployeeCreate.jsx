import React, { useState, useEffect, useRef } from "react";
import MainLayout from "../components/MainLayout";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import { Save } from "lucide-react";
import "../styles/EmployeeDetails.css";
import employeeService from "../services/employeeService";
import CCCDModal from "../components/CCCDModal";

import departmentService from "../services/departmentService";
import { format } from "date-fns";
import Swal from "sweetalert2";

function EmployeeCreate() {
  const [employeeCode, setEmployeeCode] = useState("");
  const [fullName, setFullName] = useState("");
  const [gender, setGender] = useState("");
  const [birthDate, setBirthDate] = useState(null);
  const [birthPlace, setBirthPlace] = useState("");
  const [originPlace, setOriginPlace] = useState("");
  const [departmentId, setDepartmentId] = useState("");
  const [positionId, setPositionId] = useState("");
  const [lineId, setLineId] = useState("");
  const [errors, setErrors] = useState({});
  const [showOcrModal, setShowOcrModal] = useState(false);

  const [nationality, setNationality] = useState("");
  const [idNumber, setIdNumber] = useState("");
  const [issueDate, setIssueDate] = useState(null);
  const [expiryDate, setExpiryDate] = useState(null);
  const [startWorkAt, setStartWorkAt] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);
  const [currentAddress, setCurrentAddress] = useState("");
  const [ethnicity, setEthnicity] = useState("");
  const [religion, setReligion] = useState("");
  const [educationLevel, setEducationLevel] = useState("");
  const [specializedLevel, setSpecializedLevel] = useState("");
  const [trainingType, setTrainingType] = useState("");
  const [trainingMajor, setTrainingMajor] = useState("");
  const [foreignLanguages, setForeignLanguages] = useState("");

  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [activeSection, setActiveSection] = useState("basic-info");
  const resetForm = () => {
    setEmployeeCode("");
    setFullName("");
    setGender("");
    setBirthDate(null);
    setBirthPlace("");
    setOriginPlace("");
    setNationality("");
    setIdNumber("");
    setIssueDate(null);
    setExpiryDate(null);
    setAddress("");
    setCurrentAddress("");
    setEthnicity("");
    setReligion("");
    setEducationLevel("");
    setSpecializedLevel("");
    setTrainingType("");
    setTrainingMajor("");
    setForeignLanguages("");
    setPhone("");
    setEmail("");
    setStartWorkAt(null);
    setDepartmentId("");
    setPositionId("");
    setLineId("");
  };

  const handleSubmit = async () => {
    const payload = {
      employeeCode: employeeCode?.trim() ? employeeCode : null,
      employeeName: fullName?.trim() ? fullName : null,
      gender: gender?.trim() ? gender : null,
      dob: birthDate ? format(birthDate, "yyyy-MM-dd") : null,
      placeOfBirth: birthPlace?.trim() ? birthPlace : null,
      originPlace: originPlace?.trim() ? originPlace : null,
      nationality: nationality?.trim() ? nationality : null,
      citizenId: idNumber?.trim() ? idNumber : null,
      citizenIssueDate: issueDate ? format(issueDate, "yyyy-MM-dd") : null,
      citizenExpiryDate: expiryDate ? format(expiryDate, "yyyy-MM-dd") : null,
      address: address?.trim() ? address : null,
      currentAddress: currentAddress?.trim() || null,
      ethnicity: ethnicity?.trim() || null,
      religion: religion?.trim() || null,
      educationLevel: educationLevel?.trim() || null,
      specializedLevel: specializedLevel?.trim() || null,
      trainingType: trainingType?.trim() || null,
      trainingMajor: trainingMajor?.trim() || null,
      foreignLanguages: foreignLanguages?.trim() || null,
      phoneNumber: phone?.trim() ? phone : null,
      email: email?.trim() ? email : null,
      startWorkAt: startWorkAt ? format(startWorkAt, "yyyy-MM-dd") : null,
      departmentId: departmentId !== "" ? Number(departmentId) : null,
      positionId: positionId !== "" ? Number(positionId) : null,
      lineId: lineId !== "" ? Number(lineId) : null,
    };

    console.log(" Payload gửi đi:", payload);

    try {
      await employeeService.createEmployee(payload);
      alert("Tạo nhân viên thành công!");
      setErrors({});
      resetForm();
    } catch (err) {
      console.error("Lỗi tạo nhân viên:", err);

      if (err.response && err.response.data) {
        console.log("err.response.data:", err.response.data);

        const serverData = err.response.data;

        if (serverData.errors) {
          setErrors(serverData.errors);
        } else if (typeof serverData === "object" && !serverData.message) {
          setErrors(serverData);
        } else if (serverData.message) {
          const fieldErrorMap = [
            { keyword: "CMND/CCCD", field: "citizenId" },
            { keyword: "CMND", field: "citizenId" },
            { keyword: "CCCD", field: "citizenId" },
            { keyword: "Email", field: "email" },
            { keyword: "Số điện thoại", field: "phoneNumber" },
            { keyword: "Mã nhân viên", field: "employeeCode" },
            { keyword: "Chức vụ", field: "positionId" },
            { keyword: "Phòng ban", field: "departmentId" },
            { keyword: "Chuyền sản xuất", field: "lineId" },
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
        } else {
          alert("Server trả về lỗi nhưng không có message!");
        }
      } else {
        alert("Không nhận được phản hồi từ server!");
      }
    }
  };
  const confirmSave = async () => {
    const result = await Swal.fire({
      title: "Xác nhận lưu?",
      text: "Bạn có muốn lưu thay đổi?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Lưu",
      cancelButtonText: "Hủy",
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#aaa",
    });

    if (result.isConfirmed) {
      Swal.fire({
        title: "Đang lưu dữ liệu...",
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading(),
      });

      const success = await handleSubmit();
      Swal.close();
      if (success) {
        await Swal.fire({
          icon: "success",
          title: "Đã lưu thành công!",
          confirmButtonText: "OK",
        });
      }
    }
  };
  useEffect(() => {
    const fetchNextEmployeeCode = async () => {
      try {
        const res = await employeeService.getNextEmployeeCode();
        setEmployeeCode(res.data);
      } catch (err) {
        console.error("Lỗi lấy mã nhân viên:", err);
      }
    };
    fetchNextEmployeeCode();
  }, []);
  useEffect(() => {
    const fetchCode = async () => {
      if (positionId) {
        try {
          const res = await employeeService.getNextEmployeeCodeByPosition(
            positionId
          );
          setEmployeeCode(res.data);
        } catch (err) {
          console.error("Lỗi lấy mã theo vị trí:", err);
        }
      } else {
        try {
          const res = await employeeService.getNextEmployeeCode();
          setEmployeeCode(res.data);
        } catch (err) {
          console.error("Lỗi fallback mã nhân viên:", err);
        }
      }
    };

    fetchCode();
  }, [positionId]);

  useEffect(() => {
    const contentEl = document.querySelector(".employeedetail-form-content");
    const handleScroll = () => {
      if (isClickScrolling.current) return;
      const sections = ["basic-info", "contact-info", "job-info"];
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

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const res = await departmentService.getAllDepartments();
        setDepartments(res.data);
      } catch (err) {
        console.error("Lỗi load phòng ban:", err);
      }
    };
    fetchDepartments();
  }, []);

  useEffect(() => {
    if (departmentId) {
      const fetchPosLine = async () => {
        try {
          const [posRes, lineRes] = await Promise.all([
            departmentService.getPositionsByDepartment(departmentId),
            departmentService.getLinesByDepartment(departmentId),
          ]);
          setPositions(posRes.data);
          setLines(lineRes.data);
        } catch (err) {
          console.error("Lỗi load vị trí hoặc line:", err);
        }
      };
      fetchPosLine();
    } else {
      setPositions([]);
      setLines([]);
    }
  }, [departmentId]);

  const isClickScrolling = useRef(false);

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
          <h1 className="page-title">NHẬP HỒ SƠ NHÂN VIÊN</h1>
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
            <div
              className={`employeedetail-nav-item ${
                activeSection === "job-info" ? "employeedetail-active" : ""
              }`}
              onClick={() => scrollToSection("job-info")}
            >
              Thông tin công việc
            </div>
          </div>

          <div className="employeedetail-form-content">
            <div
              id="basic-info"
              className="employeedetail-basic-information"
            >
              <div
                className="employeedetail-form-title"
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <span>Thông tin cơ bản</span>
                <button
                  type="button"
                  onClick={() => setShowOcrModal(true)}
                  className="submit-button"
                  style={{ padding: "4px 8px", fontSize: "14px" }}
                >
                  📷 Trích xuất CCCD
                </button>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Mã nhân viên<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field disabled-input"
                    type="text"
                    value={employeeCode}
                    placeholder="Nhập mã nhân viên"
                    onChange={(e) => setEmployeeCode(e.target.value)}
                    disabled
                  />
                  {errors.employeeCode && (
                    <div className="error-message">
                      {errors.employeeCode.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Họ và tên<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={fullName}
                    placeholder="Nhập họ và tên"
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
                  <div className="employeedetail-input-label">
                    Giới tính<span className="required-star">*</span>
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                  >
                    <option value="">-- Chọn giới tính --</option>
                    <option>NAM</option>
                    <option>NỮ</option>
                  </select>
                  {errors.gender && (
                    <div className="error-message">
                      {errors.gender.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Ngày sinh<span className="required-star">*</span>
                  </div>
                  <DatePicker
                    selected={birthDate}
                    onChange={(date) => setBirthDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                    placeholderText="Chọn ngày"
                    showMonthDropdown
                    showYearDropdown
                    dropdownMode="select"
                  />
                  {errors.dob && (
                    <div className="error-message">{errors.dob.join(", ")}</div>
                  )}
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
              {/* Dân tộc & Tôn giáo */}
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Dân tộc<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={ethnicity}
                    placeholder="Nhập dân tộc"
                    onChange={(e) => setEthnicity(e.target.value)}
                  />
                  {errors.ethnicity && (
                    <div className="error-message">
                      {errors.ethnicity.join(", ")}
                    </div>
                  )}
                </div>

                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Tôn giáo<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={religion}
                    placeholder="Nhập tôn giáo"
                    onChange={(e) => setReligion(e.target.value)}
                  />
                  {errors.religion && (
                    <div className="error-message">
                      {errors.religion.join(", ")}
                    </div>
                  )}
                </div>
              </div>

              {/* Nơi ở hiện nay & Trình độ văn hóa */}
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Nơi ở hiện nay<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={currentAddress}
                    placeholder="Nhập nơi ở hiện nay"
                    onChange={(e) => setCurrentAddress(e.target.value)}
                  />
                  {errors.currentAddress && (
                    <div className="error-message">
                      {errors.currentAddress.join(", ")}
                    </div>
                  )}
                </div>

                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Trình độ văn hóa<span className="required-star">*</span>
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={educationLevel}
                    onChange={(e) => setEducationLevel(e.target.value)}
                  >
                    <option value="">-- Chọn trình độ văn hóa --</option>
                    <option value="5/12">5/12</option>
                    <option value="9/12">9/12</option>
                    <option value="12/12">12/12</option>
                    <option value="Trung cấp">Trung cấp</option>
                    <option value="Cao đẳng">Cao đẳng</option>
                    <option value="Đại học">Đại học</option>
                    <option value="Sau đại học">Sau đại học</option>
                  </select>

                  {errors.educationLevel && (
                    <div className="error-message">
                      {errors.educationLevel.join(", ")}
                    </div>
                  )}
                </div>
              </div>

              {/* Trình độ chuyên môn & Ngoại ngữ */}
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Trình độ chuyên môn<span className="required-star">*</span>
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={specializedLevel}
                    onChange={(e) => setSpecializedLevel(e.target.value)}
                  >
                    <option value="">-- Chọn trình độ chuyên môn --</option>
                    <option value="Sơ cấp">Sơ cấp</option>
                    <option value="Trung cấp">Trung cấp</option>
                    <option value="Cao đẳng">Cao đẳng</option>
                    <option value="Đại học">Đại học</option>
                    <option value="Thạc sĩ">Thạc sĩ</option>
                    <option value="Tiến sĩ">Tiến sĩ</option>
                  </select>

                  {errors.specializedLevel && (
                    <div className="error-message">
                      {errors.specializedLevel.join(", ")}
                    </div>
                  )}
                </div>

                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Ngoại ngữ</div>
                  <select
                    className="employeedetail-input-field"
                    value={foreignLanguages}
                    onChange={(e) => setForeignLanguages(e.target.value)}
                  >
                    <option value="">-- Chọn ngoại ngữ --</option>
                    <option value="Tiếng Anh">Tiếng Anh</option>
                    <option value="Tiếng Trung">Tiếng Trung</option>
                    <option value="Tiếng Nhật">Tiếng Nhật</option>
                    <option value="Tiếng Hàn">Tiếng Hàn</option>
                    <option value="Khác">Khác</option>
                  </select>

                  {errors.foreignLanguages && (
                    <div className="error-message">
                      {errors.foreignLanguages.join(", ")}
                    </div>
                  )}
                </div>
              </div>

              {/* Loại hình đào tạo & Chuyên ngành đào tạo */}
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Loại hình đào tạo
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={trainingType}
                    onChange={(e) => setTrainingType(e.target.value)}
                  >
                    <option value="">-- Chọn loại hình đào tạo --</option>
                    <option value="Chính quy">Chính quy</option>
                    <option value="Tại chức">Tại chức</option>
                    <option value="Liên thông">Liên thông</option>
                    <option value="Khác">Khác</option>
                  </select>

                  {errors.trainingType && (
                    <div className="error-message">
                      {errors.trainingType.join(", ")}
                    </div>
                  )}
                </div>

                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Chuyên ngành đào tạo<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={trainingMajor}
                    placeholder="Nhập chuyên ngành đào tạo"
                    onChange={(e) => setTrainingMajor(e.target.value)}
                  />
                  {errors.trainingMajor && (
                    <div className="error-message">
                      {errors.trainingMajor.join(", ")}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div
              id="contact-info"
              className="employeedetail-contact-information"
            >
              <div className="employeedetail-form-title">
                Thông tin liên hệ<span className="required-star">*</span>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Số điện thoại
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="tel"
                    value={phone}
                    placeholder="Nhập số điện thoại"
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
                    placeholder="Nhập email liên hệ"
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
                  <div className="employeedetail-input-label">
                    Địa chỉ<span className="required-star">*</span>
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={address}
                    placeholder="Nhập địa chỉ"
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

            <div
              id="job-info"
              className="employeedetail-job-information"
            >
              <div className="employeedetail-form-title">
                Thông tin công việc
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Ngày vào công ty<span className="required-star">*</span>
                  </div>
                  <DatePicker
                    selected={startWorkAt}
                    onChange={(date) => setStartWorkAt(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                    placeholderText="Chọn ngày"
                    showMonthDropdown
                    showYearDropdown
                    dropdownMode="select"
                  />
                  {errors.startWorkAt && (
                    <div className="error-message">
                      {errors.startWorkAt.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Phòng ban<span className="required-star">*</span>
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={departmentId}
                    onChange={(e) => setDepartmentId(e.target.value)}
                  >
                    <option value="">-- Chọn phòng ban --</option>
                    {departments.map((d) => (
                      <option
                        key={d.id}
                        value={d.id}
                      >
                        {d.name}
                      </option>
                    ))}
                  </select>
                  {errors.departmentId && (
                    <div className="error-message">
                      {errors.departmentId.join(", ")}
                    </div>
                  )}
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Vị trí<span className="required-star">*</span>
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={positionId}
                    onChange={(e) => setPositionId(e.target.value)}
                  >
                    <option value="">-- Chọn vị trí --</option>
                    {positions.map((p) => (
                      <option
                        key={p.id}
                        value={p.id}
                      >
                        {p.name}
                      </option>
                    ))}
                  </select>
                  {errors.positionId && (
                    <div className="error-message">
                      {errors.positionId.join(", ")}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="employeedetail-form-actions">
              <button
                className="submit-button"
                onClick={confirmSave}
              >
                <Save
                  size={16}
                  style={{ marginRight: "8px" }}
                />
                Lưu nhân viên
              </button>
            </div>
          </div>
        </div>
      </div>
      {showOcrModal && (
        <CCCDModal
          onClose={() => setShowOcrModal(false)}
          onApply={(ocrData) => {
            setFullName(ocrData.employeeName || "");
            setGender((ocrData.gender || "").toUpperCase());

            setBirthDate(ocrData.dob ? new Date(ocrData.dob) : null);
            setBirthPlace(ocrData.placeOfBirth || "");
            setOriginPlace(ocrData.originPlace || "");
            setNationality(ocrData.nationality || "");
            setIdNumber(ocrData.citizenId || "");
            setIssueDate(
              ocrData.citizenIssueDate
                ? new Date(ocrData.citizenIssueDate)
                : null
            );
            setExpiryDate(
              ocrData.citizenExpiryDate
                ? new Date(ocrData.citizenExpiryDate)
                : null
            );
            setAddress(ocrData.address || "");
            setShowOcrModal(false);
          }}
        />
      )}
    </MainLayout>
  );
}

export default EmployeeCreate;
