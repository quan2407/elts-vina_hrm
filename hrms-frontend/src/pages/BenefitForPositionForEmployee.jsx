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
import BenefitForPositionForEmployeeTable from "../components/BenefitForPositionForEmployeeTable.jsx";
import {useParams} from "react-router-dom";
import AssignEmployeeToBenefit from "../components/modals/benefit/AssignEmployeeToBenefit.jsx";

function BenefitForPositionForEmployee() {
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
    const { benefitId } = useParams();
    const { positionId } = useParams();
    const [benefit, setBenefit] = useState(null);
    const [position, setPosition] = useState(null);
    const [loading, setLoading] = useState(true);

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

        fetchBenefitDetail(); // ⬅ GỌI HÀM ở đây
    }, [benefitId]);useEffect(() => {
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

        fetchBenefitDetail(); // ⬅ GỌI HÀM ở đây
    }, [benefitId]);

    useEffect(() => {
        const fetchPositionDetail = async () => {
            try {
                const response = await benefitService.getPositionById( positionId);
                setPosition(response.data);
            } catch (err) {
                console.error("Failed to fetch benefit detail", err);
            } finally {
                setLoading(false);
            }
        };

        fetchPositionDetail(); // ⬅ GỌI HÀM ở đây
    }, [positionId]);


    const breadcrumbPaths = [
        { name: "Quản lý phúc lợi",  url: "http://localhost:5173/benefits-management"},
        { name: benefit ? `${benefit.title} ` : "Đang tải..." , url: `http://localhost:5173/benefits-management/benefit/${benefitId}`},
         {name: position ? `${position.name}`: "Đang tải..."}

    ];
    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">Quản lý phúc lợi của nhân viên theo phòng ban </h1>
                    <div className="page-actions">

                        {/*<BenefitCreateModal*/}
                        {/*    onCreated={() => setReloadKey(prev => prev + 1)}*/}
                        {/*/>*/}

                        <AssignEmployeeToBenefit
                            benefitId={benefitId}
                            positionId={positionId}
                            reloadKey={reloadKey}
                            onForceReload={() => setReloadKey((prev) => prev + 1)}
                        />
                    </div>
                </div>
                <Breadcrumb paths={breadcrumbPaths} />


                <BenefitForPositionForEmployeeTable
                    benefitId={benefitId}
                    positionId={positionId}
                    reloadKey={reloadKey}
                    onForceReload={() => setReloadKey((prev) => prev + 1)}  />
            </div>
        </MainLayout>
    );
}

export default BenefitForPositionForEmployee;
