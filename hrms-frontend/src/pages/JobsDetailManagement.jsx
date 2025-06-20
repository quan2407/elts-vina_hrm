import React, { useState, useEffect } from "react";
import MainLayout from "../components/MainLayout";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { vi } from "date-fns/locale";
import { Save } from "lucide-react"; // ✅ Icon đẹp từ lucide
import "../styles/EmployeeDetails.css";
import { getAllCity } from "../services/recruitmentService";
import { CreateRecruitment } from "../services/recruitmentService";
import departmentService from "../services/departmentService";

function RecruitmentDetailManagement() {
    const [title, setTitle] = useState("");
    const [workLocation, setWorkLocation] = useState("");
    const [employmentType, setEmploymentType] = useState("");
    const [jobDescription, setJobDescription] = useState("");
    const [jobRequirement, setJobRequirement] = useState("");
    const [benefits, setBenefits] = useState("");
    const [minSalary, setMinSalary] = useState("");
    const [maxSalary, setMaxSalary] = useState("");
    const [quantity, setQuantity] = useState("");
    const [errors, setErrors] = useState({});
    const [expiredAt, setExpiredAt] = useState("");
    const [departments, setDepartments] = useState([]);
    const [departmentId, setDepartmentId] = useState("");
    const [cities, setCities] = useState([]);

    const resetForm = () => {
        setTitle("");
        setWorkLocation("");
        setEmploymentType("");
        setExpiredAt(null);
        setJobDescription("");
        setJobRequirement("");
        setBenefits("");
        setMinSalary("");
        setMaxSalary("");
        setQuantity("");
        setDepartmentId("");

    };

    const handleSubmit = async () => {
        const payload = {
            title: title?.trim() ? title : null,
            workLocation: workLocation?.trim() ? workLocation : null,
            employmentType: employmentType?.trim() ? employmentType : null,
            jobDescription: jobDescription?.trim() ? jobDescription : null,
            jobRequirement: jobRequirement?.trim() ? jobRequirement : null,
            benefits: benefits?.trim() ? benefits : null,
            salaryRange: minSalary?.trim() && maxSalary?.trim() ? `${minSalary}-${maxSalary}` + " vnd" : null,
            quantity: quantity?.trim() ? quantity : null,
            expiredAt: expiredAt ? expiredAt.toISOString() : null,
            departmentId: departmentId !== "" ? Number(departmentId) : null
        };

        console.log("📌 Payload gửi đi:", payload);

        try {
            await CreateRecruitment(payload);
            alert("Tạo tin tuyển dụng thành công!");
            setErrors({});
            resetForm();
        } catch (err) {
            console.error("❌ Lỗi tạo tin tuyển dụng:", err);
            if (err.response && err.response.data) {
                const rawErrors = err.response.data;
                const normalizedErrors = {};

                for (const key in rawErrors) {
                    normalizedErrors[key] = Array.isArray(rawErrors[key])
                        ? rawErrors[key]
                        : [rawErrors[key]]; // ⚠️ ép string thành mảng
                }

                setErrors(normalizedErrors);
            } else {
                alert("Có lỗi xảy ra khi tạo tin tuyển dụng!");
            }
        }
    };

    useEffect(() => {
        const fetchDepartments = async () => {
            try {
                const res = await departmentService.getAllDepartments();
                setDepartments(res.data);
            } catch (err) {
                console.error("Lỗi load phòng ban:", err);
            }
        };
        fetchDepartments();
    }, []);

    useEffect(() => {
        const fetchCities = async () => {
            try {
                const res = await getAllCity();
                setCities(res.data);
            } catch (err) {
                console.error("Lỗi load thành phố:", err);
            }
        };
        fetchCities();
    }, []);

    const CustomInput = React.forwardRef(
        ({ value, onClick, placeholder }, ref) => (
            <input
                className="employeedetail-input-field"
                onClick={onClick}
                value={value || ""}
                placeholder={placeholder}
                readOnly
                ref={ref}
            />
        )
    );

    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">THÔNG TIN TUYỂN DỤNG</h1>
                </div>

                <div className="employeedetail-form-container">

                    <div className="employeedetail-form-content">

                        <div className="employeedetail-form-title">Thông tin tuyển dụng cơ bản</div>
                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Nội dung tuyển dụng<span className="required-star">*</span>
                                </div>
                                <input
                                    className="employeedetail-input-field"
                                    type="text"
                                    value={title}
                                    placeholder="Nhập nội dung tuyển dụng"
                                    onChange={(e) => setTitle(e.target.value)}
                                />
                                {errors.title && (
                                    <div className="error-message">
                                        {errors.title.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Địa điểm làm việc<span className="required-star">*</span>
                                </div>
                                <select
                                    className="employeedetail-input-field"
                                    value={workLocation}
                                    onChange={(e) => setWorkLocation(e.target.value)}
                                >
                                    <option value="">-- Chọn địa điểm làm việc --</option>
                                    {cities.map((d) => (
                                        <option
                                            key={d.id}
                                            value={d.name}
                                        >
                                            {d.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.workLocation && (
                                    <div className="error-message">
                                        {errors.workLocation.join(", ")}
                                    </div>
                                )}
                            </div>
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Loại hình công việc
                                    <span className="required-star">*</span>
                                </div>
                                <select
                                    className="employeedetail-input-field"
                                    value={employmentType}
                                    onChange={(e) => setEmploymentType(e.target.value)}
                                >
                                    <option value="">-- Chọn loại hình công việc --</option>
                                    <option>FULLTIME</option>
                                    <option>PARTTIME</option>
                                </select>
                                {errors.employmentType && (
                                    <div className="error-message">
                                        {errors.employmentType.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">Mô tả công việc</div>
                                <textarea
                                    className="employeedetail-input-field"
                                    type="text"
                                    value={jobDescription}
                                    placeholder="Nhập mô tả công việc"
                                    onChange={(e) => setJobDescription(e.target.value)}
                                />
                                {errors.jobDescription && (
                                    <div className="error-message">
                                        {errors.jobDescription.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">Quyền lợi</div>
                                <textarea
                                    className="employeedetail-input-field"
                                    type="text"
                                    value={benefits}
                                    placeholder="Nhập quyền lợi công việc"
                                    onChange={(e) => setBenefits(e.target.value)}
                                />
                                {errors.benefits && (
                                    <div className="error-message">
                                        {errors.benefits.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Mức lương nhỏ nhất(VNĐ)<span className="required-star">*</span>
                                </div>
                                <input
                                    className="employeedetail-input-field"
                                    type="number"
                                    value={minSalary}
                                    placeholder="Nhập mã nhân viên"
                                    onChange={(e) => setMinSalary(e.target.value)}
                                />
                                {errors.minSalary && (
                                    <div className="error-message">
                                        {errors.minSalary.join(", ")}
                                    </div>
                                )}
                            </div>
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Mức lương lớn nhất(VNĐ)<span className="required-star">*</span>
                                </div>
                                <input
                                    className="employeedetail-input-field"
                                    type="text"
                                    value={maxSalary}
                                    placeholder="Nhập mã nhân viên"
                                    onChange={(e) => setMaxSalary(e.target.value)}
                                />
                                {errors.maxSalary && (
                                    <div className="error-message">
                                        {errors.maxSalary.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Số lượng tuyển dụng
                                </div>
                                <input
                                    className="employeedetail-input-field"
                                    type="number"
                                    value={quantity}
                                    placeholder="0"
                                    onChange={(e) => setQuantity(e.target.value)}
                                />
                                {errors.phoneNumber && (
                                    <div className="error-message">
                                        {errors.phoneNumber.join(", ")}
                                    </div>
                                )}
                            </div>
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Hạn tuyển dụng                                    </div>
                                <DatePicker
                                    selected={expiredAt}
                                    onChange={(date) => setExpiredAt(date)}
                                    dateFormat="dd/MM/yyyy"
                                    locale={vi}
                                    customInput={<CustomInput />}
                                    placeholderText="Chọn ngày"
                                    showMonthDropdown
                                    showYearDropdown
                                    dropdownMode="select"
                                />
                                {errors.expiredAt && (
                                    <div className="error-message">
                                        {errors.expiredAt.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>


                        <div className="employeedetail-form-row">
                            <div className="employeedetail-input-group">
                                <div className="employeedetail-input-label">
                                    Phòng ban<span className="required-star">*</span>
                                </div>
                                <select
                                    className="employeedetail-input-field"
                                    value={departmentId}
                                    onChange={(e) => setDepartmentId(e.target.value)}
                                >
                                    <option value="">-- Chọn phòng ban --</option>
                                    {departments.map((d) => (
                                        <option
                                            key={d.id}
                                            value={d.id}
                                        >
                                            {d.name}
                                        </option>
                                    ))}
                                </select>
                                {errors.departmentId && (
                                    <div className="error-message">
                                        {errors.departmentId.join(", ")}
                                    </div>
                                )}
                            </div>
                        </div>



                        <div className="employeedetail-form-actions">
                            <button
                                className="submit-button"
                                onClick={handleSubmit}
                            >
                                <Save
                                    size={16}
                                    style={{ marginRight: "8px" }}
                                />
                                Lưu tin tuyển dụng
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </MainLayout>
    );
}

export default RecruitmentDetailManagement;
