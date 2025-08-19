import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import EmployeeTable from "../components/EmployeeTable";
import "../styles/ManagementLayout.css";
import employeeService from "../services/employeeService";
import departmentService from "../services/departmentService";
import positionService from "../services/positionService";
import { getAllLines } from "../services/linesService";

function EmployeeManagement() {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentId, setDepartmentId] = useState(null);
  const [positionId, setPositionId] = useState(null);
  const [lineId, setLineId] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [lines, setLines] = useState([]);

  useEffect(() => {
    (async () => {
      try {
        const depRes = await departmentService.getAllDepartments();
        setDepartments(depRes.data || []);

        const posRes = await positionService.getAll();
        setPositions(posRes.data || []);

        const lineRes = await getAllLines();
        setLines(lineRes || []);
      } catch (e) {
        console.error("Load danh mục lỗi:", e);
      }
    })();
  }, []);
  useEffect(() => {
    (async () => {
      try {
        if (departmentId) {
          const [posByDep, lineByDep] = await Promise.all([
            departmentService.getPositionsByDepartment(departmentId),
            departmentService.getLinesByDepartment(departmentId),
          ]);
          setPositions(posByDep.data || []);
          setLines(lineByDep.data || []);
        } else {
          const posRes = await positionService.getAll();
          setPositions(posRes.data || []);
          const lineRes = await getAllLines();
          setLines(lineRes || []);
        }
        setPositionId(null);
        setLineId(null);
      } catch (e) {
        console.error("Load positions/lines theo department lỗi:", e);
      }
    })();
  }, [departmentId]);

  const fetchEmployees = async (targetPage = page, targetSize = size) => {
    try {
      const res = await employeeService.getAllEmployees(
        targetPage,
        targetSize,
        searchTerm,
        departmentId,
        positionId,
        lineId
      );
      setEmployees(res.data?.content || []);
      setTotalPages(res.data?.totalPages || 0);
    } catch (error) {
      console.error("Failed to fetch employees", error);
      setEmployees([]);
      setTotalPages(0);
    }
  };

  useEffect(() => {
    fetchEmployees(page, size);
  }, [page]);

  useEffect(() => {
    setPage(0);
    fetchEmployees(0, size);
  }, [size]);

  const handleCreate = () => {
    navigate("/employee-create");
  };

  const handleExport = async () => {
    try {
      const response = await employeeService.exportFile();
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "danhsachnhanvien.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Export failed:", error);
      alert("Export failed. Please try again.");
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Danh sách nhân viên</h1>

          <div className="page-actions">
            <button
              className="add-button"
              style={{ height: "55px" }}
              onClick={handleCreate}
            >
              <span className="export-text">Thêm nhân viên</span>
            </button>
            <button
              className="export-button"
              style={{ height: "55px" }}
              onClick={handleExport}
            >
              <span className="export-text">Export</span>
            </button>
          </div>
        </div>
        <div
          className="employee-search-wrapper"
          style={{
            display: "flex",
            gap: 8,
            flexWrap: "wrap",
            justifyContent: "space-between",
          }}
        >
          <div
            className="page-size-control"
            style={{ display: "flex", alignItems: "center", gap: 6 }}
          >
            <label htmlFor="empPageSize">Hiển thị</label>
            <select
              id="empPageSize"
              value={size}
              onChange={(e) => {
                const newSize = Number(e.target.value);
                setSize(newSize);
              }}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
            <span>/ trang</span>
          </div>

          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <input
              type="text"
              className="employee-search-input"
              placeholder="Tìm theo mã hoặc tên nhân viên..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  setPage(0);
                  fetchEmployees(0, size);
                }
              }}
            />

            <select
              value={departmentId ?? ""}
              onChange={(e) =>
                setDepartmentId(e.target.value ? Number(e.target.value) : null)
              }
            >
              <option value="">-- Chọn Bộ Phận --</option>
              {departments.map((d) => (
                <option
                  key={d.id}
                  value={d.id}
                >
                  {d.name}
                </option>
              ))}
            </select>

            <select
              value={lineId ?? ""}
              onChange={(e) => {
                setLineId(e.target.value ? Number(e.target.value) : null);
              }}
            >
              <option value="">-- Chọn Chuyền --</option>
              {lines.map((l) => (
                <option
                  key={l.id}
                  value={l.id}
                >
                  {l.name}
                </option>
              ))}
            </select>

            <select
              value={positionId ?? ""}
              onChange={(e) => {
                setPositionId(e.target.value ? Number(e.target.value) : null);
              }}
            >
              <option value="">-- Chọn Chức Vụ --</option>
              {positions.map((p) => (
                <option
                  key={p.id}
                  value={p.id}
                >
                  {p.name}
                </option>
              ))}
            </select>

            <button
              onClick={() => {
                setSearchTerm("");
                setDepartmentId(null);
                setLineId(null);
                setPositionId(null);
                setPage(0);
                fetchEmployees(0, size);
              }}
            >
              Xóa bộ lọc
            </button>

            <button
              onClick={() => {
                setPage(0);
                fetchEmployees(0, size);
              }}
            >
              Tìm kiếm
            </button>
          </div>
        </div>

        <EmployeeTable
          employees={employees}
          page={page}
          totalPages={totalPages}
          onPageChange={setPage}
          onRowClick={(id) => navigate(`/employees/${id}`)}
        />
      </div>
    </MainLayout>
  );
}

export default EmployeeManagement;
