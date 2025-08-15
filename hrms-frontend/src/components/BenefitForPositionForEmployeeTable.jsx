import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import { Modal, message, Pagination, Button } from "antd";
import BenefitForPositionForEmployeeActionDropdown from "./common/BenefitForPositionForEmployeeActionDropdown.jsx";
import BenefitUpdateModal from "./modals/benefit/BenefitUpdateModal.jsx";
import { useNavigate } from 'react-router-dom';

/** HEADER */
const BenefitForPositionForEmployeeTableHeader = ({ sortConfig, onSort, bulkMode }) => {
    const baseHeaders = [
        { label: "Id", key: "id" },
        { label: "Tên nhân viên", key: "name" },
        { label: "Email", key: "email" },
        { label: "Lương cơ bản", key: "salary", align: "center" },
        { label: "Ngày đăng kí", key: "date", align: "center" },
        { label: "Vị trí", key: "position" },
        { label: "Chức năng", key: null }
    ];
    const headers = bulkMode ? [{ label: "", key: null }, ...baseHeaders] : baseHeaders;

    const getSortSymbol = (key) => {
        if (sortConfig.key !== key) return null;
        return sortConfig.direction === "asc" ? "▲" : sortConfig.direction === "desc" ? "▼" : null;
    };

    return (
        <div
            className="employee-table-header"
            style={{
                display: "grid",
                gridTemplateColumns: bulkMode
                    ? "56px 1fr 2fr 2fr 2fr 2fr 2fr 2fr"
                    : "1fr 2fr 2fr 2fr 2fr 2fr 2fr",
                alignItems: "center",
            }}
        >
            {headers.map(({ label, key, align }, idx) => (
                <div
                    className="employee-header-cell"
                    key={`${label}-${idx}`}
                    onClick={() => key && onSort(key)}
                    style={{
                        cursor: key ? "pointer" : "default",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        gap: "4px",
                        padding: "8px",
                        textAlign: align || "left",
                    }}
                >
                    <span>{label}</span>
                    {key && getSortSymbol(key) && (
                        <span style={{ fontSize: "12px", color: "black" }}>
                            {getSortSymbol(key)}
                        </span>
                    )}
                </div>
            ))}
        </div>
    );
};


/** ROW */
const BenefitForPositionForEmployeeTableRow = ({
                                                   benefit,
                                                   registration,
                                                   onUpdateSuccess,
                                                   position,
                                                   selectedEmployees,
                                                   setSelectedEmployees,
                                                   bulkMode
                                               }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();

    const employeeId = Number(registration?.employee?.employeeId);
    const isSelected = selectedEmployees.includes(employeeId);

    const toggleSelect = () => {
        setSelectedEmployees((prev) =>
            isSelected ? prev.filter((id) => id !== employeeId) : [...prev, employeeId]
        );
    };

    const handleEdit = () => setIsModalOpen(true);

    const handleUpdate = async (updatedData) => {
        try {
            await benefitService.update(updatedData, benefit.id);
            message.success("Cập nhật thành công!");
            setIsModalOpen(false);
            onUpdateSuccess?.();
        } catch (err) {
            const msg = err.response?.data?.message || err.message || "Cập nhật thất bại";
            message.error(msg);
        }
    };

    return (
        <>
            <div
                className="employee-table-row"
                style={{
                    display: "grid",
                    gridTemplateColumns: bulkMode
                        ? "40px 1fr 2fr 2fr 2fr 2fr 2fr 2fr"
                        : "1fr 2fr 2fr 2fr 2fr 2fr 2fr",
                    alignItems: "center",
                }}
            >
                {bulkMode && (
                    <div className="checkbox-cell">
                        <input type="checkbox" className="bulk-checkbox" checked={isSelected} onChange={toggleSelect} />
                    </div>
                )}

                <div className="employee-table-cell" style={{ padding: "8px", textAlign: "center" }}>
                    {registration.employee?.employeeId}
                </div>
                <div className="employee-table-cell" style={{ padding: "8px" }}>
                    {registration.employee?.employeeName}
                </div>
                <div className="employee-table-cell" style={{ padding: "8px" }}>
                    {registration.employee?.email}
                </div>
                <div className="employee-table-cell" style={{ padding: "8px", textAlign: "right" }}>
                    {Number(registration.employee.basicSalary).toLocaleString('vi-VN')}đ
                </div>
                <div className="employee-table-cell" style={{ padding: "8px", textAlign: "center" }}>
                    {formatDate(registration.registeredAt)}
                </div>
                <div className="employee-table-cell" style={{ padding: "8px" }}>
                    {position.positionName}
                </div>
                <div className="employee-table-cell" style={{ padding: "8px", textAlign: "center" }}>
                    <BenefitForPositionForEmployeeActionDropdown
                        onEdit={handleEdit}
                        onView={() => {
                            if (benefit.detail) {
                                Modal.info({ title: 'Chi tiết phúc lợi', content: benefit.detail });
                            } else {
                                Modal.confirm({
                                    title: 'Chưa có chi tiết',
                                    content: 'Bạn chưa nhập chi tiết cho phúc lợi này. Bạn có muốn thêm không?',
                                    okText: 'Thêm ngay',
                                    cancelText: 'Đóng',
                                    onOk: () => setIsModalOpen(true),
                                });
                            }
                        }}
                        onDelete={() =>
                            Modal.confirm({
                                title: "Bạn có chắc chắn muốn xóa?",
                                onOk: async () => {
                                    try {
                                        await benefitService.unRegister(
                                            benefit.id,
                                            position.positionId,
                                            registration.employee.employeeId
                                        );
                                        message.success("Đã xóa thành công!");
                                        onUpdateSuccess();
                                    } catch {
                                        message.error("Xóa thất bại");
                                    }
                                },
                            })
                        }
                    />
                </div>
            </div>

            <BenefitUpdateModal
                open={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                onSubmit={handleUpdate}
                initialData={benefit}
            />
        </>
    );
};

/** TABLE */
function BenefitForPositionForEmployeeTable({
                                                benefitId,
                                                positionId,
                                                reloadKey,
                                                onForceReload,
                                                selectedEmployees = [],
                                                setSelectedEmployees = () => {},
                                                bulkMode = false,
                                                onRequestBulkDelete = () => {},
                                                // NEW: nhận searchText từ parent
                                                searchText = ""
                                            }) {
    const [benefits, setBenefits] = useState([]);
    const [registrationsToRender, setRegistrationsToRender] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [sortConfig, setSortConfig] = useState({ key: null, direction: null });

    useEffect(() => {
        setLoading(true);
        benefitService
            .getEmployeeByPositionAndBenefit(benefitId, positionId, {})
            .then((res) => {
                const fetchedBenefits = res.data.content || [];
                setBenefits(fetchedBenefits);

                const allRegs = [];
                fetchedBenefits.forEach((benefit) => {
                    const targetPosition = benefit.positions?.find((pos) => pos.positionId === Number(positionId));
                    if (targetPosition?.registrations) {
                        targetPosition.registrations.forEach((reg) => {
                            allRegs.push({ benefit, registration: reg, position: targetPosition });
                        });
                    }
                });
                setRegistrationsToRender(allRegs);
            })
            .catch((err) => {
                console.error("Failed to fetch benefits", err);
                setError("Không thể lấy dữ liệu phúc lợi tương ứng");
            })
            .finally(() => setLoading(false));
    }, [reloadKey, benefitId, positionId]);

    // Reset page khi đổi từ khóa tìm kiếm
    useEffect(() => {
        setPageNumber(1);
    }, [searchText]);

    // Khi thoát bulkMode hoặc reload, bỏ chọn
    useEffect(() => {
        if (!bulkMode) setSelectedEmployees([]);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [bulkMode, reloadKey]);

    const handleSort = (key) => {
        setPageNumber(1);
        setSortConfig((prev) => {
            if (prev.key === key) {
                const nextDirection = prev.direction === 'asc' ? 'desc' : prev.direction === 'desc' ? null : 'asc';
                return { key, direction: nextDirection };
            }
            return { key, direction: 'asc' };
        });
    };

    // Lọc theo searchText (từ parent)
    let filteredData = registrationsToRender.filter(({ registration }) => {
        const keyword = (searchText || "").toLowerCase();
        const name = registration.employee?.employeeName?.toLowerCase() || "";
        const email = registration.employee?.email?.toLowerCase() || "";
        return name.includes(keyword) || email.includes(keyword);
    });

    // Sort
    if (sortConfig.key && sortConfig.direction) {
        filteredData = [...filteredData].sort((a, b) => {
            const { key } = sortConfig;
            const order = sortConfig.direction === 'asc' ? 1 : -1;

            const getValue = (obj) => {
                switch (key) {
                    case 'id': return obj.registration.employee?.employeeId;
                    case 'name': return obj.registration.employee?.employeeName?.toLowerCase() || "";
                    case 'email': return obj.registration.employee?.email?.toLowerCase() || "";
                    case 'salary': return Number(obj.registration.employee?.basicSalary);
                    case 'date': return new Date(obj.registration.registeredAt);
                    case 'position': return obj.position?.positionName?.toLowerCase() || "";
                    default: return "";
                }
            };

            const aValue = getValue(a);
            const bValue = getValue(b);

            if (aValue < bValue) return -1 * order;
            if (aValue > bValue) return 1 * order;
            return 0;
        });
    }

    const startIndex = (pageNumber - 1) * pageSize;
    const currentPageData = filteredData.slice(startIndex, startIndex + pageSize);

    // IDs theo bộ lọc để dùng cho “Chọn tất cả”
    const allFilteredIds = filteredData.map(({ registration }) => Number(registration.employee?.employeeId));

    const selectAllFiltered = () => {
        setSelectedEmployees(Array.from(new Set([...selectedEmployees, ...allFilteredIds])));
    };

    const clearSelection = () => setSelectedEmployees([]);

    return (
        <div className="employee-table-wrapper">
            {/* NEW: Chỉ còn thanh hành động khi bulkMode = true (search box đã chuyển ra parent) */}
            {bulkMode && (
                <div className="bulk-actions">
                    <Button onClick={selectAllFiltered}>Chọn tất cả</Button>
                    <Button onClick={clearSelection} disabled={selectedEmployees.length === 0}>Hủy chọn tất cả</Button>
                    <Button
                        type="primary"
                        danger
                        disabled={selectedEmployees.length === 0}
                        onClick={onRequestBulkDelete}
                    >
                        Xóa đã chọn ({selectedEmployees.length})
                    </Button>
                </div>
            )}


            <BenefitForPositionForEmployeeTableHeader
                sortConfig={sortConfig}
                onSort={handleSort}
                bulkMode={bulkMode}
            />

            <div className="employee-table">
                {loading && <p>Loading...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && !error && currentPageData.length === 0 && <p>Không tìm thấy dữ liệu.</p>}
                {!loading && !error && currentPageData.map(({ benefit, registration, position }) => (
                    <BenefitForPositionForEmployeeTableRow
                        key={`${benefit.id}-${registration.id}`}
                        benefit={benefit}
                        registration={registration}
                        position={position}
                        onUpdateSuccess={onForceReload}
                        selectedEmployees={selectedEmployees}
                        setSelectedEmployees={setSelectedEmployees}
                        bulkMode={bulkMode}
                    />
                ))}
            </div>

            {!loading && filteredData.length > 0 && (
                <div style={{ marginTop: 16, textAlign: "right" }}>
                    <Pagination
                        current={pageNumber}
                        pageSize={pageSize}
                        total={filteredData.length}
                        showSizeChanger
                        pageSizeOptions={['5', '10', '20', '50']}
                        onChange={(page, size) => {
                            if (size !== pageSize) setPageNumber(1);
                            else setPageNumber(page);
                            setPageSize(size);
                        }}
                        showTotal={(total, range) => `${range[0]}–${range[1]} trên tổng ${total} nhân viên`}
                    />
                </div>
            )}
        </div>
    );
}

export default BenefitForPositionForEmployeeTable;
