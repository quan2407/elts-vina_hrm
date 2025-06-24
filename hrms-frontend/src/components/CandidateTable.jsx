import React, { useEffect, useState, useImperativeHandle, forwardRef } from "react";
import "../styles/CandidateTable.css";
import { getAllCandidateRecrutment } from "../services/candidateRecruitmentService";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { useParams } from "react-router-dom";

function removeVietnameseTones(str) {
    return str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/đ/g, "d")
        .replace(/Đ/g, "D");
}

const CandidateTable = forwardRef(({ searchTerm, sortOrder }, ref) => {
    const [candidates, setCandidates] = useState([]);
    const { jobId } = useParams();

    useEffect(() => {
        const fetchCandidates = async () => {
            try {
                const data = await getAllCandidateRecrutment(jobId);
                setCandidates(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách ứng viên:", error);
            }
        };
        fetchCandidates();
    }, [jobId]);

    const filteredCandidates = candidates
        .filter(candidate =>
            removeVietnameseTones(candidate.candidateName.toLowerCase()).includes(
                removeVietnameseTones(searchTerm.toLowerCase())
            )
        )
        .sort((a, b) => {
            const dateA = new Date(a.submittedAt);
            const dateB = new Date(b.submittedAt);
            return sortOrder === "asc" ? dateA - dateB : dateB - dateA;
        });

    const formatDate = (isoDate) => {
        if (!isoDate) return "";
        const date = new Date(isoDate);
        return date.toLocaleDateString("vi-VN");
    };

    const exportToExcel = () => {
        if (!candidates || candidates.length === 0) {
            alert("Không có dữ liệu để xuất.");
            return;
        }

        const exportData = filteredCandidates.map(candidate => ({
            "ID": candidate.id,
            "Tên ứng viên": candidate.candidateName,
            "Giới tính": candidate.gender,
            "Ngày sinh": formatDate(candidate.dob),
            "Email": candidate.email,
            "Số điện thoại": candidate.phoneNumber,
            "Ngày ứng tuyển": candidate.submittedAt,
            "Note": candidate.note,
            "Trạng thái": candidate.status,
        }));

        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Danh sách ứng viên");

        const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
        const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
        saveAs(blob, "DanhSachUngVien.xlsx");
    };

    // Expose exportToExcel to parent via ref
    useImperativeHandle(ref, () => ({
        exportToExcel
    }));

    return (
        <div className="candidate-table-wrapper">
            <div className="candidate-table">
                <div className="candidate-table-header">
                    <div className="candidate-header-cell">Id</div>
                    <div className="candidate-header-cell">Tên ứng viên</div>
                    <div className="candidate-header-cell">Giới tính</div>
                    <div className="candidate-header-cell">Ngày sinh</div>
                    <div className="candidate-header-cell">Email</div>
                    <div className="candidate-header-cell">Số điện thoại</div>
                    <div className="candidate-header-cell">Ngày ứng tuyển</div>
                    <div className="candidate-header-cell">Note</div>
                    <div className="candidate-header-cell">Trạng thái</div>

                </div>

                {filteredCandidates.map((candidate) => (
                    <div
                        key={candidate.candidateRecruitmentId}
                        className="candidate-table-row"
                    >
                        <div className="candidate-table-cell">{candidate.id}</div>
                        <div className="candidate-table-cell">{candidate.candidateName}</div>
                        <div className="candidate-table-cell">{candidate.gender}</div>
                        <div className="candidate-table-cell">{candidate.dob}</div>
                        <div className="candidate-table-cell">{candidate.email}</div>
                        <div className="candidate-table-cell">{candidate.phoneNumber}</div>
                        <div className="candidate-table-cell">{formatDate(candidate.submittedAt)}</div>
                        <div className="candidate-table-cell">{candidate.note}</div>
                        <div className="candidate-table-cell">{candidate.status}</div>

                    </div>
                ))}
            </div>
        </div>
    );
});

export default CandidateTable;
