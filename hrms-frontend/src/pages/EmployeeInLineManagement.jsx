import React, { useEffect, useRef, useState } from "react";
import MainLayout from "../components/MainLayout";
import EmployeeTableInLine from "../components/EmployeeTableInLine";
import "../styles/ManagementLayout.css";
import { getLineById } from "../services/linesService";
import { useParams, useNavigate } from "react-router-dom";
import AddEmployeeModal from "../components/modals/employee/AddEmployeeModal";
function EmployeeInLineManagement() {
  const tableRef = useRef();
  const [showAddModal, setShowAddModal] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [line, setLine] = useState([]);
  const { id } = useParams();
  const [reloadFlag, setReloadFlag] = useState(false);
  const navigate = useNavigate();

  const handleExportClick = () => {
    if (tableRef.current) {
      tableRef.current.exportToExcel();
    }
  };

  const handleBackClick = () => {
    navigate(-1);
  };

  useEffect(() => {
    const fetchLineInfo = async () => {
      try {
        const data = await getLineById(id);
        setLine(data);
      } catch (error) {
        console.error("Lỗi khi load thông tin chuyền:", error);
      }
    };
    fetchLineInfo();
  }, [id]);

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">
            Danh sách nhân viên {line.name ? `- ${line.name}` : ""}
          </h1>
          <div className="page-actions">
            <form
              className="form-floating"
              onSubmit={(e) => e.preventDefault()}
            >
              <input
                type="text"
                className="form-control"
                style={{ width: "240px" }}
                id="floatingInputValue"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <label htmlFor="floatingInputValue">
                Tìm kiếm theo tên ứng viên
              </label>
            </form>

            <button
              type="button"
              onClick={() => setShowAddModal(true)}
              style={{
                backgroundColor: "#2563eb", // xanh dương
                color: "#fff",
                padding: "10px 20px",
                borderRadius: "8px",
                border: "none",
                fontSize: "16px",
                fontWeight: 500,
                cursor: "pointer",
                height: "55px",
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                transition: "background-color 0.2s ease",
              }}
              onMouseOver={(e) =>
                (e.currentTarget.style.backgroundColor = "#1e40af")
              }
              onMouseOut={(e) =>
                (e.currentTarget.style.backgroundColor = "#2563eb")
              }
            >
              Thêm nhân viên vào line
            </button>

            <button
              type="button"
              onClick={handleExportClick}
              style={{
                backgroundColor: "#22c55e", // xanh lá
                color: "#fff",
                padding: "10px 20px",
                borderRadius: "8px",
                border: "none",
                fontSize: "16px",
                fontWeight: 500,
                cursor: "pointer",
                height: "55px",
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                transition: "background-color 0.2s ease",
              }}
              onMouseOver={(e) =>
                (e.currentTarget.style.backgroundColor = "#15803d")
              }
              onMouseOut={(e) =>
                (e.currentTarget.style.backgroundColor = "#22c55e")
              }
            >
              Export
            </button>
          </div>
        </div>

        <EmployeeTableInLine
          searchTerm={searchTerm}
          reloadFlag={reloadFlag}
        />
        <div className="page-header">
          <div
            className="page-actions"
            style={{
              display: "flex",
              justifyContent: "flex-end",
              width: "100%",
            }}
          >
            <div
              className="export-button"
              style={{
                height: "55px",
                marginTop: "50px",
                marginLeft: "auto",
                background: "#e70c0c",
              }}
              onClick={handleBackClick}
            >
              <span className="export-text">Back</span>
            </div>
          </div>
        </div>
      </div>

      {showAddModal && (
        <AddEmployeeModal
          lineId={id}
          onClose={() => setShowAddModal(false)}
          onSuccess={() => {
            setReloadFlag((prev) => !prev);
          }}
        />
      )}
    </MainLayout>
  );
}

export default EmployeeInLineManagement;
