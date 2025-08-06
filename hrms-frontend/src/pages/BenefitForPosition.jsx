// import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import BenefitHrTable from "../components/BenefitHrTable";
// import "../styles/ManagementLayout.css";
import "../styles/EmployeeTable.css";
import { PlusOutlined } from "@ant-design/icons";
import { Button } from "antd";
import BenefitCreateModal from "../components/modals/benefit/BenefitCreateModal.jsx";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import benefitService from "../services/benefitService.js";
import BenefitForPositionTable from "../components/BenefitForPositionTable.jsx";
import AssignBenefitToPositions from "../components/modals/benefit/AssignBenefitToPositionsModal.jsx";
import AssignBenefitToPositionsModal from "../components/modals/benefit/AssignBenefitToPositionsModal.jsx";
import Breadcrumb from "../components/Breadcrumb";


function BenefitForPosition() {
    /**
     * Represents the benefits associated with a specific entity or process.
     * This variable is intended to hold information detailing the advantages,
     * perks, or positive outcomes related to the context in which it is used.
     */
    // const [benefits, setBenefits] = useState([]);
    // const fetchBenefits = async () => {
    //     try {
    //         const res = await benefitService.getAll();
    //         setBenefits(res.data); // hoặc res nếu không có .data
    //     } catch (err) {
    //         console.error("Lỗi tải phúc lợi:", err);
    //     }
    // };
    //
    // useEffect(() => {
    //     fetchBenefits().then(setBenefits);
    // }, []);
    const { benefitId } = useParams(); // Lấy id từ URL
    const [benefit, setBenefit] = useState(null);
    const [loading, setLoading] = useState(true);

    const [modalOpen, setModalOpen] = useState(false);


    console.log("benefitId trog benefitPosition:" ,benefitId);
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
    }, [benefitId]);

console.log("benefit trog benefitPosition:" ,benefit);
    const breadcrumbPaths = [
        { name: "Quản lý phúc lợi",  url: "http://localhost:5173/benefits-management"},
        { name: benefit ? `${benefit.title} ` : "Đang tải..."  }

    ];

    // useEffect(() => {
    //     const fetchBenefitDetail = async () => {
    //         try {
    //             const response = await benefitService.getById(id);
    //             setBenefit(response.data);
    //         } catch (err) {
    //             console.error("Failed to fetch benefit detail", err);
    //         } finally {
    //             setLoading(false);
    //         }
    //     };
    //
    //     fetchBenefitDetail();
    // }, [id]);
    //
    // if (loading) {
    //     return <p>Loading...</p>;
    // }
    //
    // if (!benefit) {
    //     return <p>Không tìm thấy phúc lợi .</p>;
    // }

    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">Quản lý {benefit ? benefit.title: ""}  </h1>
                    <div className="page-actions">
                        <AssignBenefitToPositionsModal benefitId={benefitId} />

                    </div>
                </div>
                <Breadcrumb paths={breadcrumbPaths} />
                <BenefitForPositionTable benefitId={benefitId} />
            </div>
        </MainLayout>
    );
}

export default BenefitForPosition;
