import React, { useState, useEffect } from "react";
import "../styles/LeaveCodeModal.css";

const LeaveCodeModal = ({
  isOpen,
  onClose,
  onSave,
  recordId,
  dateInfo,
  dateMeta,
}) => {
  const [leaveCode, setLeaveCode] = useState("");
  const [targetField, setTargetField] = useState("dayShift");

  useEffect(() => {
    if (dateMeta?.holidayFlag) {
      setTargetField("holidayShift");
    } else if (dateMeta?.weekendFlag) {
      setTargetField("weekendShift");
    } else {
      setTargetField("dayShift");
    }
  }, [dateMeta]);

  const getAvailableTargetFields = () => {
    if (dateMeta?.holidayFlag) {
      return [{ value: "holidayShift", label: "Ngày lễ" }];
    }
    if (dateMeta?.weekendFlag) {
      return [{ value: "weekendShift", label: "Cuối tuần" }];
    }
    const fields = [{ value: "dayShift", label: "Công ngày" }];
    if (dateMeta?.hasOt) {
      fields.push({ value: "otShift", label: "Tăng ca" });
    }
    return fields;
  };

  const availableTargetFields = getAvailableTargetFields();

  const handleSave = () => {
    if (leaveCode && targetField) {
      onSave(recordId, leaveCode, targetField);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="leave-code-modal-overlay">
      <div className="leave-code-modal-container">
        <div className="leave-code-modal-title">Chọn nghỉ phép</div>

        <div className="leave-code-modal-field">
          <label>Mã nghỉ phép</label>
          <select
            value={leaveCode}
            onChange={(e) => setLeaveCode(e.target.value)}
          >
            <option value="">-- Chọn mã --</option>
            {["KL", "KH", "CKH", "NT", "P", "P_2", "NTS"].map((code) => (
              <option
                key={code}
                value={code}
              >
                {code}
              </option>
            ))}
          </select>
        </div>

        <div className="leave-code-modal-field">
          <label>Áp dụng cho</label>
          <select
            value={targetField}
            onChange={(e) => setTargetField(e.target.value)}
          >
            {availableTargetFields.map((field) => (
              <option
                key={field.value}
                value={field.value}
              >
                {field.label}
              </option>
            ))}
          </select>
        </div>

        <div className="leave-code-modal-actions">
          <button
            className="leave-code-modal-save-btn"
            onClick={handleSave}
          >
            Lưu
          </button>
          <button
            className="leave-code-modal-cancel-btn"
            onClick={onClose}
          >
            Huỷ
          </button>
        </div>
      </div>
    </div>
  );
};

export default LeaveCodeModal;
