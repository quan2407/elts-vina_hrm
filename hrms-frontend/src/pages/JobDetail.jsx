import React, { useEffect, useState } from "react";
import "../styles/JobDetail.css";
import { getRecruitmentById } from "../services/recruitmentService";
import { useParams, useNavigate } from "react-router-dom";
import HeaderRecruitment from "../components/HeaderRecruitment";
import FooterRecruitment from "../components/FooterRecruitment";
import Breadcrumb from "../components/Breadcrumb";

const formatDate = (isoDate) => {
  if (!isoDate) return "";
  const date = new Date(isoDate);
  return date.toLocaleDateString("vi-VN");
};

const JobDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);

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
  const handleBackBtn = () => {
    navigate(`/jobs`);
  };

    const handleApplyBtn = () => {
    navigate(`/applyjob/${id}`);
  };

  return (
    <div className="jobDetail-page">
      <HeaderRecruitment />

      <Breadcrumb
        paths={[
          { name: "Danh sách công việc", url: "/jobs" },
          { name: "Chi tiết "+ job.title },
        ]}
      />

      <main className="jobDetail-container">
        <aside className="jobDetail-sidebar">
          <div className="jobDetail-card">
            <h4>Mức lương</h4>
            <p>{job.minSalary} - {job.maxSalary} VND</p>
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
            onClick={() => handleBackBtn()}
          >
            Quay lại
          </button>
        </aside>

        <section className="jobDetail-content">
                    <h1>{job.title}</h1>

          <h3>Mô tả công việc</h3>
          <p>{job.jobDescription}</p>

          <h4>Thời hạn tuyển dụng</h4>
          <p>
            {formatDate(job.createAt)} - {formatDate(job.expiredAt)}
          </p>

          <h4>Yêu cầu công việc</h4>
          <p>{job.jobRequirement}</p>

          <h4>Quyền lợi</h4>
          <p>{job.benefits}</p>

          <button className="jobDetail-apply-btn" onClick={() => handleApplyBtn()}>Ứng tuyển</button>
        </section>
      </main>
      <FooterRecruitment />
    </div>
  );
};

export default JobDetail;
