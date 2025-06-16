import React from "react";
import "../assets/styles/JobDetail.css";
import { useEffect, useState } from "react";
import { getRecruitmentById } from "../services/recruitmentService";
import { useParams } from "react-router-dom";

import HeaderRecruitment from "../component/HeaderRecruitment.jsx";

const formatDate = (isoDate) => {
  const date = new Date(isoDate);
  return date.toLocaleDateString("vi-VN");
};

const JobDetail = () => {
  let params = useParams(); // Lấy ID từ URL

  console.log(params.id);

  const [job, setJob] = useState([]);

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        const data = await getRecruitmentById(params.id);
        setJob(data);
      } catch (error) {
        console.error("Lỗi khi lấy danh sách jobs:", error);
      }
    };
    fetchJobs();
  }, [params.id]);
  console.log(job);
  return (
    <div className="job-page">
      <HeaderRecruitment />

      <main className="job-container">
        <div className="job-info">

          <div className="job-card">
            <h4>Tuyển dụng</h4>
            <p>{job.title}</p>
          </div>

          <div className="job-card">
            <h4>Số lượng tuyển dụng</h4>
            <p>{job.quantity}</p>
          </div>

          <div className="job-card">
            <h4>Địa điểm làm việc</h4>
            <p>{job.workLocation}</p>
          </div>

          {job.salaryRange && (
            <div className="job-card">
              <h4>Mức lương</h4>
              <p>{job.salaryRange}</p>
            </div>
          )}

          <div className="job-card">
            <h4>Loại hình công việc</h4>
            <p>{job.employmentType}</p>
          </div>
          <button className="back-btn">← Back</button>
        </div>

        <div className="job-description">
          <h3>Mô tả công việc</h3>
          <p>
            {job.jobDescription}
          </p>
          <br />
          
          <h4>Thời hạn tuyển dụng</h4>
          <p>{formatDate(job.createAt)} - {formatDate(job.expiredAt)}</p>

          <br />
          <h4>Yêu cầu công việc</h4>
          <p>
            {job.jobRequirement}
          </p>
          <br />

          <h4>Quyền lợi</h4>
          <p>
            {job.benefits}
          </p>

          <button className="apply-btn">Ứng tuyển →</button>
        </div>
      </main>

      <footer className="footer">
        <span>ELTS VINA</span>
        <span>Liên hệ: <strong>0987654321</strong></span>
      </footer>
    </div>
  );
};

export default JobDetail;
