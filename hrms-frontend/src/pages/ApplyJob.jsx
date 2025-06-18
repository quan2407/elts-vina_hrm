import React, { useEffect, useState } from "react";
import "../styles/JobDetail.css";
import { getRecruitmentById } from "../services/recruitmentService";
import { useParams, useNavigate } from "react-router-dom";
import HeaderRecruitment from "../components/HeaderRecruitment";
import FooterRecruitment from "../components/FooterRecruitment";
import { applyJob } from "../services/candidateRecruitmentService";

const formatDate = (dateString) => {
    if (!dateString) return "";
    const [year, month, day] = dateString.split("-");
    return `${year}/${month}/${day}`;  // từ "2003-07-07" → "2003/07/07"
};

const ApplyJob = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [job, setJob] = useState(null);
    const [formData, setFormData] = useState({
        candidateName: "",
        gender: "nam",
        dob: "",
        phoneNumber: "",
        email: ""
    });

    useEffect(() => {
        const fetchJob = async () => {
            try {
                const data = await getRecruitmentById(id);
                setJob(data);
            } catch (error) {
                console.error("Lỗi khi lấy chi tiết công việc:", error);
            }
        };
        fetchJob();
    }, [id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value
        }));
    };
    const handleApply = async () => {
        const { candidateName, gender, dob, phoneNumber, email } = formData;

        if (!candidateName || !gender || !dob || !phoneNumber || !email) {
            alert("Vui lòng điền đầy đủ thông tin.");
            return;
        }
        if (!isValidEmail(email)) {
            alert("Email không hợp lệ.");
            return;
        }

        if (!isValidPhone(phoneNumber)) {
            alert("Số điện thoại không hợp lệ.");
            return;
        }
        if (!isValidBod(dob)) {
            alert("Ngày sinh phải trước ngày hiện tại.");
            return;
        }
        const payload = {
            candidateName,
            gender,
            dob: formatDate(dob),  // ✅ sửa tại đây
            phoneNumber,
            email
        };

        try {
            const response = await applyJob(id, payload);  // `id` là recruitmentId từ useParams
            alert(response);  // sẽ là: "Ứng viên đã ứng tuyển thành công"
            navigate(-1); // hoặc navigate lại: navigate(-1)
        } catch (error) {
            const message = error.response?.data || "Không gửi được đơn ứng tuyển.";

            if (error.response?.status === 400) {
                alert(message); // ✅ "Bạn đã ứng tuyển công việc này rồi."
            } else {
                alert( message);
            }
        }
    };

    const isValidBod = (birthDate) => {
        const selectedDate = new Date(birthDate);
        const today = new Date();

        // So sánh chỉ theo ngày (loại bỏ giờ phút giây)
        selectedDate.setHours(0, 0, 0, 0);
        today.setHours(0, 0, 0, 0);

        return selectedDate < today;
    };

    const isValidEmail = (email) => {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    };

    const isValidPhone = (phone) => {
        const regex = /^(0[1-9][0-9]{8})$/; // Định dạng 10 số bắt đầu bằng 0
        return regex.test(phone);
    };
    if (!job) {
        return (
            <div className="jobDetail-page">
                <HeaderRecruitment />
                <main className="jobDetail-container">
                    <p>Đang tải dữ liệu...</p>
                </main>
            </div>
        );
    }

    return (
        <div className="jobDetail-page">
            <HeaderRecruitment />

            <main className="jobDetail-container">
                <aside className="jobDetail-sidebar">
                    <div className="jobDetail-card">
                        <h4>Địa điểm làm việc</h4>
                        <p>{job.workLocation}</p>
                    </div>
                    <div className="jobDetail-card">
                        <h4>Mức lương</h4>
                        <p>{job.salaryRange}</p>
                    </div>
                    <div className="jobDetail-card">
                        <h4>Loại hình công việc</h4>
                        <p>{job.employmentType}</p>
                    </div>
                    <div className="jobDetail-card">
                        <h4>Số lượng tuyển dụng</h4>
                        <p>{job.quantity}</p>
                    </div>
                    <button
                        className="jobDetail-back-btn"
                        onClick={() => navigate(-1)}
                    >
                        Quay lại
                    </button>
                </aside>

                <section className="jobDetail-content">
                    <h3>Thông tin ứng tuyển</h3>

                    <div className="form-group mb-4">
                        <label className="fs-4  mb-3">Họ và tên <span style={{ color: "red" }}>*</span></label>
                        <input
                            type="text"
                            className="form-control"
                            name="candidateName"
                            value={formData.candidateName}
                            onChange={handleChange}
                            placeholder="Họ và tên"
                        />
                    </div>
                    <div className="form-group mb-4">
                        <label className="fs-4  mb-3">Giới tính <span style={{ color: "red" }}>*</span></label>
                        <select
                            className="form-control"
                            name="gender"
                            value={formData.gender}
                            onChange={handleChange}
                        >
                            <option value="nam">Nam</option>
                            <option value="nữ">Nữ</option>
                        </select>
                    </div>

                    <div className="form-group mb-4">
                        <label className="fs-4  mb-3">Ngày sinh <span style={{ color: "red" }}>*</span></label>
                        <input
                            type="date"
                            className="form-control"
                            name="dob"
                            value={formData.dob}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group mb-4">
                        <label className="fs-4  mb-3">Số điện thoại <span style={{ color: "red" }}>*</span></label>
                        <input
                            type="tel"
                            className="form-control"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            placeholder="Nhập số điện thoại"
                        />
                    </div>

                    <div className="form-group mb-4">
                        <label className="fs-4  mb-3">Email <span style={{ color: "red" }}>*</span></label>
                        <input
                            type="email"
                            className="form-control"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="example@email.com"
                        />
                    </div>

                    <button className="jobDetail-apply-btn" onClick={handleApply}>Ứng tuyển</button>
                </section>
            </main>
            <FooterRecruitment />
        </div>
    );
};

export default ApplyJob;
