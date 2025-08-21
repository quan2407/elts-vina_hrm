import React, { useEffect, useState } from "react";
import "../styles/JobsPage.css";
import { useNavigate } from "react-router-dom";
import HeaderRecruitment from "../components/HeaderRecruitment";
import FooterRecruitment from "../components/FooterRecruitment";
import { getAllRecruitments } from "../services/recruitmentService";

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

const JobCard = ({ job, onClick }) => (
  <article className="job-card" aria-label={`Tin tuyển dụng: ${job.title}`}>
    <header className="job-card__header">
      <time className="job-card__date">
        {formatDate(job.createAt)} • Hết hạn: {formatDate(job.expiredAt)}
      </time>
      <h3 className="job-card__title">{job.title}</h3>
    </header>

    {job.benefits && job.benefits.trim() !== "" && (
      <p className="job-card__benefits">
        <span className="chip">Quyền lợi</span>
        <span className="clamp-2">{job.benefits}</span>
      </p>
    )}

    <p className="job-card__salary">
      <span className="chip chip--primary">Mức lương</span>
      {formatSalary(job.minSalary, job.maxSalary)}
    </p>

    {job.jobDescription && (
      <p className="job-card__desc clamp-3">
        {job.jobDescription}
      </p>
    )}

    <div className="job-card__footer">
      <button
        className="btn btn--primary"
        onClick={onClick}
        aria-label={`Xem chi tiết ${job.title}`}
      >
        Xem chi tiết
        <svg width="20" height="20" viewBox="0 0 24 24" aria-hidden>
          <path d="M13 5l7 7-7 7M5 12h14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      </button>
    </div>
  </article>
);

const JobsPage = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [jobs, setJobs] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchJobs = async (targetPage = page, targetSize = size) => {
    try {
      setLoading(true);
      const res = await getAllRecruitments(
        targetPage,
        targetSize,
        searchTerm,
        "createAt",
        "desc"
      );
      const content = (res?.content || []).filter((j) => j.status === "OPEN");
      setJobs(content);
      setTotalPages(res?.totalPages || 0);
    } catch (error) {
      console.error("Lỗi khi load danh sách công việc:", error);
      setJobs([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchJobs(page, size); /* eslint-disable-next-line */ }, [page]);
  useEffect(() => { setPage(0); fetchJobs(0, size); /* eslint-disable-next-line */ }, [size]);
  useEffect(() => {
    const handler = setTimeout(() => { setPage(0); fetchJobs(0, size); }, 300);
    return () => clearTimeout(handler);
    // eslint-disable-next-line
  }, [searchTerm]);

  const handleViewDetails = (jobId) => navigate(`/jobs/${jobId}`);

  return (
    <div className="jobs-page">
      <HeaderRecruitment />

      <section className="search-section">
        <div className="search-container">
          <div className="search-input-wrap">
            <svg className="search-leading" viewBox="0 0 24 24" width="20" height="20" aria-hidden>
              <path d="M21 21l-4.35-4.35M10 18a8 8 0 1 1 0-16 8 8 0 0 1 0 16z" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
            <input
              type="text"
              placeholder="Tìm kiếm vị trí, kỹ năng, mức lương…"
              className="search-input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={(e) => { if (e.key === "Enter") { setPage(0); fetchJobs(0, size); } }}
              aria-label="Tìm kiếm công việc"
            />
          </div>
          <button className="btn btn--primary search-button" onClick={() => { setPage(0); fetchJobs(0, size); }}>
            Tìm kiếm
          </button>
          <select className="select-size" value={size} onChange={(e) => setSize(Number(e.target.value))} aria-label="Số dòng mỗi trang">
            {[10, 15, 20, 30].map((n) => (<option key={n} value={n}>{n}/trang</option>))}
          </select>
        </div>
      </section>

      <main className="jobs-listing">
        {loading && (
          <div className="grid grid--3">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="skeleton-card" />
            ))}
          </div>
        )}

        {!loading && jobs.length > 0 && (
          <div className="grid grid--3">
            {jobs.map((job) => (
              <JobCard key={job.recruitmentId} job={job} onClick={() => handleViewDetails(job.recruitmentId)} />
            ))}
          </div>
        )}

        {!loading && jobs.length === 0 && (
          <div className="empty-state">
            <img alt="Không có kết quả" src="data:image/svg+xml;utf8,\
            <svg xmlns='http://www.w3.org/2000/svg' width='140' height='100' viewBox='0 0 140 100'><rect width='140' height='100' rx='12' ry='12' fill='%23f2f3f5'/><path d='M20 70h100' stroke='%23c6c7cb' stroke-width='6' stroke-linecap='round'/><circle cx='50' cy='45' r='14' fill='%23e5e7eb'/><path d='M74 60l14-18 18 24' stroke='%23d1d5db' stroke-width='6' fill='none' stroke-linecap='round' stroke-linejoin='round'/></svg>" />
            <p>Không có công việc nào phù hợp với tìm kiếm của bạn.</p>
          </div>
        )}

        <nav className="pagination" aria-label="Phân trang tin tuyển dụng">
          <button className="page-btn" onClick={() => setPage(0)} disabled={page === 0} aria-label="Trang đầu">«</button>
          <button className="page-btn" onClick={() => setPage(page - 1)} disabled={page === 0} aria-label="Trang trước">‹</button>

          {Array.from({ length: totalPages }).map((_, p) => (
            <button
              key={p}
              onClick={() => setPage(p)}
              className={`page-btn ${p === page ? "is-active" : ""}`}
              aria-current={p === page ? "page" : undefined}
            >
              {p + 1}
            </button>
          ))}

          <button className="page-btn" onClick={() => setPage(page + 1)} disabled={page === totalPages - 1 || totalPages === 0} aria-label="Trang sau">›</button>
          <button className="page-btn" onClick={() => setPage(totalPages - 1)} disabled={page === totalPages - 1 || totalPages === 0} aria-label="Trang cuối">»</button>
        </nav>
      </main>

      <FooterRecruitment />
    </div>
  );
};

export default JobsPage;