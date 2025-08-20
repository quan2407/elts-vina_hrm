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

const JobCard = ({
  createAt,
  expiredAt,
  title,
  benefits,
  salary,
  description,
  onClick,
}) => (
  <div
    className="job-card"
    style={{ backgroundColor: "#eeeeee" }}
  >
    <div className="job-ref">
      {formatDate(createAt)} - {formatDate(expiredAt)}
    </div>
    <div className="job-title">{title}</div>

    {benefits && benefits.trim() !== "" && (
      <div className="job-location">
        <b>Quyền lợi công việc: </b>
        {benefits}
      </div>
    )}
    <div className="job-salary">
      <b>Mức lương: </b>
      {salary}
    </div>
    <div className="job-description">
      <b>Mô tả công việc: </b>
      {description}
    </div>
    <div
      className="job-arrow-button"
      onClick={onClick}
    >
      <svg
        width="31"
        height="31"
        viewBox="0 0 31 31"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        className="arrow-icon"
      >
        <path
          d="M15.5 25.8333L13.6594 24.025L20.8927 16.7916H5.16666V14.2083H20.8927L13.6594 6.97496L15.5 5.16663L25.8333 15.5L15.5 25.8333Z"
          fill="black"
        />
      </svg>
    </div>
  </div>
);

const JobsPage = () => {
  const navigate = useNavigate();

  const [searchTerm, setSearchTerm] = useState("");
  const [jobs, setJobs] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const fetchJobs = async (targetPage = page, targetSize = size) => {
    try {
      const res = await getAllRecruitments(
        targetPage,
        targetSize,
        searchTerm,
        "createAt",
        "desc"
      );
      // chỉ hiển thị job OPEN ở BE thì tốt nhất; tạm thời lọc ở FE theo content đã phân trang
      const content = (res?.content || []).filter((j) => j.status === "OPEN");
      setJobs(content);
      setTotalPages(res?.totalPages || 0);
    } catch (error) {
      console.error("Lỗi khi load danh sách công việc:", error);
      setJobs([]);
      setTotalPages(0);
    }
  };

  // lần đầu và khi đổi trang
  useEffect(() => {
    fetchJobs(page, size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  // đổi size -> về trang 0
  useEffect(() => {
    setPage(0);
    fetchJobs(0, size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [size]);

  // đổi search -> về trang 0
  useEffect(() => {
    const handler = setTimeout(() => {
      setPage(0);
      fetchJobs(0, size);
    }, 300); // debounce nhẹ để đỡ spam API
    return () => clearTimeout(handler);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchTerm]);

  const handleViewDetails = (jobId) => {
    navigate(`/jobs/${jobId}`);
  };

  return (
    <div className="jobs-page">
      <HeaderRecruitment />

      <section className="search-section">
        <div className="search-container">
          <input
            type="text"
            placeholder="Tìm kiếm công việc"
            className="search-input"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                setPage(0);
                fetchJobs(0, size);
              }
            }}
          />
          <div
            className="search-button"
            onClick={() => {
              setPage(0);
              fetchJobs(0, size);
            }}
          >
            <svg
              width="46"
              height="46"
              viewBox="0 0 46 46"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              className="search-icon"
            >
              <path
                d="M36.2946 38.2053L25.7946 27.7053C24.8571 28.4553 23.779 29.0491 22.5603 29.4866C21.3415 29.9241 20.0446 30.1428 18.6696 30.1428C15.2634 30.1428 12.3809 28.9628 10.0221 26.6028C7.6634 24.2428 6.4834 21.3603 6.48215 17.9553C6.48215 14.5491 7.66215 11.6666 10.0221 9.30782C12.3821 6.94907 15.2646 5.76907 18.6696 5.76782C22.0759 5.76782 24.9584 6.94782 27.3171 9.30782C29.6759 11.6678 30.8559 14.5503 30.8571 17.9553C30.8571 19.3303 30.6384 20.6272 30.2009 21.8459C29.7634 23.0647 29.1696 24.1428 28.4196 25.0803L38.9665 35.6272C39.3103 35.9709 39.4821 36.3928 39.4821 36.8928C39.4821 37.3928 39.2946 37.8303 38.9196 38.2053C38.5759 38.5491 38.1384 38.7209 37.6071 38.7209C37.0759 38.7209 36.6384 38.5491 36.2946 38.2053ZM18.6696 26.3928C21.0134 26.3928 23.0059 25.5722 24.6471 23.9309C26.2884 22.2897 27.1084 20.2978 27.1071 17.9553C27.1071 15.6116 26.2865 13.6191 24.6453 11.9778C23.004 10.3366 21.0121 9.51657 18.6696 9.51782C16.3259 9.51782 14.3334 10.3384 12.6921 11.9797C11.0509 13.6209 10.2309 15.6128 10.2321 17.9553C10.2321 20.2991 11.0528 22.2916 12.694 23.9328C14.3353 25.5741 16.3271 26.3941 18.6696 26.3928Z"
                fill="black"
              />
            </svg>
          </div>
        </div>
      </section>

      <main className="jobs-listing">
        <div className="jobs-container">
          {jobs.length > 0 ? (
            jobs.map((job) => (
              <JobCard
                key={job.recruitmentId}
                createAt={job.createAt}
                expiredAt={job.expiredAt}
                benefits={job.benefits}
                title={job.title}
                salary={`${job.minSalary} - ${job.maxSalary} VND`}
                description={job.jobDescription}
                onClick={() => handleViewDetails(job.recruitmentId)}
              />
            ))
          ) : (
            <div
              className="no-jobs-message"
              style={{ color: "red" }}
            >
              Không có công việc nào phù hợp với tìm kiếm của bạn.
            </div>
          )}
        </div>

        {/* Pagination dưới danh sách */}
        <div
          className="employee-pagination-container"
          style={{ marginTop: 16 }}
        >
          <button
            className="employee-pagination-btn"
            onClick={() => setPage(0)}
            disabled={page === 0}
          >
            «
          </button>
          <button
            className="employee-pagination-btn"
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
          >
            ‹
          </button>

          {Array.from({ length: totalPages }).map((_, p) => (
            <button
              key={p}
              onClick={() => setPage(p)}
              className={`employee-pagination-btn ${
                p === page ? "employee-pagination-active" : ""
              }`}
            >
              {p + 1}
            </button>
          ))}

          <button
            className="employee-pagination-btn"
            onClick={() => setPage(page + 1)}
            disabled={page === totalPages - 1 || totalPages === 0}
          >
            ›
          </button>
          <button
            className="employee-pagination-btn"
            onClick={() => setPage(totalPages - 1)}
            disabled={page === totalPages - 1 || totalPages === 0}
          >
            »
          </button>
        </div>
      </main>

      <FooterRecruitment />
    </div>
  );
};

export default JobsPage;
