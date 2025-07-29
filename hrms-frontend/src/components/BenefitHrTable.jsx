import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitSearchForm from "./common/search/BenefitSearchForm.jsx";
import ActionDropdown from "./common/ActionDropdown.jsx";
import BenefitUpdateModal from "./modals/benefit/BenefitUpdateModal.jsx";
import { Modal, message,  } from "antd";
import getBenefitTypeDisplay from '../utils/DisplayBenefitType.js'
import { useNavigate } from 'react-router-dom';

const BenefitHRTableHeader = () => {
    const headers = [
        "Id", "Tiêu đề", "Mô tả", "Loại phúc lợi", "Ngày bắt đầu", "Ngày kết thúc", "Số lượng người tham gia tối đa", "Trạng thái hoạt động", "Ngày tạo"
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



    const handleEdit = () => {
        setIsModalOpen(true);
    };

    const handleUpdate = async (updatedData) => {
        try {
            await benefitService.update(updatedData, benefit.id);
            message.success("Cập nhật thành công!");
            setIsModalOpen(false);
            onUpdateSuccess?.(); // để reload bảng sau khi cập nhật
        } catch (err) {
            console.error("Update failed", err);
            message.error("Cập nhật thất bại");
        }
    };

    const handleDetails = () => {
        // Modal.info({
        //     title: 'Thông tin chi tiết phúc lợi',
        //     content: (
        //         <div>
        //             <p><strong>ID:</strong> {benefit.id}</p>
        //             <p><strong>Tiêu đề:</strong> {benefit.title}</p>
        //             <p><strong>Mô tả:</strong> {benefit.description}</p>
        //             <p><strong>Loại phúc lợi:</strong> {getBenefitTypeDisplay(benefit.benefitType)}</p>
        //             <p><strong>Ngày bắt đầu:</strong> {formatDate(benefit.startDate)}</p>
        //             <p><strong>Ngày kết thúc:</strong> {formatDate(benefit.endDate)}</p>
        //             <p><strong>Số lượng người tham gia tối đa:</strong> {benefit.maxParticipants}</p>
        //             <p><strong>Trạng thái:</strong> {benefit.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động'}</p>
        //             <p><strong>Ngày tạo:</strong> {formatDate(benefit.createdAt)}</p>
        //         </div>
        //     ),
        // });

        navigate(`/benefit/${benefit.id}`);
    };

    return (
        <div className="employee-table-row">
            <div className="employee-table-cell">{benefit.id}</div>
            <div className="employee-table-cell">{benefit.title}</div>
            <div className="employee-table-cell">{benefit.description}</div>
            <div className="employee-table-cell">{getBenefitTypeDisplay(benefit.benefitType)}</div>
            <div className="employee-table-cell">{formatDate(benefit.startDate)}</div>
            <div className="employee-table-cell">{formatDate(benefit.endDate)}</div>
            <div className="employee-table-cell">{benefit.maxParticipants}</div>
            <div className="employee-table-cell">{benefit.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động'}</div>
            <div className="employee-table-cell">{formatDate(benefit.createdAt)}</div>
            <div className="employee-table-cell">
                <ActionDropdown
                    onEdit={handleEdit}
                    // onView={() => Modal.info({ title: 'Chi tiết', content: benefit.detail })}
                    onView={() => {
                        if (benefit.detail) {
                            Modal.info({
                                title: 'Chi tiết phúc lợi',
                                content: benefit.detail,
                            });
                        } else {
                            Modal.confirm({
                                title: 'Chưa có chi tiết',
                                content: 'Bạn chưa nhập chi tiết cho phúc lợi này. Bạn có muốn thêm không?',
                                okText: 'Thêm ngay',
                                cancelText: 'Đóng',
                                onOk: () => {
                                    // Cách 1: Navigate sang trang update:
                                    // navigate(`/benefit/update/${benefit.id}`);

                                    // Cách 2: Mở modal cập nhật tại chỗ:
                                    setIsModalOpen(true);
                                },
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

function BenefitHrTable({ reloadKey, onForceReload }) {
    const [benefits, setBenefit] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [filters, setFilters] = useState({});

    useEffect(() => {
        const params = {
            page: pageNumber,
            size: pageSize,
            ...filters
        };

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
    }, [pageNumber, pageSize, filters, reloadKey]);

    return (

        <div className="employee-table-wrapper">



            <BenefitSearchForm
                onSearch={(newFilters) => {
                    setFilters(newFilters);
                    setPageNumber(1); // reset về page đầu khi search
                }}
            />
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
