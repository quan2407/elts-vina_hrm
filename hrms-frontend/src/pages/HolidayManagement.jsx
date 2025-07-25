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

            <div
              className="add-button"
              style={{ height: "55px" }}
              onClick={handleAddClick}
            >
              <span className="export-text">Thêm ngày nghỉ</span>
            </div>
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
