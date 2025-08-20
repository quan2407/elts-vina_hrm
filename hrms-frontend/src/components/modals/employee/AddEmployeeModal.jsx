import React, { useEffect, useState } from "react";
import employeeService from "../../../services/employeeService";
import "../../../styles/AddEmployeeModal.css";
import "../../../styles/AddEmployeeModalTable.css";
import SuccessModal from "../../popup/SuccessModal";

const AddEmployeeModal = ({ lineId, onClose, onSuccess }) => {
  const [aemEmployees, setAemEmployees] = useState([]);
  const [aemSelectedIds, setAemSelectedIds] = useState([]);
  const [aemSearchTerm, setAemSearchTerm] = useState("");
  const [aemShowSuccess, setAemShowSuccess] = useState(false);
  const [aemShowAddEmpty, setAemShowAddEmpty] = useState(false);

  useEffect(() => {
    const fetchAvailableEmployees = async () => {
      try {
        const response = await employeeService.getEmployeeNotInLine(
          lineId,
          aemSearchTerm
        );
        setAemEmployees(response.data || []);
      } catch (error) {
        console.error("Lỗi khi lấy danh sách nhân viên chưa vào line:", error);
      }
    };
    fetchAvailableEmployees();
  }, [lineId, aemSearchTerm]);

  const aemHandleCheckboxChange = (employeeId) => {
    setAemSelectedIds((prev) =>
      prev.includes(employeeId)
        ? prev.filter((id) => id !== employeeId)
        : [...prev, employeeId]
    );
  };

  const aemSortByLineStatus = (list) => {
    return list.slice().sort((a, b) => {
      const aInLine = a.lineName && a.lineName.trim() !== "";
      const bInLine = b.lineName && b.lineName.trim() !== "";
      return aInLine - bInLine;
    });
  };

  const aemHandleAdd = async () => {
    if (aemSelectedIds.length === 0) {
      setAemShowAddEmpty(true);
      return;
    }
    try {
      await employeeService.addEmployeesToLine(lineId, aemSelectedIds);
      setAemShowSuccess(true);
    } catch (error) {
      console.error("Lỗi khi thêm nhân viên vào line", error);
    }
  };

  return (
    <div className="aem-modal-overlay">
      <div className="aem-modal-box large">
        <h3 className="aem-modal-title aem-sticky-title">Thêm nhân viên vào line</h3>

        <div className="aem-search-bar-wrapper">
          <input
            type="text"
            className="form-control"
            placeholder="Tìm kiếm theo tên nhân viên..."
            value={aemSearchTerm}
            onChange={(e) => setAemSearchTerm(e.target.value)}
            style={{ width: "300px", marginBottom: 16 }}
          />
        </div>

        <div className="aem-employee-table-container">
          <div className="aem-employee-table">
            <div className="aem-employee-table-header">
              <div className="aem-employee-header-cell">Chọn</div>
              <div className="aem-employee-header-cell">Mã NV</div>
              <div className="aem-employee-header-cell">Họ tên</div>
              <div className="aem-employee-header-cell">Chuyền</div>
              <div className="aem-employee-header-cell">Giới tính</div>
              <div className="aem-employee-header-cell">Phòng ban</div>
              <div className="aem-employee-header-cell">Vị trí</div>
            </div>

            {aemEmployees.length === 0 && (
              <div className="aem-employee-table-empty" style={{ color: "red" }}>
                Không tìm thấy nhân viên nào phù hợp.
              </div>
            )}

            {aemSortByLineStatus(aemEmployees).map((emp) => (
              <div key={emp.employeeId} className="aem-employee-table-row">
                <div className="aem-employee-table-cell">
                  <input
                    type="checkbox"
                    checked={aemSelectedIds.includes(emp.employeeId)}
                    onChange={() => aemHandleCheckboxChange(emp.employeeId)}
                  />
                </div>
                <div className="aem-employee-table-cell">{emp.employeeCode}</div>
                <div className="aem-employee-table-cell">{emp.employeeName}</div>
                <div className="aem-employee-table-cell">{emp.lineName || "-"}</div>
                <div className="aem-employee-table-cell">{emp.gender || "-"}</div>
                <div className="aem-employee-table-cell">
                  {emp.departmentName || "-"}
                </div>
                <div className="aem-employee-table-cell">
                  {emp.positionName || "-"}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="aem-modal-actions">
          <button className="aem-btn-confirm" onClick={aemHandleAdd}>
            Thêm
          </button>
          <button className="aem-btn-cancel" onClick={onClose}>
            Hủy
          </button>
        </div>
      </div>

      {aemShowAddEmpty && (
        <SuccessModal
          title="Chưa chọn nhân viên"
          message="Chưa chọn nhân viên để thêm vào line!"
          onClose={() => {
            setAemShowAddEmpty(false);
          }}
        />
      )}

      {aemShowSuccess && (
        <SuccessModal
          title="Thêm nhân viên thành công"
          message="Thêm nhân viên mới thành công!"
          onClose={() => {
            setAemShowSuccess(false);
            onSuccess();
            onClose();
          }}
        />
      )}
    </div>
  );
};

export default AddEmployeeModal;
