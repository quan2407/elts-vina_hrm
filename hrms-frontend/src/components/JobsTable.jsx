import React, {
  useEffect,
  useState,
  useImperativeHandle,
  forwardRef,
} from "react";
import "../styles/JobsTable.css";
import { getAllRecruitments } from "../services/recruitmentService";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router-dom";

function removeVietnameseTones(str) {
  return str
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/Đ/g, "D");
}

const JobsTable = forwardRef(({ searchTerm, sortOrder, sortField }, ref) => {

  const [jobs, setJobs] = useState([]);
  const navigate = useNavigate();


  useEffect(() => {
    const fetchJobs = async () => {
      try {
        const data = await getAllRecruitments();
        setJobs(data);
      } catch (error) {
        console.error("Lỗi khi load danh sách công việc:", error);
      }
    };
    fetchJobs();
  }, []);


  const filteredJobs = jobs
    .filter(job =>
      removeVietnameseTones(job.title.toLowerCase()).includes(
        removeVietnameseTones(searchTerm.toLowerCase())
      )
    )
    .sort((a, b) => {
      const aValue = a[sortField];
      const bValue = b[sortField];

      // Sắp xếp theo kiểu chuỗi, số hoặc ngày
      if (typeof aValue === "string") {
        return sortOrder === "asc"
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue);
      } else if (typeof aValue === "number") {
        return sortOrder === "asc" ? aValue - bValue : bValue - aValue;
      } else if (aValue instanceof Date) {
        return sortOrder === "asc"
          ? new Date(aValue) - new Date(bValue)
          : new Date(bValue) - new Date(aValue);
      }
      return 0;
    });


  const formatDate = (isoDate) => {
    if (!isoDate) return "";
    const date = new Date(isoDate);
    return date.toLocaleDateString("vi-VN");
  };

  const exportToExcel = () => {
    if (!jobs || jobs.length === 0) {
      alert("Không có dữ liệu để xuất.");
      return;
    }


    const exportData = filteredJobs.map((job) => ({
      ID: job.recruitmentId,
      "Tiêu đề": job.title,
      "Địa điểm làm việc": job.workLocation,
      "Loại hình": job.employmentType,
      "Mô tả": job.jobDescription,
      "Yêu cầu": job.jobRequirement,
      "Quyền lợi": job.benefits,
      "Lương": job.minSalary +" - "+ job.maxSalary +"VND",
      "Số lượng": job.quantity,
      "Ngày tạo": formatDate(job.createAt),
      "Ngày hết hạn": formatDate(job.expiredAt),
      "Trạng thái": job.status,
      "SL Ứng tuyển": job.candidateRecruitmentsId?.length || 0,
    }));

    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Danh sách tuyển dụng");

    const excelBuffer = XLSX.write(workbook, {
      bookType: "xlsx",
      type: "array",
    });
    const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(blob, "DanhSachTuyenDung.xlsx");
  };

  useImperativeHandle(ref, () => ({
    exportToExcel,
  }));

  const handleCandidateClick = (jobId) => {
    navigate(`/candidates-management/${jobId}`);
  }

  const handleDetailClick = (jobId) => {
    navigate(`/jobsdetail-management/${jobId}`);
  };
  return (
    <div className="jobs-table-wrapper">
      <div className="jobs-table">
        <div className="jobs-table-header">
          <div className="jobs-header-cell">Id</div>
          <div className="jobs-header-cell">Nội dung</div>
          <div className="jobs-header-cell">Địa điểm làm việc</div>
          <div className="jobs-header-cell">Loại hình công việc</div>
          <div className="jobs-header-cell">Số lượng tuyển dụng</div>
          <div className="jobs-header-cell">Thời gian tuyển dụng</div>
          <div className="jobs-header-cell">Trạng thái</div>
          <div className="jobs-header-cell">Số lượng ứng tuyển</div>
          <div className="jobs-header-cell">Action</div>
        </div>

        {filteredJobs.map((job) => (
          <div
            key={job.recruitmentId}
            className="jobs-table-row"
          >
            <div className="jobs-table-cell">{job.recruitmentId}</div>
            <div className="jobs-table-cell">{job.title}</div>
            <div className="jobs-table-cell">{job.workLocation}</div>
            <div className="jobs-table-cell">{job.employmentType}</div>
            <div className="jobs-table-cell">{job.quantity}</div>
            <div className="jobs-table-cell">{formatDate(job.createAt)} - {formatDate(job.expiredAt)}</div>
            <div className="jobs-table-cell">{job.status}</div>
            <div className="jobs-table-cell">{job.candidateRecruitmentsId.length}</div>
            <div className="jobs-table-cell">

              <button className="viewcandidate-button" onClick={() => handleCandidateClick(job.recruitmentId)}>Danh sách ứng viên</button>

              <button className="viewdetail-button" onClick={() => handleDetailClick(job.recruitmentId)}>Xem chi tiết</button>

            </div>
          </div>
        ))}

      </div>
    </div>
  );
});

export default JobsTable;
