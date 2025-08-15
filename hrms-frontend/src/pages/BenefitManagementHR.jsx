// import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import BenefitHrTable from "../components/BenefitHrTable";
// import "../styles/ManagementLayout.css";
import "../styles/EmployeeTable.css";
import { PlusOutlined } from "@ant-design/icons";
import { Button } from "antd";
import BenefitCreateModal from "../components/modals/benefit/BenefitCreateModal.jsx";
import Breadcrumb from "../components/Breadcrumb";
import { useEffect, useState } from "react";
import benefitService from "../services/benefitService.js";

// NEW
import BenefitSearchForm from "../components/common/search/BenefitSearchForm.jsx";

function BenefitManagementHR() {
    const [reloadKey, setReloadKey] = useState(0);

    // NEW: đưa filters lên cha
    const [filters, setFilters] = useState({});

    const breadcrumbPaths = [{ name: "Quản lý phúc lợi" }];

    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">Quản lý phúc lợi</h1>
                    <div className="page-actions">
                        <div className="page-actions" style={{ marginTop: '30px' }}>
                            <BenefitCreateModal
                                onCreated={() => setReloadKey(prev => prev + 1)}
                            />
                        </div>
                    </div>
                </div>

                <Breadcrumb paths={breadcrumbPaths} />

                {/* NEW: Form tìm kiếm ở đây */}
                <BenefitSearchForm
                    onSearch={(newFilters) => {
                        setFilters(newFilters); // bảng sẽ tự reset page về 1 khi filters đổi
                    }}
                />

                <BenefitHrTable
                    reloadKey={reloadKey}
                    onForceReload={() => setReloadKey((prev) => prev + 1)}
                    // NEW: truyền filters xuống bảng
                    filters={filters}
                />
            </div>
        </MainLayout>
    );
}

export default BenefitManagementHR;
