import React, { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import { vi } from "date-fns/locale";
import applicationApprovalService from "../services/applicationApprovalService";
import TimePicker from "react-time-picker";
import "react-time-picker/dist/TimePicker.css";
import { useNavigate } from "react-router-dom";
import ReactQuill from "react-quill";
import "react-datepicker/dist/react-datepicker.css";
import "react-quill/dist/quill.snow.css";
import "../styles/ApplicationForm.css";
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
  function formatToHHmm(value) {
    if (!value) return "";
    const [h, m] = value.split(":");
    const hour = String(h).padStart(2, "0");
    const minute = String(m || "00").padStart(2, "0");
    return `${hour}:${minute}`;
  }

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
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const navigate = useNavigate();

  const [isReadOnly, setIsReadOnly] = useState(true);

  const roles = JSON.parse(localStorage.getItem("role") || "[]");
  const currentUserId = localStorage.getItem("userId") || "";
  const isManager = roles.includes("ROLE_PRODUCTION_MANAGER");
  const isCreator = data?.creator;
  console.log("🆔 Người tạo đơn:", data?.employeeId);
  console.log("🧑‍💼 Người đang đăng nhập:", currentUserId);
  console.log("✅ isCreator:", isCreator);

  const isManagerApprover =
    isManager &&
    data?.status === "PENDING_MANAGER_APPROVAL" &&
    data?.approvalSteps?.[0]?.status === "PENDING";
  const isHrApprover =
    roles.includes("ROLE_HR") &&
    data?.status === "MANAGER_APPROVED" &&
    data?.approvalSteps?.[1]?.approverName === null &&
    data?.approvalSteps?.[1]?.status === "PENDING";

  const isStillEditableByCreator =
    isCreator &&
    data?.status === "PENDING_MANAGER_APPROVAL" &&
    data?.approvalSteps?.every((step) => step.status === "PENDING");

  const isEditable =
    mode === "create" ||
    (isCreator && (isStillEditableByCreator || isManagerApprover));

  const CustomInput = React.forwardRef(function CustomInput(
    { value, onClick, placeholder },
    ref
  ) {
    return (
      <input
        className="application-form-input-field"
        onClick={onClick}
        value={value || ""}
        placeholder={placeholder}
        readOnly
        ref={ref}
      />
    );
  });
  useEffect(() => {
    console.log("🧾 FULL DATA:", data);
  }, [data]);

  // ✅ Khởi tạo giá trị nếu tạo đơn mới
  useEffect(() => {
    if (mode === "create" && initialDate) {
      setStartDate(initialDate);
      if (type === "leave") setEndDate(initialDate);
      if (type === "makeup") {
        setCheckIn("08:00");
        setCheckOut("17:00");
      }
    }
  }, [mode, initialDate, type]);

  // ✅ Gán readonly đúng theo logic đã tính
  useEffect(() => {
    setIsReadOnly(!isEditable);

    if (mode === "detail") {
      setTitle(data.title || "");
      setContent(data.content || "");
      setStartDate(data.startDate ? new Date(data.startDate) : null);
      setEndDate(data.endDate ? new Date(data.endDate) : null);
      setLeaveCode(data.leaveCode || "");
      setIsHalfDay(data.isHalfDay || false);
      setHalfDayType(data.halfDayType || "MORNING");
      setCheckIn(formatToHHmm(data.checkIn || "08:00"));
      setCheckOut(formatToHHmm(data.checkOut || "17:00"));
      setAttachmentPath(data.attachmentPath || null);
      setAttachmentPreview(null);
    }
  }, [mode, data, isEditable]);

  const apiBase = import.meta.env.VITE_API_URL || "";

  return (
    <div className="application-form-container">
      <div className="application-form-content">
        <div className="application-form-row">
          <div className="application-form-input-group">
            <div className="application-form-input-label">Tiêu đề đơn</div>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="application-form-input-field"
              placeholder="Nhập tiêu đề"
              readOnly={isReadOnly}
            />
          </div>
        </div>

        {mode === "detail" && (
          <>
            <div className="application-form-row">
              <div className="application-form-input-group">
                <div className="application-form-input-label">Mã nhân viên</div>
                <input
                  type="text"
                  className="application-form-input-field"
                  value={data.employeeCode || ""}
                  readOnly
                />
              </div>
              <div className="application-form-input-group">
                <div className="application-form-input-label">
                  Tên nhân viên
                </div>
                <input
                  type="text"
                  className="application-form-input-field"
                  value={data.employeeName || ""}
                  readOnly
                />
              </div>
            </div>

            <div className="application-form-row">
              <div className="application-form-input-group">
                <div className="application-form-input-label">Chức vụ</div>
                <input
                  type="text"
                  className="application-form-input-field"
                  value={data.positionName || ""}
                  readOnly
                />
              </div>
              <div className="application-form-input-group">
                <div className="application-form-input-label">Phòng ban</div>
                <input
                  type="text"
                  className="application-form-input-field"
                  value={data.departmentName || ""}
                  readOnly
                />
              </div>
              <div className="application-form-input-group">
                <div className="application-form-input-label">Line</div>
                <input
                  type="text"
                  className="application-form-input-field"
                  value={data.lineName || ""}
                  readOnly
                />
              </div>
            </div>
          </>
        )}

        <div className="application-form-row">
          <div className="application-form-input-group">
            <div className="application-form-input-label">Nội dung đơn</div>
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
              readOnly={isReadOnly}
            />
            {errors.content && (
              <div className="error-message">{errors.content.join(", ")}</div>
            )}
          </div>
        </div>

        <div className="application-form-row">
          <div className="application-form-input-group">
            <div
              className="application-form-input-label"
              style={type === "makeup" ? { flex: "0 0 50%" } : {}}
            >
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
            {mode === "detail" &&
              type === "makeup" &&
              data?.employeeId &&
              data?.startDate && (
                <div style={{ margin: "16px 0" }}>
                  <button
                    className="application-form-navigate-btn"
                    onClick={() => {
                      const dateStr = data.startDate.substring(0, 10); // yyyy-MM-dd
                      navigate(
                        `/attendance?focusEmployee=${
                          data.employeeId
                        }&focusDate=${data.startDate.substring(0, 10)}`
                      );
                    }}
                  >
                    📅 Xem bảng công ngày này
                  </button>
                </div>
              )}
            {mode === "detail" &&
              type === "leave" &&
              data?.startDate &&
              data?.departmentName && ( // dùng tên thay vì id
                <div style={{ marginTop: "8px" }}>
                  <button
                    className="application-form-navigate-btn"
                    onClick={() => {
                      const dateStr = data.startDate.substring(0, 10); // yyyy-MM-dd
                      const query = new URLSearchParams({
                        focusDate: dateStr,
                        departmentName: data.departmentName,
                      });

                      if (data.lineName) {
                        query.append("lineName", data.lineName);
                      }

                      navigate(`/work-schedule?${query.toString()}`);
                    }}
                  >
                    🗓️ Xem lịch làm việc ngày này
                  </button>
                </div>
              )}
          </div>

          {type === "leave" && (
            <div className="application-form-input-group">
              <div className="application-form-input-label">Đến ngày</div>
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
        {type === "makeup" && (
          <div className="application-form-row">
            <div className="application-form-input-group">
              <div className="application-form-input-label">
                Giờ vào (check-in)
              </div>
              <TimePicker
                value={checkIn}
                onChange={(value) => setCheckIn(formatToHHmm(value))}
                format="HH:mm"
                disableClock
                clearIcon={null}
                className="application-form-time-picker"
                locale="vi-VN"
              />
            </div>
            <div className="application-form-input-group">
              <div className="application-form-input-label">
                Giờ ra (check-out)
              </div>
              <TimePicker
                value={checkOut}
                onChange={(value) => setCheckOut(formatToHHmm(value))}
                format="HH:mm"
                disableClock
                clearIcon={null}
                className="application-form-time-picker"
                locale="vi-VN"
              />
            </div>
          </div>
        )}

        {type === "leave" && (
          <div className="application-form-row">
            <div className="application-form-input-group">
              <div className="application-form-input-label">Mã nghỉ phép</div>
              <select
                value={leaveCode}
                onChange={(e) => setLeaveCode(e.target.value)}
                className="application-form-input-field"
                disabled={isReadOnly}
              >
                <option value="">-- Chọn mã nghỉ phép --</option>
                <option value="KL">KL - Nghỉ không lương</option>
                <option value="KH">KH - Kết hôn</option>
                <option value="CKH">CKH - Con kết hôn</option>
                <option value="NT">NT - Nghỉ tang</option>
                <option value="P">P - Nghỉ phép</option>
                <option value="P_2">P_2 - Nghỉ phép nửa ngày</option>
                <option value="NTS">NTS - Nghỉ thai sản</option>
              </select>
            </div>

            {leaveCode === "P_2" && (
              <>
                <div className="application-form-input-group">
                  <div className="application-form-input-label">Buổi nghỉ</div>
                  <select
                    value={halfDayType}
                    onChange={(e) => setHalfDayType(e.target.value)}
                    className="application-form-input-field"
                    disabled={isReadOnly}
                  >
                    <option value="MORNING">Sáng</option>
                    <option value="AFTERNOON">Chiều</option>
                  </select>
                </div>
              </>
            )}
          </div>
        )}

        {mode === "detail" && (
          <div className="application-form-row">
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
                  <div className="application-form-input-group">
                    <div className="application-form-input-label">
                      {stepLabel}
                    </div>
                    <input
                      type="text"
                      className="application-form-input-field"
                      value={step.approverName || "(chưa phân công)"}
                      readOnly
                    />
                  </div>
                  <div className="application-form-input-group">
                    <div className="application-form-input-label">
                      Tình trạng
                    </div>
                    <input
                      type="text"
                      className="application-form-input-field"
                      value={statusVN}
                      readOnly
                    />
                  </div>
                </React.Fragment>
              );
            })}
          </div>
        )}

        <div className="application-form-row">
          <div className="application-form-input-group">
            <div className="application-form-input-label">Ảnh đính kèm</div>
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

        {(mode === "create" || (mode === "detail" && isEditable)) && (
          <div className="application-form-actions">
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
                  checkIn,
                  checkOut,
                })
              }
            >
              <Save
                size={16}
                style={{ marginRight: "8px" }}
              />
              {mode === "create" ? "Gửi đơn" : "Cập nhật đơn"}
            </button>
          </div>
        )}

        {mode === "detail" &&
          data.status === "PENDING_MANAGER_APPROVAL" &&
          data.approvalSteps?.[0]?.status === "PENDING" &&
          isManager && (
            <div
              className="application-detail-actions"
              style={{ marginTop: 20 }}
            >
              <button
                className="application-detail-approve-btn"
                onClick={() => {
                  const note = prompt("Ghi chú (nếu có):");
                  if (window.confirm("Bạn chắc chắn muốn duyệt đơn này?")) {
                    applicationApprovalService
                      .approveStep1(data.id, { approved: true, note })
                      .then(() => {
                        alert("✅ Đã duyệt đơn");
                        window.location.reload();
                      })
                      .catch(() => alert("❌ Lỗi khi duyệt đơn"));
                  }
                }}
                style={{ marginRight: 10 }}
              >
                ✅ Duyệt đơn
              </button>

              <button
                className="application-detail-reject-btn"
                onClick={() => {
                  const note = prompt("Lý do từ chối:");
                  if (!note) return alert("❗ Vui lòng nhập lý do từ chối.");
                  if (window.confirm("Bạn chắc chắn muốn từ chối đơn này?")) {
                    applicationApprovalService
                      .approveStep1(data.id, { approved: false, note })
                      .then(() => {
                        alert("🚫 Đã từ chối đơn");
                        window.location.reload();
                      })
                      .catch(() => alert("❌ Lỗi khi từ chối đơn"));
                  }
                }}
              >
                ❌ Từ chối đơn
              </button>
            </div>
          )}
        {mode === "detail" && isHrApprover && (
          <div
            className="application-detail-actions"
            style={{ marginTop: 20 }}
          >
            <button
              className="application-detail-approve-btn"
              onClick={() => {
                const note = prompt("Ghi chú (nếu có):");
                if (window.confirm("Bạn chắc chắn muốn duyệt đơn này?")) {
                  applicationApprovalService
                    .approveStep2(data.id, { approved: true, note })
                    .then(() => {
                      alert("✅ Đã duyệt đơn");
                      window.location.reload();
                    })
                    .catch(() => alert("❌ Lỗi khi duyệt đơn"));
                }
              }}
              style={{ marginRight: 10 }}
            >
              ✅ Duyệt đơn
            </button>

            <button
              className="application-detail-reject-btn"
              onClick={() => {
                const note = prompt("Lý do từ chối:");
                if (!note) return alert("❗ Vui lòng nhập lý do từ chối.");
                if (window.confirm("Bạn chắc chắn muốn từ chối đơn này?")) {
                  applicationApprovalService
                    .approveStep2(data.id, { approved: false, note })
                    .then(() => {
                      alert("🚫 Đã từ chối đơn");
                      window.location.reload();
                    })
                    .catch(() => alert("❌ Lỗi khi từ chối đơn"));
                }
              }}
            >
              ❌ Từ chối đơn
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default ApplicationForm;
