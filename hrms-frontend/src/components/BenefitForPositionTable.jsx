import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitForPositionActionDropDown from "./common/BenefitForPositionActionDropDown.jsx";
import BenefitPositionUpdateModal from "./modals/benefit/BenefitPositionUpdateModal.jsx";
import { Modal, message, Checkbox, Button } from "antd";
import { useNavigate } from "react-router-dom";

const BenefitByPositionHeader = ({ isMultiSelectMode }) => {
    const headers = ["", "Id", "Tên vị trí", "Giá trị tính vào lương", "Chức năng"]; // Thêm cột trống cho checkbox

    return (
        <div className="employee-table-header">
            {headers.map((label, index) => (
                <div className="employee-header-cell" key={label + index}>
                    {label}
                </div>
            ))}
        </div>
    );
};

const BenefitByPositionTableRow = ({ benefit, onUpdateSuccess, isMultiSelectMode, isSelected, onSelectChange, onRowReady }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isFull, setIsFull] = useState(false);
    const navigate = useNavigate();
    const rowKey = benefit.id + "-" + benefit.positions.positionId;

    useEffect(() => {
        if (isMultiSelectMode) {
            benefitService
                .searchUnregisteredEmployees({
                    benefitId: benefit.id,
                    positionId: benefit.positions.positionId,
                })
                .then((res) => {
                    const full = res.data.length === 0;
                    setIsFull(full);
                    onRowReady(rowKey, full);
                })
                .catch((err) => {
                    console.error("Lỗi khi kiểm tra unregistered employees:", err);
                });
        }
    }, [isMultiSelectMode, benefit.id, benefit.positions.positionId, onRowReady, rowKey]);

    const handleEdit = () => {
        setIsModalOpen(true);
    };

    const handleUpdate = async (updatedData) => {
        try {
            await benefitService.updateFormula(updatedData);
            message.success("Cập nhật thành công!");
            setIsModalOpen(false);
            onUpdateSuccess();
        } catch (err) {
            console.error("Cập nhật thất bại:", err);
            message.error("Cập nhật thất bại");
        }
    };

    const handleDetails = () => {
        navigate(`/benefits-management/benefit/${benefit.id}/position/${benefit.positions.positionId}`);
    };

    return (
        <div className="employee-table-row">
            <div className="employee-table-cell">
                {isMultiSelectMode && (
                    <>
                        {isFull ? (
                            <span>full</span>
                        ) : (
                            <Checkbox
                                checked={isSelected}
                                onChange={(e) => onSelectChange(rowKey, e.target.checked)}
                            />
                        )}
                    </>
                )}
            </div>
            <div className="employee-table-cell">{benefit.positions.positionId}</div>
            <div className="employee-table-cell">{benefit.positions.positionName}</div>
            <div className="employee-table-cell">
                {benefit.benefit.benefitType === "PHU_CAP" ? (
                    benefit.positions.formulaType === "AMOUNT" ?
                        `Lương cơ bản + ${Number(benefit.positions.formulaValue).toLocaleString('vi-VN')}đ` :
                        `Lương cơ bản + ${Number(benefit.positions.formulaValue).toLocaleString('vi-VN')}%Lương cơ bản`
                ) : benefit.benefit.benefitType === "KHAU_TRU" ? (
                    benefit.positions.formulaType === "AMOUNT" ?
                        `Lương cơ bản - ${Number(benefit.positions.formulaValue).toLocaleString('vi-VN')}đ` :
                        `Lương cơ bản - ${Number(benefit.positions.formulaValue).toLocaleString('vi-VN')}%Lương cơ bản`
                ) : (
                    "không ảnh hưởng vào lương cơ bản"
                )}
            </div>
            <div className="employee-table-cell">
                <BenefitForPositionActionDropDown
                    positionName={benefit.positions.positionName}
                    benefitType={benefit.benefit.benefitType}
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

function BenefitForPositionTable({ benefitId, reloadFlag, isMultiSelectMode }) {
    const [benefits, setBenefit] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [filters, setFilters] = useState({});
    const [selectedRows, setSelectedRows] = useState([]); // Trạng thái cho các hàng được chọn
    const [selectableKeys, setSelectableKeys] = useState([]);

    const reloadData = () => {
        setFilters((prev) => ({ ...prev }));
    };

    const handleSelectChange = (rowKey, checked) => {
        setSelectedRows((prev) =>
            checked
                ? [...prev, rowKey]
                : prev.filter((key) => key !== rowKey)
        );
    };

    const handleSelectAll = () => {
        setSelectedRows([...selectableKeys]);
    };

    const handleDeselectAll = () => {
        setSelectedRows([]);
    };

    const handleRowReady = (rowKey, isFull) => {
        if (!isFull) {
            setSelectableKeys((prev) => {
                const newKeys = new Set(prev);
                newKeys.add(rowKey);
                return [...newKeys];
            });
        }
    };

    const handleConfirmRegister = () => {
        Modal.confirm({
            title: "Xác nhận đăng ký",
            content: "Bạn có chắc chắn muốn đăng ký phúc lợi cho tất cả nhân viên trong các vị trí đã chọn?",
            onOk: async () => {
                try {
                    const positionIds = selectedRows.map((rowKey) =>
                        parseInt(rowKey.split("-")[1])
                    );
                    await benefitService.quickRegisterAll({
                        benefitId: parseInt(benefitId),
                        positionIds,
                    });
                    message.success("Đã đăng ký thành công!");
                    setSelectedRows([]);
                    reloadData();
                } catch (err) {
                    console.error("Lỗi khi xử lý đăng ký:", err);
                    message.error("Đăng ký thất bại!");
                }
            },
        });
    };

    useEffect(() => {
        if (!benefitId) {
            console.warn("❗ benefitId chưa có, không gọi API");
            return;
        }

        const params = {
            page: pageNumber,
            size: pageSize,
            ...filters,
        };

        setError(null);
        setLoading(true);
        console.log("🧪 Đang gọi API:", `http://localhost:8080/api/hr/benefits/${benefitId}`, params);

        benefitService
            .getPositionRegisterationDetail(params, benefitId)
            .then((res) => {
                setSelectableKeys([]); // Reset selectable keys before setting new data
                setBenefit(res.data.content);
                setTotalElements(res.data.totalElements);
            })
            .catch((err) => {
                console.error("Lỗi khi lấy dữ liệu phúc lợi:", err);
                setError("Không thể lấy dữ liệu phúc lợi tương ứng");
            })
            .finally(() => setLoading(false));
    }, [benefitId, pageNumber, pageSize, filters, reloadFlag]);

    return (
        <div className="employee-table-wrapper">
            <BenefitByPositionHeader isMultiSelectMode={isMultiSelectMode} />
            {isMultiSelectMode && (
                <div style={{ marginBottom: 16 }}>
                    <Button
                        onClick={handleSelectAll}
                        disabled={selectableKeys.length === 0}
                        style={{ marginRight: 8 }}
                    >
                        Chọn tất cả
                    </Button>
                    <Button
                        onClick={handleDeselectAll}
                        disabled={selectedRows.length === 0}
                        style={{ marginRight: 8 }}
                    >
                        Hủy chọn tất cả
                    </Button>
                    <Button
                        type="primary"
                        onClick={handleConfirmRegister}
                        disabled={selectedRows.length === 0}
                    >
                        Xác nhận đăng ký đã chọn
                    </Button>
                </div>
            )}
            <div className="employee-table">
                {loading && <p>Đang tải...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && benefits.length === 0 && <p>Không tìm thấy phúc lợi.</p>}
                {!error && !loading && Array.isArray(benefits) &&
                    benefits.flatMap((benefit) =>
                        benefit.positions.map((position, index) => (
                            <BenefitByPositionTableRow
                                key={`${benefit.id}-${index}`}
                                benefit={{
                                    id: benefit.id,
                                    positionId: position.positionId,
                                    positions: position,
                                    benefit: benefit,
                                }}
                                onUpdateSuccess={reloadData}
                                isMultiSelectMode={isMultiSelectMode}
                                isSelected={selectedRows.includes(`${benefit.id}-${position.positionId}`)}
                                onSelectChange={handleSelectChange}
                                onRowReady={handleRowReady}
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