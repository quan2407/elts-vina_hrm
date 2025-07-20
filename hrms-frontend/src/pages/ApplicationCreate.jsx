import React, { useState, useEffect } from "react";
import MainLayout from "../components/MainLayout";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import { Save } from "lucide-react";
import applicationService from "../services/applicationService";
import "../styles/EmployeeDetails.css";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";

function ApplicationCreate() {
  const queryParams = new URLSearchParams(window.location.search);
  const [attachment, setAttachment] = useState(null);
  const [attachmentPreview, setAttachmentPreview] = useState(null);

  const type = queryParams.get("type"); // 'leave' | 'makeup'
  const date = queryParams.get("date"); // YYYY-MM-DD or null
  const quillModules = {
    toolbar: [
      ["bold", "italic", "underline"], // các nút định dạng chữ
      [{ list: "ordered" }, { list: "bullet" }], // danh sách đánh số & gạch đầu dòng
    ],
  };

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [startDate, setStartDate] = useState(date ? new Date(date) : null);
  const [endDate, setEndDate] = useState(date ? new Date(date) : null);
  const [leaveCode, setLeaveCode] = useState("");
  const [isHalfDay, setIsHalfDay] = useState(false);
  const [halfDayType, setHalfDayType] = useState("MORNING");
  const [errors, setErrors] = useState({});

  const resetForm = () => {
    setTitle("");
    setContent("");
    setStartDate(null);
    setEndDate(null);
    setLeaveCode("");
    setIsHalfDay(false);
    setHalfDayType("MORNING");
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("title", title?.trim() || "");
    formData.append("content", content?.trim() || "");
    formData.append(
      "startDate",
      startDate ? startDate.toISOString().split("T")[0] : ""
    );
    formData.append(
      "endDate",
      endDate ? endDate.toISOString().split("T")[0] : ""
    );
    formData.append("applicationTypeId", type === "makeup" ? 2 : 1);
    formData.append("leaveCode", type === "leave" ? leaveCode : "");
    formData.append("isHalfDay", isHalfDay);
    formData.append("halfDayType", isHalfDay ? halfDayType : "");

    if (attachment) {
      formData.append("attachment", attachment);
    }

    try {
      await applicationService.createApplication(formData);
      alert("Tạo đơn thành công!");
      resetForm();
      setAttachment(null);
    } catch (err) {
      console.error("Lỗi tạo đơn:", err);
      const rawErrors = err.response?.data || {};
      const formatted = {};
      for (const key in rawErrors) {
        formatted[key] = Array.isArray(rawErrors[key])
          ? rawErrors[key]
          : [rawErrors[key]];
      }
      setErrors(formatted);
    }
  };

  const CustomInput = React.forwardRef(function CustomInput(
    { value, onClick, placeholder },
    ref
  ) {
    return (
      <input
        className="employeedetail-input-field"
        onClick={onClick}
        value={value || ""}
        placeholder={placeholder}
        readOnly
        ref={ref}
      />
    );
  });

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">
            TẠO ĐƠN {type === "makeup" ? "BÙ CÔNG" : "NGHỈ PHÉP"}
          </h1>
        </div>

        <div className="employeedetail-form-container">
          <div className="employeedetail-form-content">
            <div className="employeedetail-form-title">Thông tin đơn</div>

            <div className="employeedetail-form-row">
              <div className="employeedetail-input-group">
                <div className="employeedetail-input-label">Tiêu đề đơn</div>
                <input
                  className="employeedetail-input-field"
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Nhập tiêu đề"
                />
                {errors.title && (
                  <div className="error-message">{errors.title.join(", ")}</div>
                )}
              </div>
            </div>

            <div className="employeedetail-form-row">
              <div className="employeedetail-input-group">
                <div className="employeedetail-input-label">Nội dung đơn</div>
                <ReactQuill
                  value={content}
                  onChange={setContent}
                  modules={quillModules}
                  placeholder={
                    type === "leave"
                      ? "Lý do xin nghỉ phép..."
                      : "Lý do xin bù công..."
                  }
                  className="react-quill"
                />

                {errors.content && (
                  <div className="error-message">
                    {errors.content.join(", ")}
                  </div>
                )}
              </div>
            </div>

            <div className="employeedetail-form-row">
              <div className="employeedetail-input-group">
                <div className="employeedetail-input-label">
                  {type === "makeup" ? "Ngày bù công" : "Từ ngày"}
                </div>
                <DatePicker
                  selected={startDate}
                  onChange={(date) => setStartDate(date)}
                  dateFormat="dd/MM/yyyy"
                  locale={vi}
                  customInput={<CustomInput />}
                  placeholderText={
                    type === "makeup"
                      ? "Chọn ngày bù công"
                      : "Chọn ngày bắt đầu"
                  }
                />
              </div>

              {type === "leave" && (
                <div className="employeedetail-input-group">
                  <div className="employeedetail-input-label">Đến ngày</div>
                  <DatePicker
                    selected={endDate}
                    onChange={(date) => setEndDate(date)}
                    dateFormat="dd/MM/yyyy"
                    locale={vi}
                    customInput={<CustomInput />}
                    placeholderText="Chọn ngày kết thúc"
                  />
                </div>
              )}
            </div>

            {type === "leave" && (
              <>
                <div className="employeedetail-form-row">
                  <div className="employeedetail-input-group">
                    <div className="employeedetail-input-label">
                      Mã nghỉ phép
                    </div>
                    <select
                      className="employeedetail-input-field"
                      value={leaveCode}
                      onChange={(e) => setLeaveCode(e.target.value)}
                    >
                      <option value="">-- Chọn mã phép --</option>
                      <option value="P">P (Có lương)</option>
                      <option value="KL">KL (Không lương)</option>
                      <option value="TS">TS (Thai sản)</option>
                      <option value="B">B (Bệnh)</option>
                    </select>
                    {errors.leaveCode && (
                      <div className="error-message">
                        {errors.leaveCode.join(", ")}
                      </div>
                    )}
                  </div>
                </div>

                <div className="employeedetail-form-row">
                  <div className="employeedetail-input-group">
                    <div className="employeedetail-input-label">
                      {type === "makeup" ? "Ngày bù công" : "Từ ngày"}
                    </div>
                    <DatePicker
                      selected={startDate}
                      onChange={(date) => setStartDate(date)}
                      dateFormat="dd/MM/yyyy"
                      locale={vi}
                      customInput={<CustomInput />}
                      placeholderText={
                        type === "makeup"
                          ? "Chọn ngày bù công"
                          : "Chọn ngày bắt đầu"
                      }
                    />
                  </div>

                  {/* Chỉ hiển thị ngày kết thúc nếu là đơn nghỉ phép */}
                  {type === "leave" && (
                    <div className="employeedetail-input-group">
                      <div className="employeedetail-input-label">Đến ngày</div>
                      <DatePicker
                        selected={endDate}
                        onChange={(date) => setEndDate(date)}
                        dateFormat="dd/MM/yyyy"
                        locale={vi}
                        customInput={<CustomInput />}
                        placeholderText="Chọn ngày kết thúc"
                      />
                    </div>
                  )}
                </div>
              </>
            )}
            <div className="employeedetail-form-row">
              <div className="employeedetail-input-group">
                <div className="employeedetail-input-label">Ảnh đính kèm</div>
                <div className="custom-upload-container">
                  <input
                    type="file"
                    accept="image/*"
                    id="fileUpload"
                    style={{ display: "none" }}
                    onChange={(e) => {
                      const file = e.target.files[0];
                      if (file) {
                        setAttachment(file);
                        setAttachmentPreview(URL.createObjectURL(file));
                      }
                    }}
                  />
                  {attachmentPreview && (
                    <div style={{ marginTop: 10 }}>
                      <img
                        src={attachmentPreview}
                        alt="Ảnh đính kèm"
                        style={{
                          maxWidth: "100%",
                          maxHeight: "300px", // 👈 tăng chiều cao
                          borderRadius: "8px",
                          objectFit: "contain", // giữ tỷ lệ ảnh
                          boxShadow: "0 0 8px rgba(0,0,0,0.15)", // thêm chút đổ bóng cho đẹp
                        }}
                      />
                    </div>
                  )}

                  <label
                    htmlFor="fileUpload"
                    className="custom-upload-label"
                  >
                    📤 <strong>Bấm vào đây</strong> để tải tệp
                    {attachment && (
                      <div style={{ marginTop: 5 }}>{attachment.name}</div>
                    )}
                  </label>
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
                Gửi đơn
              </button>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}

export default ApplicationCreate;
