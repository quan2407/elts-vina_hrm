import React, { useEffect, useState, useMemo } from "react";
import MainLayout from "../components/MainLayout";
import BenefitForPositionTable from "../components/BenefitForPositionTable.jsx";
import Breadcrumb from "../components/Breadcrumb";
import { Button, Input, Select } from "antd";
import { CheckSquareOutlined } from "@ant-design/icons";
import benefitService from "../services/benefitService.js";
import { useParams } from "react-router-dom";
import "../styles/EmployeeTable.css";

function BenefitForPosition() {
    const { benefitId } = useParams();
    const [benefit, setBenefit] = useState(null);
    const [loading, setLoading] = useState(true);
    const [reloadFlag, setReloadFlag] = useState(false);
    const [isMultiSelectMode, setIsMultiSelectMode] = useState(false);

    // 🔎 Search & Filters
    const [searchTerm, setSearchTerm] = useState("");
    const [registrationFilter, setRegistrationFilter] = useState("ALL");
    const [selectedPositionId, setSelectedPositionId] = useState(undefined);
    const [positionOptions, setPositionOptions] = useState([]); // {id, name}

    const handleReload = () => setReloadFlag((prev) => !prev);

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

    const breadcrumbPaths = useMemo(
        () => [
            { name: "Quản lý phúc lợi", url: "http://localhost:5173/benefits-management" },
            { name: benefit ? `${benefit.title}` : "Đang tải..." },
        ],
        [benefit]
    );

    const buttonStyles = isMultiSelectMode
        ? {
            borderRadius: 14,
            padding: "23px 20px",
            display: "flex",
            alignItems: "center",
            gap: 16,
            cursor: "pointer",
            boxShadow: "11px 4px 14px 0px rgba(0, 0, 0, 0.12)",
            backgroundColor: "#fff",
            border: "1px solid #3f861e",
            fontWeight: 600,
            fontSize: "18px",
            color: "#3f861e",
        }
        : {
            borderRadius: 14,
            padding: "23px 20px",
            display: "flex",
            alignItems: "center",
            gap: 16,
            cursor: "pointer",
            boxShadow: "11px 4px 14px 0px rgba(0, 0, 0, 0.12)",
            backgroundColor: "#3f861e",
            border: "none",
            fontWeight: 600,
            fontSize: "18px",
            color: "#fff",
        };

    // Hàm map benefitType sang tiếng Việt
    const getBenefitTypeLabel = (type) => {
        const mapping = {
            PHU_CAP: "Phụ cấp",
            KHAU_TRU: "Khấu trừ",
            SU_KIEN: "Sự kiện",
        };
        return mapping[type] || "...";
    };

    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header" style={{ alignItems: "center", gap: 16 }}>
                    <h1 className="page-title" style={{ marginBottom: 0 }}>
                        Quản lý {benefit ? benefit.title : ""}
                    </h1>

                    {/* ✅ Hiển thị loại phúc lợi với tiếng Việt */}
                    <span
                        style={{
                            display: "inline-block",
                            padding: "6px 12px",
                            borderRadius: 8,
                            background: "#f6ffed",
                            border: "1px solid #b7eb8f",
                            color: "#389e0d",
                            fontWeight: 600,
                        }}
                    >
            Loại phúc lợi: {getBenefitTypeLabel(benefit?.benefitType || benefit?.beneiftType)}
          </span>

                    <div className="page-actions" style={{ marginLeft: "auto" }}>
                        <Button
                            icon={<CheckSquareOutlined style={{ color: isMultiSelectMode ? "#3f861e" : "#fff" }} />}
                            onClick={() => setIsMultiSelectMode(!isMultiSelectMode)}
                            style={buttonStyles}
                        >
                            {isMultiSelectMode ? "Tắt áp dụng nhiều" : "Áp dụng cho nhiều vị trí"}
                        </Button>
                    </div>
                </div>

                <Breadcrumb paths={breadcrumbPaths} />

                {/* 🔎 Tìm kiếm + Lọc theo vị trí + Lọc theo trạng thái đăng ký */}
                <div style={{ display: "flex", gap: 12, alignItems: "center", margin: "16px 0 8px 0", flexWrap: "wrap" }}>
                    <Input
                        allowClear
                        size="large"
                        placeholder="Tìm kiếm theo tên vị trí"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ width: 360, maxWidth: "100%" }}
                    />

                    <Select
                        allowClear
                        size="large"
                        placeholder="Lọc theo vị trí"
                        value={selectedPositionId}
                        onChange={setSelectedPositionId}
                        style={{ width: 360, maxWidth: "100%" }}
                        options={positionOptions.map((p) => ({ label: p.name, value: String(p.id) }))}
                    />

                    <Select
                        size="large"
                        value={registrationFilter}
                        style={{ width: 260 }}
                        onChange={setRegistrationFilter}
                        options={[
                            { label: "Đăng ký: Tất cả", value: "ALL" },
                            { label: "Đăng ký: Đã đủ", value: "FULL" },
                            { label: "Đăng ký: Chưa ai đăng ký", value: "NONE" },
                            { label: "Đăng ký: Một phần", value: "PARTIAL" },
                        ]}
                    />
                </div>

                <BenefitForPositionTable
                    benefitId={benefitId}
                    reloadFlag={reloadFlag}
                    isMultiSelectMode={isMultiSelectMode}
                    onReload={handleReload}
                    searchTerm={searchTerm}
                    registrationFilter={registrationFilter}
                    positionFilter={selectedPositionId}
                    onPositionsLoaded={setPositionOptions}
                />
            </div>
        </MainLayout>
    );
}

export default BenefitForPosition;
