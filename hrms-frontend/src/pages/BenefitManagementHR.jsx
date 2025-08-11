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

function BenefitManagementHR() {
  /**
   * Represents the benefits associated with a specific entity or process.
   * This variable is intended to hold information detailing the advantages,
   * perks, or positive outcomes related to the context in which it is used.
   */
  // const [benefits, setBenefits] = useState([]);
  // const fetchBenefits = async () => {
  //   try {
  //     const res = await benefitService.getAll();
  //     setBenefits(res.data); // hoặc res nếu không có .data
  //   } catch (err) {
  //     console.error("Lỗi tải phúc lợi:", err);
  //   }
  // };
  //
  // useEffect(() => {
  //   fetchBenefits().then(setBenefits);
  // }, []);
  const [reloadKey, setReloadKey] = useState(0);


    const breadcrumbPaths = [
        { name: "Quản lý phúc lợi"}

      ];
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


        <BenefitHrTable
                        reloadKey={reloadKey}
                        onForceReload={() => setReloadKey((prev) => prev + 1)}  />
      </div>
    </MainLayout>
  );
}

export default BenefitManagementHR;
