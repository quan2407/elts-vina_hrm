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

const formatSalary = (min, max) => {
  const fmt = (n) => (n != null ? Number(n).toLocaleString("vi-VN") : null);
  const a = fmt(min);
  const b = fmt(max);
  if (a && b) return `${a} – ${b} VND`;
  if (a) return `${a} VND`;
  if (b) return `${b} VND`;
  return "Thoả thuận";
};

const MetaRow = ({ label, value, primary }) => (
  <div className={`jd-card__row ${primary ? "jd-card__row--primary" : ""}`}>
    <span className={`chip ${primary ? "chip--primary" : ""}`}>{label}</span>
    <span className="jd-card__value">{value ?? "—"}</span>
  </div>
);

const JobDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchJob = async () => {
      try {
        setLoading(true);
        const data = await getRecruitmentById(id);
        setJob(data);
      } catch (err) {
        console.error("Lỗi khi lấy chi tiết công việc:", err);
        setError("Không thể tải chi tiết công việc. Vui lòng thử lại sau.");
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  const handleBackBtn = () => navigate(`/jobs`);
  const handleApplyBtn = () => navigate(`/applyjob/${id}`);

  return (
    <div className="jobDetail-page">
      <HeaderRecruitment />

      {/* Wrap breadcrumb để không dính lề trái */}
      <div className="jd-breadcrumb-wrap">
        <Breadcrumb
          paths={[
            { name: "Danh sách công việc", url: "/jobs" },
            { name: job?.title ? `Chi tiết ${job.title}` : "Chi tiết" },
          ]}
        />
      </div>

      <main className="jobDetail-container">
        {loading && (
          <div className="jd-skeleton-wrap">
            <div className="jd-skeleton-aside" />
            <div className="jd-skeleton-content" />
          </div>
        )}

        {!loading && error && (
          <div className="jd-error">
            <p>{error}</p>
            <button className="btn" onClick={handleBackBtn}>
              Quay lại trang tuyển dụng
            </button>
          </div>
        )}

        {!loading && !error && job && (
          <>
            <aside className="jobDetail-sidebar">
              <div className="jobDetail-card">
                <h4>Tổng quan</h4>
                <MetaRow
                  label="Mức lương"
                  value={formatSalary(job.minSalary, job.maxSalary)}
                  primary
                />
                <MetaRow label="Loại hình" value={job.employmentType} />
                <MetaRow label="Số lượng" value={job.quantity} />
                <MetaRow label="Bắt đầu" value={formatDate(job.createAt)} />
                <MetaRow label="Hết hạn" value={formatDate(job.expiredAt)} />
              </div>

              <div className="jd-actions">
                <button className="btn btn--ghost" onClick={handleBackBtn}>
                  Quay lại
                </button>
              </div>
            </aside>

            <section className="jobDetail-content">
              <header className="jd-header">
                <h1 className="jd-title">{job.title}</h1>
                {job.status === "OPEN" ? (
                  <span className="jd-badge jd-badge--open">Đang tuyển</span>
                ) : (
                  <span className="jd-badge">Đã đóng</span>
                )}
              </header>


              {job.jobDescription && (
                <section className="jd-block">
                  <h3>Mô tả công việc</h3>
                  <div
                    className="jd-richtext"
                    dangerouslySetInnerHTML={{
                      __html: (job.jobDescription || "").replace(/\n/g, "<br/>"),
                    }}
                  />
                </section>
              )}

              {job.jobRequirement && (
                <section className="jd-block">
                  <h3>Yêu cầu công việc</h3>
                  <div
                    className="jd-richtext"
                    dangerouslySetInnerHTML={{
                      __html: (job.jobRequirement || "").replace(/\n/g, "<br/>"),
                    }}
                  />
                </section>
              )}

              {job.benefits && (
                <section className="jd-block">
                  <h3>Quyền lợi</h3>
                  <div
                    className="jd-richtext"
                    dangerouslySetInnerHTML={{
                      __html: (job.benefits || "").replace(/\n/g, "<br/>"),
                    }}
                  />
                </section>
              )}

              <footer className="jd-footer-cta">
                <button
                  className="btn btn--primary btn--lg"
                  onClick={handleApplyBtn}
                >
                  Ứng tuyển ngay
                </button>
              </footer>
            </section>
          </>
        )}
      </main>

      <FooterRecruitment />
    </div>
  );
};

export default JobDetail;
