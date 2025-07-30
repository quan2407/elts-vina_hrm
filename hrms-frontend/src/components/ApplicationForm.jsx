import React, { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import { vi } from "date-fns/locale";
import applicationApprovalService from "../services/applicationApprovalService";
import employeeService from "../services/employeeService";
import TimePicker from "react-time-picker";
import "react-time-picker/dist/TimePicker.css";
import { useNavigate } from "react-router-dom";
import ReactQuill from "react-quill";
import "react-datepicker/dist/react-datepicker.css";
import "react-quill/dist/quill.snow.css";
import "../styles/ApplicationForm.css";
import { Save } from "lucide-react";
import Select from "react-select";

function ApplicationForm({
  mode = "create",
  data = {},
  type = "leave",
  onSubmit,
  initialDate = null,
  externalErrors = {},
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
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [applicationType, setApplicationType] = useState(type || "leave");

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

  const roles = JSON.parse(localStorage.getItem("role") || "[]");
  const currentUserId = localStorage.getItem("userId") || "";
  const isManager = roles.includes("ROLE_PRODUCTION_MANAGER");
  const isCreator = data?.creator;

  useEffect(() => {
    if (data) {
      console.log("🆔 Người tạo đơn:", data?.employeeId);
      console.log("🧑‍💼 Người đang đăng nhập:", currentUserId);
      console.log("✅ isCreator:", data?.creator); // hoặc là isCreator cũng được
    }
  }, [data, currentUserId]);

  const currentType = mode === "create" ? applicationType : type;

  const isManagerApprover = React.useMemo(() => {
    return (
      isManager &&
      data?.status === "PENDING_MANAGER_APPROVAL" &&
      data?.approvalSteps?.[0]?.status === "PENDING"
    );
  }, [isManager, data]);

  const isHrApprover = React.useMemo(() => {
    return (
      roles.includes("ROLE_HR") &&
      data?.status === "MANAGER_APPROVED" &&
      data?.approvalSteps?.[1]?.approverName === null &&
      data?.approvalSteps?.[1]?.status === "PENDING"
    );
  }, [roles, data]);

  const isStillEditableByCreator = React.useMemo(() => {
    return (
      isCreator &&
      data?.status === "PENDING_MANAGER_APPROVAL" &&
      data?.approvalSteps?.every((step) => step.status === "PENDING")
    );
  }, [isCreator, data]);

  const isEditable = React.useMemo(() => {
    return (
      mode === "create" ||
      (isCreator && (isStillEditableByCreator || isManagerApprover))
    );
  }, [mode, isCreator, isStillEditableByCreator, isManagerApprover]);

  const CustomInput = React.forwardRef(function CustomInput(
    { value, onClick, placeholder },
    ref
  ) {
    const isMakeupReadOnly =
      mode === "create" &&
      type === "makeup" &&
      !(roles.includes("ROLE_HR") || roles.includes("ROLE_PRODUCTION_MANAGER"));

    return (
      <input
        className="application-form-input-field"
        onClick={isMakeupReadOnly ? undefined : onClick}
        value={value || ""}
        placeholder={placeholder}
        readOnly
        ref={ref}
      />
    );
  });
  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const res = await employeeService.getSimpleEmployees();
        setEmployees(res.data);
      } catch (err) {
        console.error("Lỗi khi tải danh sách nhân viên:", err);
      }
    };

    if (
      mode === "create" &&
      (roles.includes("ROLE_HR") || roles.includes("ROLE_PRODUCTION_MANAGER"))
    ) {
      fetchEmployees();
    }
  }, []);

  useEffect(() => {
    setErrors((prev) => {
      const isSame =
        JSON.stringify(prev || {}) === JSON.stringify(externalErrors || {});
      return isSame ? prev : externalErrors;
    });
  }, [externalErrors]);

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

  const isReadOnly =
    mode !== "create" &&
    !(isCreator && (isStillEditableByCreator || isManagerApprover));

  useEffect(() => {
    if (mode === "detail" && data) {
      if (title !== (data.title || "")) setTitle(data.title || "");
      if (content !== (data.content || "")) setContent(data.content || "");

      const newStart = data.startDate ? new Date(data.startDate) : null;
      if (
        (startDate && newStart && startDate.getTime() !== newStart.getTime()) ||
        (!startDate && newStart) ||
        (startDate && !newStart)
      ) {
        setStartDate(newStart);
      }

      const newEnd = data.endDate ? new Date(data.endDate) : null;
      if (
        (endDate && newEnd && endDate.getTime() !== newEnd.getTime()) ||
        (!endDate && newEnd) ||
        (endDate && !newEnd)
      ) {
        setEndDate(newEnd);
      }

      if (leaveCode !== (data.leaveCode || ""))
        setLeaveCode(data.leaveCode || "");
      if (isHalfDay !== (data.isHalfDay || false))
        setIsHalfDay(data.isHalfDay || false);
      if (halfDayType !== (data.halfDayType || ""))
        setHalfDayType(data.halfDayType || "");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mode, data]);

  const customStyles = {
    control: (provided, state) => ({
      ...provided,
      backgroundColor: "#e3edf9",
      border: "none",
      borderRadius: "15px",
      padding: "0 16px",
      height: "68px",
      boxShadow: "none",
      fontSize: "18px",
      fontWeight: 400,
      color: "rgba(0, 0, 0, 0.7)",
    }),
    valueContainer: (provided) => ({
      ...provided,
      padding: 0,
    }),
    input: (provided) => ({
      ...provided,
      margin: 0,
      padding: 0,
    }),
    singleValue: (provided) => ({
      ...provided,
      color: "rgba(0, 0, 0, 0.7)",
    }),
    placeholder: (provided) => ({
      ...provided,
      color: "rgba(0, 0, 0, 0.5)",
    }),
    indicatorSeparator: () => ({ display: "none" }),
  };

  const apiBase = import.meta.env.VITE_API_URL || "";
  const handleSubmit = async () => {
    setErrors({});
    try {
      await onSubmit({
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
        type: currentType,
        selectedEmployee,
      });
    } catch (err) {
      console.error("❌ Lỗi gửi đơn:", err);

      if (err.response && err.response.data) {
        const data = err.response.data;

        if (typeof data === "object" && !Array.isArray(data)) {
          setErrors(data);
        } else if (data.message) {
          setErrors({ general: data.message });
        } else {
          setErrors({ general: "Có lỗi xảy ra khi gửi đơn." });
        }
      }
    }
  };

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
            {errors.title && (
              <div className="error-message">{errors.title.join(", ")}</div>
            )}
          </div>
        </div>
        {mode === "create" &&
          (roles.includes("ROLE_HR") ||
            roles.includes("ROLE_PRODUCTION_MANAGER")) && (
            <div className="application-form-row">
              <div className="application-form-input-group">
                <div className="application-form-input-label">
                  Người nộp đơn
                </div>
                <Select
                  classNamePrefix="react-select"
                  styles={customStyles}
                  options={employees.map((emp) => ({
                    value: emp.id,
                    label: `${emp.code} - ${emp.name}`,
                    ...emp,
                  }))}
                  value={
                    selectedEmployee
                      ? {
                          value: selectedEmployee.id,
                          label: `${selectedEmployee.code} - ${selectedEmployee.name}`,
                          ...selectedEmployee,
                        }
                      : null
                  }
                  onChange={(selected) => setSelectedEmployee(selected)}
                  placeholder="-- Chọn nhân viên --"
                  isClearable
                />
              </div>
            </div>
          )}

        {selectedEmployee && (
          <div className="application-form-row">
            <div className="application-form-input-group">
              <div className="application-form-input-label">Phòng ban</div>
              <input
                className="application-form-input-field"
                value={selectedEmployee.department}
                readOnly
              />
            </div>
            <div className="application-form-input-group">
              <div className="application-form-input-label">Chức vụ</div>
              <input
                className="application-form-input-field"
                value={selectedEmployee.position}
                readOnly
              />
            </div>
            <div className="application-form-input-group">
              <div className="application-form-input-label">Line</div>
              <input
                className="application-form-input-field"
                value={selectedEmployee.line}
                readOnly
              />
            </div>
          </div>
        )}

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
        {mode === "create" &&
          (roles.includes("ROLE_HR") ||
            roles.includes("ROLE_PRODUCTION_MANAGER")) && (
            <div className="application-form-row">
              <div className="application-form-input-group">
                <div className="application-form-input-label">Loại đơn</div>
                <select
                  className="application-form-input-field"
                  value={applicationType}
                  onChange={(e) => setApplicationType(e.target.value)}
                >
                  <option value="leave">Nghỉ phép</option>
                  <option value="makeup">Bù công</option>
                </select>
              </div>
            </div>
          )}

        <div className="application-form-row">
          <div className="application-form-input-group">
            <div className="application-form-input-label">Nội dung đơn</div>
            <ReactQuill
              value={content}
              onChange={setContent}
              modules={quillModules}
              placeholder={
                currentType === "leave"
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
              {currentType === "makeup" ? "Ngày bù công" : "Từ ngày"}
            </div>
            <DatePicker
              selected={startDate}
              onChange={(date) => {
                if (
                  !(
                    mode === "create" &&
                    currentType === "makeup" &&
                    roles.includes("ROLE_EMPLOYEE")
                  )
                ) {
                  setStartDate(date);
                }
              }}
              locale={vi}
              dateFormat="dd/MM/yyyy"
              customInput={<CustomInput />}
              disabled={isReadOnly}
            />

            {errors.startDate && (
              <div className="error-message">{errors.startDate.join(", ")}</div>
            )}
            {mode === "detail" &&
              currentType === "makeup" &&
              data?.employeeId &&
              data?.startDate && (
                <div style={{ margin: "16px 0" }}>
                  <button
                    className="application-form-navigate-btn"
                    onClick={() => {
                      const dateStr = data.startDate.substring(0, 10);
                      window.open(
                        `/attendance?focusEmployee=${
                          data.employeeId
                        }&focusDate=${data.startDate.substring(0, 10)}`,
                        "_blank"
                      );
                    }}
                  >
                    📅 Xem bảng công ngày này
                  </button>
                </div>
              )}
            {mode === "detail" &&
              currentType === "leave" &&
              data?.startDate &&
              data?.departmentName && (
                <div style={{ marginTop: "8px" }}>
                  <button
                    className="application-form-navigate-btn"
                    onClick={() => {
                      const dateStr = data.startDate.substring(0, 10);
                      const query = new URLSearchParams({
                        focusDate: dateStr,
                        departmentName: data.departmentName,
                      });

                      if (data.lineName) {
                        query.append("lineName", data.lineName);
                      }

                      window.open(
                        `/work-schedule?${query.toString()}`,
                        "_blank"
                      );
                    }}
                  >
                    🗓️ Xem lịch làm việc ngày này
                  </button>
                </div>
              )}
          </div>

          {currentType === "leave" && (
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
              {errors.endDate && (
                <div className="error-message">{errors.endDate.join(", ")}</div>
              )}
            </div>
          )}
        </div>
        {currentType === "makeup" && (
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
              {errors.checkIn && (
                <div className="error-message">{errors.checkIn.join(", ")}</div>
              )}
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
              {errors.checkOut && (
                <div className="error-message">
                  {errors.checkOut.join(", ")}
                </div>
              )}
            </div>
          </div>
        )}

        {currentType === "leave" && (
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
              {errors.leaveCode && (
                <div className="error-message">
                  {errors.leaveCode.join(", ")}
                </div>
              )}
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
              onClick={handleSubmit}
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
                        navigate("/applications/approvals/manager");
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
                        navigate("/applications/approvals/manager");
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
                      navigate("/applications/approvals/hr");
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
                      navigate("/applications/approvals/hr");
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
