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
    if (dateMeta?.holidayFlag) setTargetField("holidayShift");
    else if (dateMeta?.weekendFlag) setTargetField("weekendShift");
    else setTargetField("dayShift");
  }, [dateMeta]);

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
            {[
              "NL",
              "VR",
              "P",
              "P_4",
              "KL",
              "KL1",
              "KL1_2",
              "KL1_4",
              "KL1_2_4",
              "NDB",
              "NDB_4",
              "NDB_1_5",
              "VPHĐ",
              "NTS",
            ].map((code) => (
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
            <option value="dayShift">Công ngày</option>
            <option value="otShift">Tăng ca</option>
            <option value="weekendShift">Cuối tuần</option>
            <option value="holidayShift">Ngày lễ</option>
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
