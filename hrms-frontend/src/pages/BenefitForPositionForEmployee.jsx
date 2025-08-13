// import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import BenefitHrTable from "../components/BenefitHrTable";
// import "../styles/ManagementLayout.css";
import "../styles/EmployeeTable.css";
import { Button, Modal, message } from "antd";
import Breadcrumb from "../components/Breadcrumb";
import { useEffect, useState } from "react";
import benefitService from "../services/benefitService.js";
import BenefitForPositionForEmployeeTable from "../components/BenefitForPositionForEmployeeTable.jsx";
import { useParams } from "react-router-dom";
import AssignEmployeeToBenefit from "../components/modals/benefit/AssignEmployeeToBenefit.jsx";

function BenefitForPositionForEmployee() {
    const [reloadKey, setReloadKey] = useState(0);
    const { benefitId } = useParams();
    const { positionId } = useParams();
    const [benefit, setBenefit] = useState(null);
    const [position, setPosition] = useState(null);
    const [loading, setLoading] = useState(true);

    // bulk select
    const [selectedEmployees, setSelectedEmployees] = useState([]);
    const [bulkMode, setBulkMode] = useState(false);

    // NEW: search text moved here
    const [searchText, setSearchText] = useState("");

    useEffect(() => {
        const fetchBenefitDetail = async () => {
            try {
                const response = await benefitService.getById(benefitId);
                setBenefit(response.data);
            } catch (err) {
                console.error("Failed to fetch benefit detail", err);
            } finally {
                setLoading(false);
            }
        };
        fetchBenefitDetail();
    }, [benefitId]);

    useEffect(() => {
        const fetchBenefitDetail = async () => {
            try {
                const response = await benefitService.getById(benefitId);
                setBenefit(response.data);
            } catch (err) {
                console.error("Failed to fetch benefit detail", err);
            } finally {
                setLoading(false);
            }
        };
        fetchBenefitDetail();
    }, [benefitId]);

    useEffect(() => {
        const fetchPositionDetail = async () => {
            try {
                const response = await benefitService.getPositionById(positionId);
                setPosition(response.data);
            } catch (err) {
                console.error("Failed to fetch benefit detail", err);
            } finally {
                setLoading(false);
            }
        };
        fetchPositionDetail();
    }, [positionId]);

    const breadcrumbPaths = [
        { name: "Quản lý phúc lợi", url: "http://localhost:5173/benefits-management" },
        { name: benefit ? `${benefit.title} ` : "Đang tải...", url: `http://localhost:5173/benefits-management/benefit/${benefitId}` },
        { name: position ? `${position.name}` : "Đang tải..." }
    ];

    const handleBulkDelete = () => {
        if (selectedEmployees.length === 0) return;
        Modal.confirm({
            title: "Xác nhận xóa nhiều nhân viên",
            content: `Bạn có chắc muốn xóa ${selectedEmployees.length} nhân viên khỏi phúc lợi này?`,
            okText: "Xóa",
            cancelText: "Hủy",
            onOk: async () => {
                try {
                    const payload = { employeeIds: selectedEmployees.map(Number) };
                    await benefitService.multiUnRegister(benefitId, positionId, payload);
                    message.success("Đã xóa thành công!");
                    setSelectedEmployees([]);
                    setBulkMode(false);
                    setReloadKey((prev) => prev + 1);
                } catch (err) {
                    const msg = err?.response?.data?.message || err?.message || "Xóa thất bại";
                    message.error(msg);
                }
            },
        });
    };

    const toggleBulkMode = () => {
        setBulkMode((prev) => {
            const next = !prev;
            if (!next) setSelectedEmployees([]);
            return next;
        });
    };

    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">Quản lý phúc lợi của nhân viên theo phòng ban </h1>
                    <div className="page-actions" style={{ display: "flex", gap: 8 }}>
                        <AssignEmployeeToBenefit
                            benefitId={benefitId}
                            positionId={positionId}
                            reloadKey={reloadKey}
                            onForceReload={() => setReloadKey((prev) => prev + 1)}
                        />
                        <Button
                            type={bulkMode ? "default" : "primary"}
                            danger={!bulkMode}
                            onClick={toggleBulkMode}
                            title={bulkMode ? "Thoát chế độ chọn nhiều" : "Bật chế độ chọn nhiều để xóa"}
                        >
                            {bulkMode ? "Thoát chế độ chọn" : "Xóa nhiều"}
                        </Button>
                    </div>
                </div>

                <Breadcrumb paths={breadcrumbPaths} />

                {/* NEW: Search box đặt ở đây */}
                <div style={{ margin: "12px 0 8px", display: "flex", gap: 12 }}>
                    <input
                        type="text"
                        placeholder="Tìm theo tên hoặc email..."
                        value={searchText}
                        onChange={(e) => setSearchText(e.target.value)}
                        style={{ padding: "8px", width: "320px", borderRadius: "4px", border: "1px solid #ccc" }}
                    />
                </div>

                <BenefitForPositionForEmployeeTable
                    benefitId={benefitId}
                    positionId={positionId}
                    reloadKey={reloadKey}
                    onForceReload={() => setReloadKey((prev) => prev + 1)}
                    bulkMode={bulkMode}
                    selectedEmployees={selectedEmployees}
                    setSelectedEmployees={setSelectedEmployees}
                    onRequestBulkDelete={handleBulkDelete}
                    // NEW: pass search
                    searchText={searchText}
                />
            </div>
        </MainLayout>
    );
}

export default BenefitForPositionForEmployee;
