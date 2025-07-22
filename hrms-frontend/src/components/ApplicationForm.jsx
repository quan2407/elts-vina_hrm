import React, { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import { vi } from "date-fns/locale";
import ReactQuill from "react-quill";
import "react-datepicker/dist/react-datepicker.css";
import "react-quill/dist/quill.snow.css";
import "../styles/EmployeeDetails.css";
import { Save } from "lucide-react";

function ApplicationForm({
  mode = "create",
  data = {},
  type = "leave",
  onSubmit,
  initialDate = null,
}) {
  const quillModules = {
    toolbar: [
      ["bold", "italic", "underline"],
      [{ list: "ordered" }, { list: "bullet" }],
    ],
  };

  const [errors, setErrors] = useState({});
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [leaveCode, setLeaveCode] = useState("");
  const [isHalfDay, setIsHalfDay] = useState(false);
  const [halfDayType, setHalfDayType] = useState("MORNING");
  const [attachmentPath, setAttachmentPath] = useState(null);
  const [attachment, setAttachment] = useState(null);
  const [attachmentPreview, setAttachmentPreview] = useState(null);

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
  const [isReadOnly, setIsReadOnly] = useState(true);

  useEffect(() => {
    if (mode === "detail" && data) {
      setTitle(data.title || "");
      setContent(data.content || "");
      setStartDate(data.startDate ? new Date(data.startDate) : null);
      setEndDate(data.endDate ? new Date(data.endDate) : null);
      setLeaveCode(data.leaveCode || "");
      setIsHalfDay(data.isHalfDay || false);
      setHalfDayType(data.halfDayType || "MORNING");
      setAttachmentPath(data.attachmentPath || null);
      setAttachmentPreview(null);

      // 👇 Kiểm tra nếu tất cả các bước đều PENDING → cho sửa
      const editable =
        data.status === "PENDING_MANAGER_APPROVAL" &&
        data.approvalSteps?.every((step) => step.status === "PENDING");

      setIsReadOnly(!editable);
    }
  }, [mode, data]);

  const apiBase = import.meta.env.VITE_API_URL || "";

  return (
    <div className="employeedetail-form-container">
      <div className="employeedetail-form-content">
        {/* Tiêu đề đơn */}
        <div className="employeedetail-form-row">
          <div className="employeedetail-input-group">
            <div className="employeedetail-input-label">Tiêu đề đơn</div>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="employeedetail-input-field"
              placeholder="Nhập tiêu đề"
              readOnly={isReadOnly}
            />
          </div>
        </div>

        {/* Nội dung đơn */}
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
              <div className="error-message">{errors.content.join(", ")}</div>
            )}
          </div>
        </div>

        {/* Ngày bắt đầu & kết thúc */}
        <div className="employeedetail-form-row">
          <div className="employeedetail-input-group">
            <div className="employeedetail-input-label">
              {type === "makeup" ? "Ngày bù công" : "Từ ngày"}
            </div>
            <DatePicker
              selected={startDate}
              onChange={(date) => setStartDate(date)}
              locale={vi}
              dateFormat="dd/MM/yyyy"
              customInput={<CustomInput />}
              disabled={isReadOnly}
            />
          </div>

          {type === "leave" && (
            <div className="employeedetail-input-group">
              <div className="employeedetail-input-label">Đến ngày</div>
              <DatePicker
                selected={endDate}
                onChange={(date) => setEndDate(date)}
                locale={vi}
                dateFormat="dd/MM/yyyy"
                customInput={<CustomInput />}
                disabled={isReadOnly}
              />
            </div>
          )}
        </div>
        {mode === "detail" && (
          <div className="employeedetail-form-row">
            {Array.from({ length: 2 }, (_, index) => {
              const step = data.approvalSteps?.find(
                (s) => s.step === index + 1
              ) || {
                approverName: null,
                status: "PENDING",
              };

              const stepLabel =
                index === 0
                  ? "Người duyệt - Quản lý trực tiếp"
                  : "Người duyệt - Phòng Nhân sự (HR)";
              const statusVN =
                {
                  PENDING: "Chưa duyệt",
                  APPROVED: "Đã duyệt",
                  REJECTED: "Từ chối",
                }[step.status] || "Chưa duyệt";

              return (
                <React.Fragment key={index}>
                  <div className="employeedetail-input-group">
                    <div className="employeedetail-input-label">
                      {stepLabel}
                    </div>
                    <input
                      type="text"
                      className="employeedetail-input-field"
                      value={step.approverName || "(chưa phân công)"}
                      readOnly
                    />
                  </div>
                  <div className="employeedetail-input-group">
                    <div className="employeedetail-input-label">Tình trạng</div>
                    <input
                      type="text"
                      className="employeedetail-input-field"
                      value={statusVN}
                      readOnly
                    />
                  </div>
                </React.Fragment>
              );
            })}
          </div>
        )}

        {/* Ảnh đính kèm */}
        <div className="employeedetail-form-row">
          <div className="employeedetail-input-group">
            <div className="employeedetail-input-label">Ảnh đính kèm</div>
            {/* Ảnh đính kèm (luôn hiển thị nếu có) */}
            {attachmentPreview ? (
              <img
                src={attachmentPreview}
                alt="Ảnh đính kèm"
                className="preview-img"
              />
            ) : attachmentPath ? (
              <img
                src={
                  attachmentPath.startsWith("http")
                    ? attachmentPath
                    : `http://localhost:8080/${attachmentPath}`
                }
                alt="Ảnh đính kèm"
                className="preview-img"
              />
            ) : (
              <i>Không có ảnh</i>
            )}

            {/* Chỉ cho phép thêm/sửa nếu mode=create hoặc detail nhưng được sửa */}
            {(mode === "create" || (mode === "detail" && !isReadOnly)) && (
              <div
                className="custom-upload-container"
                style={{ marginTop: 10 }}
              >
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
            )}
          </div>
        </div>

        {/* Nút gửi đơn */}
        {mode === "create" && (
          <div className="employeedetail-form-actions">
            <button
              className="submit-button"
              onClick={() =>
                onSubmit({
                  title,
                  content,
                  startDate,
                  endDate,
                  leaveCode,
                  isHalfDay,
                  halfDayType,
                  attachment,
                })
              }
            >
              <Save
                size={16}
                style={{ marginRight: "8px" }}
              />
              Gửi đơn
            </button>
          </div>
        )}
        {mode === "detail" && !isReadOnly && (
          <div className="employeedetail-form-actions">
            <button
              className="submit-button"
              onClick={() =>
                onSubmit({
                  title,
                  content,
                  startDate,
                  endDate,
                  leaveCode,
                  isHalfDay,
                  halfDayType,
                  attachment,
                })
              }
            >
              <Save
                size={16}
                style={{ marginRight: "8px" }}
              />
              Cập nhật đơn
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default ApplicationForm;
