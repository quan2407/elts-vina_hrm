import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitSearchForm from "./common/search/BenefitSearchForm.jsx";
import BenefitDetailActionDropdown from "./common/BenefitDetailActionDropdown.jsx";
import BenefitPositionUpdateModal from "./modals/benefit/BenefitPositionUpdateModal.jsx";
import { Modal, message } from "antd";
import { useNavigate } from 'react-router-dom';
import BenefitForPositionActionDropDown from "./common/BenefitForPositionActionDropDown.jsx";

const BenefitByPositionHeader = () => {
    const headers = [
        "Id", "Tên vị trí", "Giá trị tính vào lương", "Chức năng",
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

const BenefitByPositionTableRow = ({ benefit, onUpdateSuccess }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();



    const handleEdit = () => {
        setIsModalOpen(true);
    };

    const handleUpdate = async (updatedData) => {
        try {
            console.log("á", updatedData);
            await benefitService.updateFormula(updatedData);
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

        navigate(`/benefits-management/benefit/${benefit.id}/position/${benefit.positions.positionId}`);
    };

    return (
        <div className="employee-table-row">
            <div className="employee-table-cell">{benefit.positions.positionId}</div>
            <div className="employee-table-cell">{benefit.positions.positionName}</div>
            <div className="employee-table-cell">
                {benefit.benefit.benefitType === "PHU_CAP" ? (
                    benefit.positions.formulaType === "AMOUNT" ?
                        `Lương cơ bản + ${benefit.positions.formulaValue}` :
                        `Lương cơ bản + ${benefit.positions.formulaValue}%.Lương cơ bản`
                ) : benefit.benefit.benefitType === "KHAU_TRU" ? (
                    benefit.positions.formulaType === "AMOUNT" ?
                        `Lương cơ bản - ${benefit.positions.formulaValue}` :
                        `Lương cơ bản - ${benefit.positions.formulaValue}%.Lương cơ bản`
                ) : (
                    "-"
                )}
            </div>
            {/*<div className="employee-table-cell">{getBenefitTypeDisplay(benefit.benefitType)}</div>*/}
            {/*<div className="employee-table-cell">{formatDate(benefit.startDate)}</div>*/}
            {/*<div className="employee-table-cell">{formatDate(benefit.endDate)}</div>*/}
            {/*<div className="employee-table-cell">{benefit.maxParticipants}</div>*/}
            {/*<div className="employee-table-cell">{benefit.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động'}</div>*/}
            {/*<div className="employee-table-cell">{formatDate(benefit.createdAt)}</div>*/}
            <div className="employee-table-cell">
                <BenefitForPositionActionDropDown
                    positionName={benefit.positions.positionName}
                    onEdit={handleEdit}
                    onView={() => Modal.info({ title: 'Chi tiết', content: benefit.detail })}
                    onDelete={() =>
                        Modal.confirm({
                            title: "Bạn có chắc chắn muốn xóa?",
                            onOk: async () => {
                                try {
                                    await benefitService.unassignPositionsFromBenefit(
                                        benefit.id,
                                        benefit.positions.positionId
                                    );
                                    message.success("Đã xóa thành công!");
                                    onUpdateSuccess();
                                } catch {
                                    message.error("Xóa thất bại");
                                }
                            },
                        })
                    }
                    onDetails={handleDetails}
                />
            </div>
            <BenefitPositionUpdateModal
                open={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                onSubmit={handleUpdate}
                initialData={benefit}
            />
        </div>
    );
};

function BenefitForPositionTable({ benefitId }) {
    const [benefits, setBenefit] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [filters, setFilters] = useState({});
    console.log("✅ benefitId từ URL:", benefitId);
    console.log(localStorage.getItem("accessToken"));
    useEffect(() => {
        if (!benefitId) {
            console.warn("❗ benefitId chưa có, không gọi API");
            return;
        }

        const params = {
            page: pageNumber,
            size: pageSize,
            ...filters
        };



        setError(null);
        setLoading(true);
        console.log("🧪 Đang gọi API:", `http://localhost:8080/api/hr/benefits/${benefitId}`, params);

        benefitService
            .getPositionRegisterationDetail(params, benefitId)
            .then((res) => {

                setBenefit(res.data.content);

                setTotalElements(res.data.totalElements);

            })
            .catch((err) => {
                console.error("Lỗi khi fetch benefit", err);
                setError("Không thể lấy dữ liệu phúc lợi tương ứng");
            })
            .finally(() => setLoading(false));
    }, [benefitId,pageNumber, pageSize, filters]);


    return (
        <div className="employee-table-wrapper">

            {/*<BenefitSearchForm*/}
            {/*    onSearch={(newFilters) => {*/}
            {/*        setFilters(newFilters);*/}
            {/*        setPageNumber(1); // reset về page đầu khi search*/}
            {/*    }}*/}
            {/*/>*/}
            <BenefitByPositionHeader />

            <div className="employee-table">


                {loading && <p>Loading...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && benefits.length === 0 && <p>No benefits found.</p>}
                {!error && !loading && Array.isArray(benefits) &&
                    benefits.flatMap((benefit) =>
                        benefit.positions.map((position, index) => (
                            <BenefitByPositionTableRow
                                key={`${benefit.id}-${index}`}
                                benefit={{
                                    id: benefit.id,
                                    positionId: position.positionId,
                                    positions: position,
                                    benefit: benefit
                                }}
                                onUpdateSuccess={() => setPageNumber(prev => prev)}
                            />
                        ))
                    )}
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

export default BenefitForPositionTable;
