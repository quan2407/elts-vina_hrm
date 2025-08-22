import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import HolidayTable from "../components/HolidayTable";
import AddHolidayModal from "../components/AddHolidayModal";
import "../styles/ManagementLayout.css";

function HolidayManagement() {
  const [searchTerm, setSearchTerm] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editId, setEditId] = useState(null);

  const handleAddClick = () => {
    setEditId(null);
    setModalOpen(true);
  };

  const handleEditClick = (id) => {
    setEditId(id);
    setModalOpen(true);
  };

  const handleModalClose = () => {
    setModalOpen(false);
    setEditId(null);
  };

  const handleCreateSuccess = () => {
    setModalOpen(false);
    setEditId(null);
    window.location.reload(); // hoặc: refetch
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách ngày nghỉ</h1>
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
              <label htmlFor="floatingInputValue">Tìm kiếm theo tên</label>
            </form>

            <button
              type="button"
              onClick={handleAddClick}
              style={{
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
              Thêm ngày nghỉ
            </button>
          </div>
        </div>

        <HolidayTable
          searchTerm={searchTerm}
          onEdit={handleEditClick}
        />

        <AddHolidayModal
          isOpen={modalOpen}
          onClose={handleModalClose}
          onSuccess={handleCreateSuccess}
          editId={editId}
        />
      </div>
    </MainLayout>
  );
}

export default HolidayManagement;
