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
import BenefitByPositionTable from "../components/BenefitByPositionTable.jsx";
import AssignBenefitToPositions from "../components/modals/benefit/AssignBenefitToPositionsModal.jsx";
import AssignBenefitToPositionsModal from "../components/modals/benefit/AssignBenefitToPositionsModal.jsx";
import Breadcrumb from "../components/Breadcrumb";


function BenefitDetail() {
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


    const breadcrumbPaths = [
        { name: "Quản lý phúc lợi",  url: "http://localhost:5173/benefit"},
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
                    <h1 className="page-title">Quản lý  </h1>
                    <div className="page-actions">
                        <div className="filter-button">
                            <svg
                                className="filter-icon"
                                width="26"
                                height="30"
                                viewBox="0 0 26 30"
                                fill="none"
                                xmlns="http://www.w3.org/2000/svg"
                            >
                                <g clipPath="url(#clip0_2_1918)">
                                    <path
                                        d="M24.6877 0.544922H1.75414C0.70036 0.544922 0.168626 2.01455 0.915288 2.87277L10.0572 13.3822V25.0898C10.0572 25.5348 10.2461 25.9517 10.5633 26.207L14.5178 29.3876C15.2979 30.0153 16.3845 29.3791 16.3845 28.2704V13.3822L25.5266 2.87277C26.2718 2.01625 25.7437 0.544922 24.6877 0.544922Z"
                                        fill="#1A1A1A"
                                    />
                                </g>
                                <defs>
                                    <clipPath id="clip0_2_1918">
                                        <rect
                                            width="25.3089"
                                            height="29.0903"
                                            fill="white"
                                            transform="translate(0.566406 0.544922)"
                                        />
                                    </clipPath>
                                </defs>
                            </svg>
                        </div>
                        {/*<div className="create-button">*/}
                        {/*    <span className="export-text" >*/}
                        {/*          <PlusOutlined className="create-icon" />*/}
                        {/*        <span className="create-label">Tạo phúc lợi</span>*/}
                        {/*    </span>*/}
                        {/*    <svg*/}
                        {/*        className="export-dropdown"*/}
                        {/*        width="14"*/}
                        {/*        height="16"*/}
                        {/*        viewBox="0 0 14 16"*/}
                        {/*        fill="none"*/}
                        {/*        xmlns="http://www.w3.org/2000/svg"*/}
                        {/*    >*/}
                        {/*        <path*/}
                        {/*            d="M13.8394 7.99913C13.8394 12.3581 10.7668 15.8899 6.97441 15.8899C3.18203 15.8899 0.109375 12.3581 0.109375 7.99913C0.109375 3.64014 3.18203 0.108398 6.97441 0.108398C10.7668 0.108398 13.8394 3.64014 13.8394 7.99913ZM7.445 11.6231L11.1959 7.31188C11.4561 7.01279 11.4561 6.52917 11.1959 6.23326L10.7253 5.69237C10.4651 5.39328 10.0443 5.39328 9.78686 5.69237L6.97441 8.92502L4.16196 5.69237C3.90175 5.39328 3.48099 5.39328 3.22355 5.69237L2.75297 6.23326C2.49276 6.53235 2.49276 7.01597 2.75297 7.31188L6.50382 11.6231C6.76403 11.9222 7.18479 11.9222 7.445 11.6231Z"*/}
                        {/*            fill="white"*/}
                        {/*        />*/}

                        {/*    </svg>*/}
                        {/*</div>*/}

                        {/*<BenefitCreateModal onCreated={fetchBenefits} />*/}
                    </div>
                </div>
                <Breadcrumb paths={breadcrumbPaths} />
                <AssignBenefitToPositionsModal benefitId={benefitId} />
                <BenefitByPositionTable benefitId={benefitId} />
            </div>
        </MainLayout>
    );
}

export default BenefitDetail;
