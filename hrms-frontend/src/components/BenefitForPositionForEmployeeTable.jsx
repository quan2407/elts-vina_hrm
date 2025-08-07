import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import { Modal, message, Pagination } from "antd";
import BenefitForPositionForEmployeeActionDropdown from "./common/BenefitForPositionForEmployeeActionDropdown.jsx";
import BenefitUpdateModal from "./modals/benefit/BenefitUpdateModal.jsx";
import { useNavigate } from 'react-router-dom';

const BenefitForPositionForEmployeeTableHeader = ({ sortConfig, onSort }) => {
    const headers = [
        { label: "Id", key: "id" },
        { label: "Tên nhân viên", key: "name" },
        { label: "Email", key: "email" },
        { label: "Lương cơ bản", key: "salary" },
        { label: "Ngày đăng kí", key: "date" },
        { label: "Vị trí", key: "position" },
        { label: "Chức năng", key: null }
    ];

    const getSortSymbol = (key) => {
        if (sortConfig.key !== key) return null;
        return sortConfig.direction === "asc" ? "▲" : sortConfig.direction === "desc" ? "▼" : null;
    };

    return (
        <div className="employee-table-header">
            {headers.map(({ label, key }) => (
                <div
                    className="employee-header-cell"
                    key={label}
                    onClick={() => key && onSort(key)}
                    style={{
                        cursor: key ? "pointer" : "default",
                        display: "flex",
                        alignItems: "center",
                        gap: "4px"
                    }}
                >
                    <span>{label}</span>
                    {getSortSymbol(key) && (
                        <span style={{ fontSize: "12px", color: "black" }}>
                            {getSortSymbol(key)}
                        </span>
                    )}
                </div>
            ))}
        </div>
    );
};

const BenefitForPositionForEmployeeTableRow = ({ benefit, registration, onUpdateSuccess, position }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();

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
        <div className="employee-table-row">
            <div className="employee-table-cell">{registration.employee?.employeeId}</div>
            <div className="employee-table-cell">{registration.employee?.employeeName}</div>
            <div className="employee-table-cell">{registration.employee?.email}</div>
            <div className="employee-table-cell">
                {Number(registration.employee.basicSalary).toLocaleString('vi-VN')}đ
            </div>
            <div className="employee-table-cell">{formatDate(registration.registeredAt)}</div>
            <div className="employee-table-cell">{position.positionName}</div>
            <div className="employee-table-cell">
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
                    onDelete={() => Modal.confirm({
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
                        }
                    })}
                />
            </div>
            <BenefitUpdateModal
                open={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                onSubmit={handleUpdate}
                initialData={benefit}
            />
        </div>
    );
};

function BenefitForPositionForEmployeeTable({ benefitId, positionId, reloadKey, onForceReload }) {
    const [benefits, setBenefits] = useState([]);
    const [registrationsToRender, setRegistrationsToRender] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchText, setSearchText] = useState("");
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
                fetchedBenefits.forEach(benefit => {
                    const targetPosition = benefit.positions?.find(pos => pos.positionId === Number(positionId));
                    if (targetPosition?.registrations) {
                        targetPosition.registrations.forEach(reg => {
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

    let filteredData = registrationsToRender.filter(({ registration }) => {
        const keyword = searchText.toLowerCase();
        const name = registration.employee?.employeeName?.toLowerCase() || "";
        const email = registration.employee?.email?.toLowerCase() || "";
        return name.includes(keyword) || email.includes(keyword);
    });

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

    return (
        <div className="employee-table-wrapper">
            <div className="filter-wrapper" style={{ marginBottom: 16 }}>
                <input
                    type="text"
                    placeholder="Tìm theo tên hoặc email..."
                    value={searchText}
                    onChange={(e) => {
                        setSearchText(e.target.value);
                        setPageNumber(1);
                    }}
                    style={{ padding: "8px", width: "300px", borderRadius: "4px", border: "1px solid #ccc" }}
                />
            </div>

            <BenefitForPositionForEmployeeTableHeader
                sortConfig={sortConfig}
                onSort={handleSort}
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
