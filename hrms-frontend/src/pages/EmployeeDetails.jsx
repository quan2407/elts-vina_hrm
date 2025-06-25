import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import "../styles/EmployeeDetails.css";
import employeeService from "../services/employeeService";
import departmentService from "../services/departmentService";
import { Save } from "lucide-react";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

function EmployeeDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
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
  const [startWorkAt, setStartWorkAt] = useState(null);
  const [departmentId, setDepartmentId] = useState("");
  const [positionId, setPositionId] = useState("");
  const [lineId, setLineId] = useState("");

  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);
  const [errors, setErrors] = useState({});
  const [activeSection, setActiveSection] = useState("basic-info");

  const isClickScrolling = useRef(false);
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

  const confirmDelete = async () => {
    const result = await Swal.fire({
      title: "Bạn có chắc chắn?",
      text: "Hành động này không thể hoàn tác!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      try {
        await employeeService.deleteEmployee(id);
        await Swal.fire(
          "Đã xóa!",
          "Nhân viên đã được xóa thành công.",
          "success"
        );
        navigate("/employee-management");
      } catch (err) {
        console.error("Lỗi xóa nhân viên:", err);
        await Swal.fire("Lỗi!", "Có lỗi xảy ra khi xóa nhân viên.", "error");
      }
    }
  };

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
    const fetchEmployeeDetail = async () => {
      try {
        const res = await employeeService.getEmployeeById(id);
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
        setPhone(data.phoneNumber || "");
        setEmail(data.email || "");
        setStartWorkAt(data.startWorkAt ? new Date(data.startWorkAt) : null);
        setDepartmentId(data.departmentId || "");
        setPositionId(data.positionId || "");
        setLineId(data.lineId || "");
      } catch (err) {
        console.error("Lỗi load chi tiết nhân viên:", err);
      }
    };

    fetchEmployeeDetail();
  }, [id]);

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
          <h1 className="page-title">HỒ SƠ NHÂN VIÊN</h1>
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
              <div className="employeedetail-form-title">Thông tin cơ bản</div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Mã nhân viên<span className="required-star">*</span>
                  </div>
                  <input
                    className={`employeedetail-input-field disabled-input`}
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
                  <div className="employeedetail-input-label">Ngày sinh</div>
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
                  <div className="employeedetail-input-label">Nơi sinh</div>
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
                  <div className="employeedetail-input-label">Nguyên quán</div>
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
                  <div className="employeedetail-input-label">Quốc tịch</div>
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
                  <div className="employeedetail-input-label">Số CCCD</div>
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
                  <div className="employeedetail-input-label">Ngày cấp</div>
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
                  <div className="employeedetail-input-label">Ngày hết hạn</div>
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
                  <div className="employeedetail-input-label">Địa chỉ</div>
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
                    Ngày vào công ty
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
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Chuyền sản xuất
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={lineId}
                    onChange={(e) => setLineId(e.target.value)}
                    disabled={lines.length === 0} // Disable khi không có line
                  >
                    {lines.length === 0 ? (
                      <option value="">Không có chuyền sản xuất</option>
                    ) : (
                      <>
                        <option value="">-- Chọn line --</option>
                        {lines.map((l) => (
                          <option
                            key={l.id}
                            value={String(l.id)}
                          >
                            {l.name}
                          </option>
                        ))}
                      </>
                    )}
                  </select>

                  {errors.lineId && (
                    <div className="error-message">
                      {errors.lineId.join(", ")}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="employeedetail-form-actions">
              <button
                className="delete-button"
                onClick={confirmDelete}
                style={{
                  backgroundColor: "#ff4d4f",
                  color: "#fff",
                  marginRight: "8px",
                  padding: "10px 20px",
                  fontSize: "16px",
                  display: "inline-flex",
                  alignItems: "center",
                  border: "none",
                  borderRadius: "8px",
                  fontWeight: 500,
                  cursor: "pointer",
                  transition: "background-color 0.2s ease",
                }}
              >
                Xóa nhân viên
              </button>

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
    </MainLayout>
  );
}

export default EmployeeDetails;
