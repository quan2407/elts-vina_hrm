import React, { useEffect, useState } from "react";
import MainLayout from "../components/MainLayout";
import BenefitForPositionTable from "../components/BenefitForPositionTable.jsx";
import AssignBenefitToPositionsModal from "../components/modals/benefit/AssignBenefitToPositionsModal.jsx";
import Breadcrumb from "../components/Breadcrumb";
import { Button } from "antd";
import { PlusOutlined, CheckSquareOutlined } from "@ant-design/icons";
import benefitService from "../services/benefitService.js";
import { useParams } from "react-router-dom";
import "../styles/EmployeeTable.css";

function BenefitForPosition() {
    const { benefitId } = useParams();
    const [benefit, setBenefit] = useState(null);
    const [loading, setLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [reloadFlag, setReloadFlag] = useState(false);
    const [isMultiSelectMode, setIsMultiSelectMode] = useState(false); // Trạng thái cho chế độ chọn nhiều

    const handleReload = () => {
        setReloadFlag((prev) => !prev);
    };

    useEffect(() => {
        const fetchBenefitDetail = async () => {
            try {
                const response = await benefitService.getById(benefitId);
                setBenefit(response.data);
            } catch (err) {
                console.error("Lỗi tải chi tiết phúc lợi:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchBenefitDetail();
    }, [benefitId]);

    const breadcrumbPaths = [
        { name: "Quản lý phúc lợi", url: "http://localhost:5173/benefits-management" },
        { name: benefit ? `${benefit.title}` : "Đang tải..." },
    ];

    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">Quản lý {benefit ? benefit.title : ""}</h1>
                    <div className="page-actions">
                        <AssignBenefitToPositionsModal
                            benefitId={benefitId}
                            onSuccess={handleReload}
                        />
                        <Button
                            type={isMultiSelectMode ? "primary" : "default"}
                            icon={<CheckSquareOutlined />}
                            onClick={() => setIsMultiSelectMode(!isMultiSelectMode)}
                            style={{ marginLeft: 8 }}
                        >
                            {isMultiSelectMode ? "Tắt chọn nhiều" : "Chọn nhiều"}
                        </Button>
                    </div>
                </div>
                <Breadcrumb paths={breadcrumbPaths} />
                <BenefitForPositionTable
                    benefitId={benefitId}
                    reloadFlag={reloadFlag}
                    isMultiSelectMode={isMultiSelectMode}
                    onReload={handleReload}
                />
            </div>
        </MainLayout>
    );
}

export default BenefitForPosition;