import React, { useEffect, useState } from "react";
import "../styles/EmployeeTable.css";
import benefitService from "../services/benefitService.js";
import Paging from "./common/Paging.jsx";
import BenefitSearchForm from "./common/search/BenefitSearchForm.jsx";
import ActionDropdown from "./common/ActionDropdown.jsx";
import BenefitUpdateModal from "./modals/BenefitUpdateModal.jsx";


const BenefitHRTableHeader = () => {
    const headers = [
        "Id", "Tiêu đề", "Mô tả", "Ngày bắt đầu", "Ngày kết thúc", "Số lượng người tham gia tối đa", "Trạng thái hoạt động", "Ngày tạo"
    ];

    return (
        <div className="employee-table-header">
            {headers.map(label =>
                <div className="employee-header-cell" key={label}>
                    {label}
                </div>
            )}
        </div>
    );
};

const BenefitHRTableRow = ({ benefit }) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");

    return (
        <div className="employee-table-row">
            <div className="employee-table-cell">{benefit.id}</div>
            <div className="employee-table-cell">{benefit.title}</div>
            <div className="employee-table-cell">{benefit.description}</div>
            <div className="employee-table-cell">{formatDate(benefit.startDate)}</div>
            <div className="employee-table-cell">{formatDate(benefit.endDate)}</div>
            <div className="employee-table-cell">{benefit.maxParticipants}</div>
            <div className="employee-table-cell">{benefit.isActive ? 'Đang hoạt động' : 'Ngừng hoạt động'}</div>
            <div className="employee-table-cell">{formatDate(benefit.createdAt)}</div>
            <div className="employee-table-cell"><ActionDropdown/></div>
            <BenefitUpdateModal/>
        </div>
    );
};

function BenefitHrTable() {
    const [benefits, setBenefit] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [filters, setFilters] = useState({});

    useEffect(() => {
        const params = {
            page: pageNumber,
            size: pageSize,
            ...filters
        };

        setError(null);

        benefitService
            .getAll(params)
            .then((res) => {
                setBenefit(res.data.content);
                setTotalElements(res.data.totalElements);

            })
            .catch((err) => {
                console.error("Failed to fetch benefits", err);
                setError("Không thể lấy dữ liệu phúc lợi tương ứng");
            })
            .finally(() => setLoading(false));
    }, [pageNumber, pageSize, filters]);

    return (
        <div className="employee-table-wrapper">

            <BenefitSearchForm
                onSearch={(newFilters) => {
                    setFilters(newFilters);
                    setPageNumber(1); // reset về page đầu khi search
                }}
            />
            <BenefitHRTableHeader />

            <div className="employee-table">


                {loading && <p>Loading...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && benefits.length === 0 && <p>No benefits found.</p>}
                {!error && !loading && Array.isArray(benefits) && benefits.map((benefit) => (
                    <BenefitHRTableRow key={benefit.id} benefit={benefit} style={{ cursor: "pointer" }} />
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

export default BenefitHrTable;
