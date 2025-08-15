import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitForPositionActionDropDown from "./common/BenefitForPositionActionDropDown.jsx";
import BenefitPositionUpdateModal from "./modals/benefit/BenefitPositionUpdateModal.jsx";
import { Modal, message, Checkbox, Button } from "antd";
import { useNavigate } from "react-router-dom";

const BenefitByPositionHeader = ({ isMultiSelectMode }) => {
    const headers = ["", "Id", "Tên vị trí", "Giá trị", "Số lượng đăng kí", "Chức năng"];
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

const BenefitByPositionTableRow = ({
                                       benefit,
                                       onUpdateSuccess,
                                       isMultiSelectMode,
                                       isSelected,
                                       onSelectChange,
                                       isFull,
                                       stats, // NEW: nhận stats từ bảng
                                   }) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const navigate = useNavigate();
    const rowKey = benefit.id + "-" + benefit.positions.positionId;

    const handleEdit = () => setIsModalOpen(true);

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
                            <Checkbox checked={isSelected} onChange={(e) => onSelectChange(rowKey, e.target.checked)} />
                        )}
                    </>
                )}
            </div>

            <div className="employee-table-cell">{benefit.positions.positionId}</div>
            <div className="employee-table-cell">{benefit.positions.positionName}</div>

            <div className="employee-table-cell">
                {benefit.benefit.benefitType === "PHU_CAP" ? (
                    benefit.positions.formulaType === "AMOUNT"
                        ? `${Number(benefit.positions.formulaValue).toLocaleString("vi-VN")}đ`
                        : ` ${Number(benefit.positions.formulaValue).toLocaleString("vi-VN")}%Lương cơ bản`
                ) : benefit.benefit.benefitType === "KHAU_TRU" ? (
                    benefit.positions.formulaType === "AMOUNT"
                        ? `${Number(benefit.positions.formulaValue).toLocaleString("vi-VN")}đ`
                        : ` ${Number(benefit.positions.formulaValue).toLocaleString("vi-VN")}%Lương cơ bản`
                ) : (
                    "0"
                )}
            </div>

            <div className="employee-table-cell">
                {stats ? `${stats.totalRegistered}/${stats.totalEmployees}` : "..."}
            </div>

            <div className="employee-table-cell">
                <BenefitForPositionActionDropDown
                    positionName={benefit.positions.positionName}
                    benefitType={benefit.benefit.benefitType}
                    onEdit={handleEdit}
                    onView={() => Modal.info({ title: "Chi tiết", content: benefit.detail })}
                    onDelete={() =>
                        Modal.confirm({
                            title: "Bạn có chắc chắn muốn xóa?",
                            onOk: async () => {
                                try {
                                    await benefitService.unassignPositionsFromBenefit(benefit.id, benefit.positions.positionId);
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

function BenefitForPositionTable({
                                     benefitId,
                                     reloadFlag,
                                     isMultiSelectMode,
                                     onReload,
                                     searchTerm,
                                     registrationFilter, // NEW
                                 }) {
    const [benefits, setBenefits] = useState([]);
    const [filteredBenefits, setFilteredBenefits] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [selectedRows, setSelectedRows] = useState([]);
    const [selectableKeys, setSelectableKeys] = useState([]);
    const [positionFullMap, setPositionFullMap] = useState({});

    // NEW: statsMap cho mọi positionId
    const [statsMap, setStatsMap] = useState({});
    const [statsLoading, setStatsLoading] = useState(false);

    const reloadData = () => fetchData();

    const handleSelectChange = (rowKey, checked) => {
        setSelectedRows((prev) => (checked ? [...prev, rowKey] : prev.filter((key) => key !== rowKey)));
    };

    const handleSelectAll = () => setSelectedRows([...selectableKeys]);
    const handleDeselectAll = () => setSelectedRows([]);

    const handleConfirmRegister = () => {
        Modal.confirm({
            title: "Xác nhận đăng ký",
            content: "Bạn có chắc chắn muốn đăng ký phúc lợi cho tất cả nhân viên trong các vị trí đã chọn?",
            onOk: async () => {
                try {
                    const positionIds = selectedRows.map((rowKey) => parseInt(rowKey.split("-")[1]));
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

    const fetchData = () => {
        if (!benefitId) {
            console.warn("❗ benefitId chưa có, không gọi API");
            return;
        }
        setError(null);
        setLoading(true);

        benefitService
            .getPositionRegisterationDetail({}, benefitId)
            .then((res) => {
                const data = res.data.content || res.data;
                setBenefits(data);
                setTotalElements(res.data.totalElements || data.length || 0);
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

    // NEW: sau khi có benefits, fetch toàn bộ stats cho từng position
    useEffect(() => {
        const allPositions = benefits.flatMap((b) => b.positions.map((p) => p.positionId));
        if (allPositions.length === 0) {
            setStatsMap({});
            return;
        }
        let alive = true;
        setStatsLoading(true);

        Promise.all(
            allPositions.map((pid) =>
                benefitService
                    .getRegistrationStats(parseInt(benefitId), pid)
                    .then((res) => ({ pid, stats: res.data }))
                    .catch(() => ({ pid, stats: null }))
            )
        )
            .then((pairs) => {
                if (!alive) return;
                const map = pairs.reduce((acc, { pid, stats }) => {
                    acc[pid] = stats;
                    return acc;
                }, {});
                setStatsMap(map);
            })
            .finally(() => {
                if (alive) setStatsLoading(false);
            });

        return () => {
            alive = false;
        };
    }, [benefits, benefitId]);

    // Giữ logic kiểm tra isFull cho multi-select (giữ nguyên như trước)
    useEffect(() => {
        if (isMultiSelectMode) {
            const allPositions = benefits.flatMap((b) => b.positions.map((p) => p.positionId));
            if (allPositions.length === 0) return;

            Promise.all(
                allPositions.map((pid) =>
                    benefitService
                        .searchUnregisteredEmployees({
                            benefitId: parseInt(benefitId),
                            positionId: pid,
                        })
                        .then((res) => ({ pid, isFull: res.data.length === 0 }))
                )
            )
                .then((results) => {
                    const map = results.reduce((acc, { pid, isFull }) => {
                        acc[pid] = isFull;
                        return acc;
                    }, {});
                    setPositionFullMap(map);
                    const selectable = allPositions.filter((pid) => !map[pid]).map((pid) => `${benefitId}-${pid}`);
                    setSelectableKeys(selectable);
                })
                .catch((err) => {
                    console.error("Lỗi khi kiểm tra unregistered employees:", err);
                });
        } else {
            setPositionFullMap({});
            setSelectedRows([]);
            setSelectableKeys([]);
        }
    }, [isMultiSelectMode, benefitId, benefits]);

    // Filter + paginate (client-side) using searchTerm & registrationFilter
    useEffect(() => {
        const allPositions = benefits.flatMap((benefit) =>
            benefit.positions.map((position) => ({
                id: benefit.id,
                positionId: position.positionId,
                positions: position,
                benefit: benefit,
            }))
        );

        const byName = allPositions.filter((item) =>
            item.positions.positionName.toLowerCase().includes((searchTerm || "").toLowerCase())
        );

        const byRegistration = byName.filter((item) => {
            if (registrationFilter === "ALL") return true;
            const s = statsMap[item.positions.positionId];
            if (!s) return registrationFilter === "ALL"; // khi stats chưa về, tạm giữ lại
            const { totalRegistered, totalEmployees } = s;
            if (registrationFilter === "FULL") return totalRegistered === totalEmployees && totalEmployees > 0;
            if (registrationFilter === "NONE") return totalRegistered === 0 && totalEmployees > 0;
            if (registrationFilter === "PARTIAL") return totalRegistered > 0 && totalRegistered < totalEmployees;
            return true;
        });

        setFilteredBenefits(byRegistration);
        setTotalElements(byRegistration.length);
        setPageNumber(1);
    }, [searchTerm, registrationFilter, benefits, statsMap]);

    const currentPageData = filteredBenefits.slice((pageNumber - 1) * pageSize, pageNumber * pageSize);

    return (
        <div className="employee-table-wrapper">
            {isMultiSelectMode && (
                <div style={{ marginBottom: 16 }}>
                    <Button onClick={handleSelectAll} disabled={selectableKeys.length === 0} style={{ marginRight: 8 }}>
                        Chọn tất cả
                    </Button>
                    <Button onClick={handleDeselectAll} disabled={selectedRows.length === 0} style={{ marginRight: 8 }}>
                        Hủy chọn tất cả
                    </Button>
                    <Button type="primary" onClick={handleConfirmRegister} disabled={selectedRows.length === 0}>
                        Xác nhận đăng ký đã chọn
                    </Button>
                </div>
            )}
            <BenefitByPositionHeader isMultiSelectMode={isMultiSelectMode} />



            <div className="employee-table">
                {loading && <p>Đang tải...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && filteredBenefits.length === 0 && <p>Không tìm thấy phúc lợi.</p>}

                {!error &&
                    !loading &&
                    currentPageData.map((benefitItem) => (
                        <BenefitByPositionTableRow
                            key={`${benefitItem.id}-${benefitItem.positions.positionId}`}
                            benefit={benefitItem}
                            onUpdateSuccess={reloadData}
                            isMultiSelectMode={isMultiSelectMode}
                            isSelected={selectedRows.includes(`${benefitItem.id}-${benefitItem.positions.positionId}`)}
                            onSelectChange={handleSelectChange}
                            isFull={positionFullMap[benefitItem.positions.positionId] || false}
                            stats={statsMap[benefitItem.positions.positionId]} // NEW
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
