import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitForPositionActionDropDown from "./common/BenefitForPositionActionDropDown.jsx";
import BenefitPositionUpdateModal from "./modals/benefit/BenefitPositionUpdateModal.jsx";
import { Modal, message, Checkbox, Button, Input } from "antd";
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

const BenefitByPositionTableRow = ({ benefit, onUpdateSuccess, isMultiSelectMode, isSelected, onSelectChange, isFull }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();
    const rowKey = benefit.id + "-" + benefit.positions.positionId;

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

function BenefitForPositionTable({ benefitId, reloadFlag, isMultiSelectMode, onReload }) {
    const [benefits, setBenefits] = useState([]); // Dữ liệu gốc từ API
    const [filteredBenefits, setFilteredBenefits] = useState([]); // Dữ liệu sau khi lọc (flatMap thành danh sách positions)
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [searchTerm, setSearchTerm] = useState(""); // Từ khóa tìm kiếm
    const [selectedRows, setSelectedRows] = useState([]); // Trạng thái cho các hàng được chọn
    const [selectableKeys, setSelectableKeys] = useState([]);
    const [positionFullMap, setPositionFullMap] = useState({});

    const reloadData = () => {
        fetchData();
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
                    onReload();
                } catch (err) {
                    console.error("Lỗi khi xử lý đăng ký:", err);
                    message.error("Đăng ký thất bại!");
                }
            },
        });
    };

    useEffect(() => {
        if (isMultiSelectMode) {
            const allPositions = benefits.flatMap(b => b.positions.map(p => p.positionId));
            if (allPositions.length === 0) return;

            Promise.all(
                allPositions.map(pid =>
                    benefitService.searchUnregisteredEmployees({
                        benefitId: parseInt(benefitId),
                        positionId: pid,
                    }).then(res => ({ pid, isFull: res.data.length === 0 }))
                )
            )
                .then(results => {
                    const map = results.reduce((acc, { pid, isFull }) => {
                        acc[pid] = isFull;
                        return acc;
                    }, {});
                    setPositionFullMap(map);
                    const selectable = allPositions
                        .filter(pid => !map[pid])
                        .map(pid => `${benefitId}-${pid}`);
                    setSelectableKeys(selectable);
                })
                .catch(err => {
                    console.error("Lỗi khi kiểm tra unregistered employees:", err);
                });
        } else {
            setPositionFullMap({});
            setSelectedRows([]);
            setSelectableKeys([]);
        }
    }, [isMultiSelectMode, benefitId, benefits]);

    const fetchData = () => {
        if (!benefitId) {
            console.warn("❗ benefitId chưa có, không gọi API");
            return;
        }

        setError(null);
        setLoading(true);
        console.log("🧪 Đang gọi API:", `http://localhost:8080/api/hr/benefits/${benefitId}`);

        // Gọi API mà không có phân trang để lấy tất cả dữ liệu
        benefitService
            .getPositionRegisterationDetail({}, benefitId) // Giả sử API hỗ trợ lấy tất cả nếu không có params page/size
            .then((res) => {
                setBenefits(res.data.content || res.data); // Điều chỉnh nếu API trả về khác
                setTotalElements(res.data.totalElements || res.data.length);
            })
            .catch((err) => {
                console.error("Lỗi khi lấy dữ liệu phúc lợi:", err);
                setError("Không thể lấy dữ liệu phúc lợi tương ứng");
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchData();
    }, [benefitId, reloadFlag]);

    // Xử lý tìm kiếm và phân trang ở client-side
    useEffect(() => {
        // FlatMap dữ liệu thành danh sách positions để dễ lọc
        const allPositions = benefits.flatMap((benefit) =>
            benefit.positions.map((position) => ({
                id: benefit.id,
                positionId: position.positionId,
                positions: position,
                benefit: benefit,
            }))
        );

        // Lọc theo searchTerm (tên vị trí)
        const filtered = allPositions.filter((item) =>
            item.positions.positionName.toLowerCase().includes(searchTerm.toLowerCase())
        );

        setFilteredBenefits(filtered);
        setTotalElements(filtered.length); // Cập nhật total sau lọc
        setPageNumber(1); // Reset về trang 1 khi lọc thay đổi
    }, [searchTerm, benefits]);

    // Dữ liệu hiển thị cho trang hiện tại
    const currentPageData = filteredBenefits.slice(
        (pageNumber - 1) * pageSize,
        pageNumber * pageSize
    );

    return (
        <div className="employee-table-wrapper">
            <div style={{ marginBottom: 16 }}>
                <Input
                    placeholder="Tìm kiếm theo tên vị trí"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    style={{ width: 300 }}
                />
            </div>
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
                {!loading && filteredBenefits.length === 0 && <p>Không tìm thấy phúc lợi.</p>}
                {!error && !loading && currentPageData.map((benefitItem, index) => (
                    <BenefitByPositionTableRow
                        key={`${benefitItem.id}-${index}`}
                        benefit={benefitItem}
                        onUpdateSuccess={reloadData}
                        isMultiSelectMode={isMultiSelectMode}
                        isSelected={selectedRows.includes(`${benefitItem.id}-${benefitItem.positions.positionId}`)}
                        onSelectChange={handleSelectChange}
                        isFull={positionFullMap[benefitItem.positions.positionId] || false}
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

export default BenefitForPositionTable;