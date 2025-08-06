import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitSearchForm from "./common/search/BenefitSearchForm.jsx";
import BenefitDetailActionDropdown from "./common/BenefitDetailActionDropdown.jsx";
import BenefitUpdateModal from "./modals/benefit/BenefitUpdateModal.jsx";
import { Modal, message,  } from "antd";
// import getBenefitTypeDisplay from '../utils/DisplayBenefitType.js'
import { useNavigate } from 'react-router-dom';
import BenefitForPositionForEmployeeActionDropdown from "./common/BenefitForPositionForEmployeeActionDropdown.jsx";
import employeeService from "../services/employeeService.js";

const BenefitForPositionForEmployeeTableHeader = () => {
    const headers = [
        "Id", "Tên nhân viên", "Email", "Lương cơ bản",  "Ngày đăng kí", "Vị trí", "Chức năng"
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

const BenefitForPositionForEmployeeTableRow = ({ benefit,positionId,registration, onUpdateSuccess, position }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();
    const [error, setError] = useState(null);
    const [isSalaryModalOpen, setIsSalaryModalOpen] = useState(false);
    const [employee, setEmployee] = useState(null);

    // const targetPosition = benefit.positions.find(pos => pos.positionId === positionId);
    console.log("employeeId:", registration.employee.employeeId)
    //
    // employeeService.getEmployeeById(registration.employee.employeeId).then(res => {
    //     setEmployee(res.data);
    //     console.log("employee:", employee);
    // }).catch(err => {
    //
    // }).finally()

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
            // console.log('Full error object:', err);
            // console.log('Response data:', err.response?.data);
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


            setError(errorMsg);          // nếu bạn muốn hiển thị ở UI
            message.error(errorMsg);     // thông báo popup
            // console.error("Update failed", err);
            // message.error("Cập nhật thất bại");
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

        navigate(`/benefits-management/benefit/${benefit.id}`);
    };

    return (
        <div className="employee-table-row">
            <div className="employee-table-cell">{registration.employee?.employeeId }</div>
            <div className="employee-table-cell">{registration.employee?.employeeName}</div>
            <div className="employee-table-cell">{registration.employee?.email}</div>
            <div className="employee-table-cell"> {Number(registration.employee.basicSalary).toLocaleString('vi-VN')}đ</div>
            <div className="employee-table-cell">{formatDate(registration.registeredAt)}</div>
            <div className="employee-table-cell">{position.positionName}</div>
            {/*<div className="employee-table-cell">{employee.departmentName}</div>*/}
            {/*<div className="employee-table-cell">{formatDate(benefit.startDate)}</div>*/}
            {/*<div className="employee-table-cell">{formatDate(benefit.endDate)}</div>*/}
            {/*<div className="employee-table-cell">{benefit.maxParticipants}</div>*/}
            {/*<div className="employee-table-cell">{benefit.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động'}</div>*/}
            {/*<div className="employee-table-cell">{formatDate(benefit.createdAt)}</div>*/}
            <div className="employee-table-cell">
                <BenefitForPositionForEmployeeActionDropdown
                    onEdit={handleEdit}
                    onDetails={() => setIsSalaryModalOpen(true)}
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
            .getEmployeeByPositionAndBenefit(benefitId, positionId, params)
            .then((res) => {
                setBenefit(res.data.content);
                 console.log("benefits:", benefits);
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



            {/*<BenefitSearchForm*/}
            {/*    onSearch={(newFilters) => {*/}
            {/*        setFilters(newFilters);*/}
            {/*        setPageNumber(1); // reset về page đầu khi search*/}
            {/*    }}*/}
            {/*/>*/}
            <BenefitForPositionForEmployeeTableHeader

            />

            <div className="employee-table">


                {loading && <p>Loading...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && benefits.length === 0 && <p>No benefits found.</p>}
                {!error && !loading && Array.isArray(benefits) && benefits.flatMap((benefit) => {
                    const targetPosition = benefit.positions.find(pos => pos.positionId === Number(positionId));
                    const registrations = targetPosition?.registrations || [];

                    return registrations.map((reg) => (
                        <BenefitForPositionForEmployeeTableRow
                            key={`${benefit.id}-${reg.id}`}
                            benefit={benefit}
                            position={targetPosition}
                            registration={reg} // 👈 truyền từng phần tử registration
                            onUpdateSuccess={onForceReload}
                        />
                    ));
                })}
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

export default BenefitForPositionForEmployeeTable;
