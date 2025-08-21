import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
// REMOVED: import BenefitSearchForm from "./common/search/BenefitSearchForm.jsx";
import BenefitDetailActionDropdown from "./common/BenefitDetailActionDropdown.jsx";
import BenefitUpdateModal from "./modals/benefit/BenefitUpdateModal.jsx";
import { Modal, message } from "antd";
import getBenefitTypeDisplay from '../utils/DisplayBenefitType.js'
import { useNavigate } from 'react-router-dom';

const BenefitHRTableHeader = () => {
    const headers = [
        "Id", "Tiêu đề", "Mô tả", "Loại phúc lợi", "Trạng thái hoạt động", "Ngày tạo", "Chức năng"
    ];
    return (
        <div className="employee-table-header">
            {headers.map(label =>
                <div className="employee-header-cell" key={label}>
                    {label}
                </div>
            )}
        </div>
    );
};

const BenefitHRTableRow = ({ benefit, onUpdateSuccess }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();
    const [error, setError] = useState(null);

    const handleEdit = () => setIsModalOpen(true);

    const handleUpdate = async (updatedData) => {
        try {
            await benefitService.update(updatedData, benefit.id);
            message.success("Cập nhật thành công!");
            setIsModalOpen(false);
            onUpdateSuccess?.();
        } catch (err) {
            console.error(err);
            let errorMsg = 'Đã có lỗi xảy ra.';
            if (err.response?.data) {
                const data = err.response.data;
                errorMsg = data.message || 'Lỗi không xác định từ server.';
            } else if (err.request) {
                errorMsg = 'Không nhận được phản hồi từ máy chủ.';
            } else {
                errorMsg = err.message || 'Cập nhật thất bại';
            }
            setError(errorMsg);
            message.error(errorMsg);
        }
    };

    const handleDetails = () => navigate(`/benefits-management/benefit/${benefit.id}`);

    return (
        <div className="employee-table-row">
            <div className="employee-table-cell">{benefit.id}</div>
            <div className="employee-table-cell">{benefit.title}</div>
            <div className="employee-table-cell">{benefit.description}</div>
            <div className="employee-table-cell">{getBenefitTypeDisplay(benefit.benefitType)}</div>
            <div className="employee-table-cell">{benefit.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động'}</div>
            <div className="employee-table-cell">{formatDate(benefit.createdAt)}</div>
            <div className="employee-table-cell">
                <BenefitDetailActionDropdown
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
                                await benefitService.delete(benefit.id);
                                message.success("Đã xóa thành công!");
                                onUpdateSuccess();
                            } catch {
                                message.error("Xóa thất bại");
                            }
                        }
                    })}
                    canDelete={!benefit.isActive}
                    onDetails={handleDetails}
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

// NEW: nhận filters từ props (mặc định rỗng)
function BenefitHrTable({ reloadKey, onForceReload, filters = {} }) {
    const [benefits, setBenefit] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);

    // NEW: mỗi khi filters thay đổi -> reset về trang 1
    useEffect(() => {
        setPageNumber(1);
    }, [JSON.stringify(filters)]);

    useEffect(() => {
        const params = {
            page: pageNumber,
            size: pageSize,
            ...filters, // dùng filters từ props
        };

        setLoading(true);
        setError(null);

        benefitService
            .getAll(params)
            .then((res) => {
                setBenefit(res.data.content);
                setTotalElements(res.data.totalElements);
            })
            .catch((err) => {
                console.error("Failed to fetch benefits", err);
                setError("Không thể lấy dữ liệu phúc lợi tương ứng");
            })
            .finally(() => setLoading(false));
    }, [pageNumber, pageSize, JSON.stringify(filters), reloadKey]);

    return (
        <div className="employee-table-wrapper">
            {/* REMOVED: <BenefitSearchForm .../> */}
            <BenefitHRTableHeader />

            <div className="employee-table">
                {loading && <p>Loading...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && benefits.length === 0 && <p>No benefits found.</p>}
                {!error && !loading && Array.isArray(benefits) && benefits.map((benefit) => (
                    <BenefitHRTableRow
                        key={benefit.id}
                        benefit={benefit}
                        onUpdateSuccess={onForceReload}
                    />
                ))}
            </div>

            {!loading && totalElements > 0 && (
                <Paging
                    totalElements={totalElements}
                    pageNumber={pageNumber}
                    pageSize={pageSize}
                    setPageNumber={setPageNumber}
                    setPageSize={setPageSize}
                />
            )}
        </div>
    );
}

export default BenefitHrTable;
