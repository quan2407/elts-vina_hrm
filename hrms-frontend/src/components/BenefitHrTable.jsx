import { useEffect } from "react";
import { useState } from "react";
// import "../styles/BenefitHrTable.css";
import benefitService from "../services/benefitService";


const BenefitHRTableHeader = () => {
    const headers = [
        "Id", "Title", "Description", "Start Date", "End Date", "Max participants", "Is Active?", "Created at"
    ];

    return (
        <div className="benefit-hr-table-header">
            {headers.map(label =>
                <div key={label} className="benefit-hr-header-cell">
                    {label}
                </div>
            )
            }
        </div>);
}

const BenefitHRTableRow = ({benefit}) => {
    const formatDate = (date) => new Date(date).toLocaleDateString("en-GB");

    return (
        <div className="benbefit-hr-table-row">
            <div className="employee-header-cell">{benefit.id}</div>
            <div className="employee-header-cell">{benefit.title}</div>

            <div className="employee-header-cell">{benefit.description}</div>
            <div className="employee-header-cell">{formatDate(benefit.startDate)}</div>
            <div className="employee-header-cell">{formatDate(benefit.endDate)}</div>
            <div className="employee-header-cell">{benefit.maxParticipants}</div>
            <div className="employee-header-cell">{benefit.isActive ? 'Yes' : 'No'}</div>
            <div className="employee-header-cell">{benefit.createdAt}</div>
        </div>
    )
}

function BenefitHrTable() {
    const [benefits, setBenefit] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    //fetch du lieu benefit
    useEffect(() => {
        benefitService
            .getAll()
            .then((res) => {setBenefit(res.data.content)
                console.log(res.data.content)
            })
            .catch((err) => {
                console.error("Failed to fetch benefits", err);
                setError("Failed to load data");
            })
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="benefit-table-wrapper">

            <div className="benefit-table">
                <BenefitHRTableHeader />

                {loading && <p>Loading...</p>}
                {error && <p style={{ color: "red" }}>{error}</p>}
                {!loading && benefits.length === 0 && <p>No benefits found.</p>}
                    {Array.isArray(benefits) && benefits.map((benefit) => (
                    <BenefitHRTableRow key={benefit.id} benefit={benefit} />
                ))}
            </div>
        </div>
    );
}

export default BenefitHrTable;