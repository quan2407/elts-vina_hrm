import React, { useEffect, useState, useImperativeHandle, forwardRef } from "react";
import "../styles/JobsTable.css";
import { getAllRecruitments } from "../services/recruitmentService";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";


const JobsTable = forwardRef(({ searchTerm, sortOrder }, ref) => {
    const [jobs, setJobs] = useState([]);

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

    const filteredJobs = jobs.filter(job =>
        job.title.toLowerCase().includes(searchTerm.toLowerCase())
    ).sort((a, b) => {
        const dateA = new Date(a.createAt);
        const dateB = new Date(b.createAt);
        return sortOrder === "asc"
            ? dateA - dateB
            : dateB - dateA;
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

        const exportData = filteredJobs.map(job => ({
            "ID": job.recruitmentId,
            "Tiêu đề": job.title,
            "Địa điểm làm việc": job.workLocation,
            "Loại hình": job.employmentType,
            "Mô tả": job.jobDescription,
            "Yêu cầu": job.jobRequirement,
            "Quyền lợi": job.benefits,
            "Lương": job.salaryRange,
            "Số lượng": job.quantity,
            "Ngày tạo": formatDate(job.createAt),
            "Ngày hết hạn": formatDate(job.expiredAt),
            "Trạng thái": job.status,
            "SL Ứng tuyển": job.candidateRecruitmentsId?.length || 0
        }));

        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Danh sách tuyển dụng");

        const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
        const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
        saveAs(blob, "DanhSachTuyenDung.xlsx");
    };

    // Expose exportToExcel to parent via ref
    useImperativeHandle(ref, () => ({
        exportToExcel
    }));


    return (
        <div className="employee-table-wrapper">
            <div className="employee-table">
                <div className="employee-table-header">
                    <div className="employee-header-cell">Id</div>
                    <div className="employee-header-cell">Nội dung</div>
                    <div className="employee-header-cell">Địa điểm làm việc</div>
                    <div className="employee-header-cell">Loại hình công việc</div>
                    <div className="employee-header-cell">Mô tả công việc</div>
                    <div className="employee-header-cell">Yêu cầu</div>
                    <div className="employee-header-cell">Quyền lợi</div>
                    <div className="employee-header-cell">Mức lương</div>
                    <div className="employee-header-cell">Số lượng tuyển dụng</div>
                    <div className="employee-header-cell">Thời gian tuyển dụng</div>
                    <div className="employee-header-cell">Trạng thái</div>
                    <div className="employee-header-cell">Số lượng ứng tuyển</div>
                    <div className="employee-header-cell">Action</div>
                </div>

                {filteredJobs.map((job) => (
                    <div
                        key={job.recruitmentId}
                        className="employee-table-row"
                    >
                        <div className="employee-table-cell">{job.recruitmentId}</div>
                        <div className="employee-table-cell">{job.title}</div>
                        <div className="employee-table-cell">{job.workLocation}</div>
                        <div className="employee-table-cell">{job.employmentType}</div>
                        <div className="employee-table-cell">{job.jobDescription}</div>
                        <div className="employee-table-cell">{job.jobRequirement}</div>
                        <div className="employee-table-cell">{job.benefits}</div>
                        <div className="employee-table-cell">{job.salaryRange}</div>
                        <div className="employee-table-cell">{job.quantity}</div>
                        <div className="employee-table-cell">{formatDate(job.createAt)} - {formatDate(job.expiredAt)}</div>
                        <div className="employee-table-cell">{job.status}</div>
                        <div className="employee-table-cell">{job.candidateRecruitmentsId.length}</div>
                        <div className="employee-table-cell"><button className="viewdetail-button" >Xem chi tiết</button></div>
                    </div>
                ))}
            </div>
        </div>
    );
});

export default JobsTable;
