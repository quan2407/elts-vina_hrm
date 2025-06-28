import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import { Save } from "lucide-react";
import "../styles/EmployeeDetails.css";
import { createInterview } from "../services/interviewScheduleService";
import { getInterviewByCandidateRecruitmentId } from "../services/interviewScheduleService";
import departmentService from "../services/departmentService";
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import employeeService from "../services/employeeService";
import dayjs from "dayjs";


function InterviewCreate() {
  const { id } = useParams();
  const [interview, setInterview] = useState(null);
  const [scheduledAt, setScheduleAt] = useState(null);
  const [feedback, setFeedback] = useState("");
  const [interviewerId, setInterviewerId] = useState(null);
  const [interviewers, setInterviewers] = useState([]);
  const [status, setStatus] = useState("PENDING");
  const [errors, setErrors] = useState({});
  const [activeSection, setActiveSection] = useState("basic-info");

  useEffect(() => {
    const fetchInterview = async () => {
      try {
        const data = await getInterviewByCandidateRecruitmentId(id);
        setInterview(data);
      }
      catch (error) {
        console.error("Lỗi khi lấy thông tin phỏng vấn:", error);
      }
    };
    fetchInterview();
  }, [id]);

  useEffect(() => {
    const fetchInterviewers = async () => {
      if (interview?.recruitmentDepartment) {
        try {
          const departments = await departmentService.getAllDepartments();
          const matchedDept = departments.data.find(
            (d) => d.name === interview.recruitmentDepartment
          );
          if (matchedDept) {
            const employeeRes = await employeeService.getEmployeesByDepartmentId(matchedDept.id);
            setInterviewers(employeeRes.data);
          }
        } catch (err) {
          console.error("Lỗi khi lấy danh sách nhân viên phòng ban:", err);
        }
      }
    };
    fetchInterviewers();
  }, [interview]);

  const handleSubmit = async () => {



    const payload = {
      candidateId: interview?.candidateId,
      recruitmentId: interview?.recruitmentId,
      scheduledAt: scheduledAt ? dayjs(scheduledAt).toISOString() : null,
      feedback: feedback?.trim() === "" ? null : feedback,
      status: status,
      interviewerId: interviewerId
    };

    console.log(" Payload gửi đi:", payload);

    try {
      await createInterview(payload);
      alert("Tạo lịch phỏng vấn thành công!");
      setErrors({});
    } catch (err) {

      console.error("Lỗi tạo lịch phỏng vấn:", err);
      if (err.response && err.response.data) {
        const rawErrors = err.response.data;
        const normalizedErrors = {};

        for (const key in rawErrors) {
          normalizedErrors[key] = Array.isArray(rawErrors[key])
            ? rawErrors[key]
            : [rawErrors[key]];
        }

        setErrors(normalizedErrors);
      } else {
        alert("Có lỗi xảy ra khi tạo lịch phỏng vấn!");
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
          <h1 className="page-title">NHẬP LỊCH PHỎNG VẤN</h1>
        </div>

        <div className="employeedetail-form-container">
          <div className="employeedetail-form-navigation">
            <div
              className={`employeedetail-nav-item ${activeSection === "basic-info" ? "employeedetail-active" : ""
                }`}
              onClick={() => scrollToSection("basic-info")}
            >
              Thông tin ứng viên
            </div>
            <div
              className={`employeedetail-nav-item ${activeSection === "contact-info" ? "employeedetail-active" : ""
                }`}
              onClick={() => scrollToSection("contact-info")}
            >
              Thông tin tuyển dụng
            </div>
            <div
              className={`employeedetail-nav-item ${activeSection === "job-info" ? "employeedetail-active" : ""
                }`}
              onClick={() => scrollToSection("job-info")}
            >
              Lịch phỏng vấn
            </div>
          </div>

          <div className="employeedetail-form-content">
            <div
              id="basic-info"
              className="employeedetail-basic-information"
            >
              <div className="employeedetail-form-title">              Thông tin ứng viên
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Họ và tên ứng viên
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={interview?.candidateName || ""}
                    readOnly
                  />

                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Giới tính
                  </div>
                  <input
                    className="employeedetail-input-field"
                    value={interview?.candidateGender || ""}
                    readOnly          >
                  </input>

                </div>
              </div>

              <div className="employeedetail-form-row">

                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Ngày sinh</div>
                  <DatePicker
                    selected={interview?.dob ? new Date(interview.dob) : null}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                    readOnly
                  />

                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Email</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={interview?.candidateEmail || ""}
                    readOnly
                  />

                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Số điện thoại</div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={interview?.candidatePhone || ""}
                    readOnly
                  />

                </div>
              </div>
            </div>

            <div
              id="contact-info"
              className="employeedetail-contact-information"
            >
              <div className="employeedetail-form-title">Thông tin tuyển dụng</div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Nội dung tuyển dụng
                  </div>
                  <input
                    className="employeedetail-input-field"
                    type="text"
                    value={interview?.recruitmentTitle || ""}
                    readOnly
                  />
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Loại hình công việc</div>
                  <input
                    className="employeedetail-input-field"
                    type="email"
                    value={interview?.employmentType || ""}
                    readOnly
                  />

                </div>
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Mô tả công việc</div>
                  <textarea
                    className="employeedetail-input-field"
                    value={interview?.recruitmentDescription || ""} readOnly
                  />
                </div>
              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Yêu cầu tuyển dụng</div>
                  <textarea
                    className="employeedetail-input-field"
                    value={interview?.jobRequirement || ""} readOnly

                  />
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Quyền lợi</div>
                  <textarea
                    className="employeedetail-input-field"
                    type="text"
                    value={interview?.benefits || ""} readOnly
                  />

                </div>
              </div>
              <div className="employeedetail-form-row">

                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Phòng ban<span className="required-star">*</span>
                  </div>
                  <input className="employeedetail-input-field"
                    value={interview?.recruitmentDepartment || ""} readOnly />

                </div>
              </div>
            </div>

            <div
              id="job-info"
              className="employeedetail-job-information"
            >
              <div className="employeedetail-form-title">
                Lịch phỏng vấn
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Thời gian phỏng vấn<span className="required-star">*</span>
                  </div>
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <DateTimePicker
                      placeholderText="Chọn ngày"
                      value={scheduledAt}
                      onChange={(newValue) => setScheduleAt(newValue)}
                      customInput={<CustomInput />}
                      format="DD/MM/YYYY HH:mm"
                      ampm={true}
                      slotProps={{
                        textField: {
                          sx: { padding: 2.5 },
                          variant: "standard",
                          InputProps: {
                            disableUnderline: true,
                          },
                          className: "employeedetail-input-field",
                          required: true,
                        },
                      }}
                    />
                  </LocalizationProvider>


                  {errors.scheduledAt && (
                    <div className="error-message">
                      {errors.scheduledAt.join(", ")}
                    </div>
                  )}
                </div>

              </div>
              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Feedback ứng viên</div>
                  <textarea
                    className="employeedetail-input-field"
                    type="text"
                    value={feedback}
                    onChange={(e) => setFeedback(e.target.value)}
                    placeholder="Nhập feedback về ứng viên"
                  />
                  {errors.feedback && (
                    <div className="error-message">
                      {errors.feedback.join(", ")}
                    </div>
                  )}
                </div>
              </div>

              <div className="employeedetail-form-row">
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Người phỏng vấn<span className="required-star">*</span>
                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={interviewerId}
                    onChange={(e) => {
                      const value = e.target.value;
    setInterviewerId(value === "" ? null : Number(value)); // ✅ null nếu chưa chọn
                    }}
                  >
                    <option value="">-- Chọn người phỏng vấn --</option>
                    {interviewers.map((p) => (
                      <option
                        key={p.employeeId}
                        value={p.employeeId}
                      >
                        {p.employeeName}
                      </option>
                    ))}
                  </select>
                  {errors.interviewerId && (
                    <div className="error-message">
                      {errors.interviewerId.join(", ")}
                    </div>
                  )}
                </div>
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">
                    Trạng thái phỏng vấn                  </div>
                  <select
                    className="employeedetail-input-field"
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                  >
                    <option value="PENDING">Đang chờ</option>
                    <option value="COMPLETED">Đã phỏng vấn</option>
                    <option value="CANCLE">Đã từ chối</option>
                  </select>
                  {errors.status && (
                    <div className="error-message">
                      {errors.status.join(", ")}
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
                Lưu lịch phỏng vấn
              </button>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default InterviewCreate;
