import React, {
    useEffect,
    useState,
    useImperativeHandle,
    forwardRef,
} from "react";
import "../styles/InterviewTable.css";
import { getAllInterviews } from "../services/interviewScheduleService";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router-dom";
import { updateInterviewStatus } from "../services/interviewScheduleService";

// Hàm xoá dấu tiếng Việt
function removeVietnameseTones(str) {
    return str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/đ/g, "d")
        .replace(/Đ/g, "D");
}

const InterviewScheduleTable = forwardRef(({ searchTerm = "" }, ref) => {

    const [interviewSchedule, setInterviewSchedule] = useState([]);
    const navigate = useNavigate();


    useEffect(() => {
        const fetchInterviews = async () => {
            try {
                const data = await getAllInterviews();
                setInterviewSchedule(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách lịch phỏng vấn:", error);
            }
        };
        fetchInterviews();
    }, []);

    const filteredInterview = interviewSchedule.filter((interview) =>
        removeVietnameseTones(interview?.candidateName?.toLowerCase() || "").includes(
            removeVietnameseTones(searchTerm.toLowerCase())
        )
    );


    const formatDate = (isoDate) => {
        if (!isoDate) return "";
        const date = new Date(isoDate);
        return date.toLocaleDateString("vi-VN");
    };
    const formatDateInterviewTime = (isoDate) => {
        if (!isoDate) return "";
        const date = new Date(isoDate);
        return date.toLocaleString("vi-VN", {
            hour12: false, // bỏ AM/PM
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const statusOptions = {
        PENDING: "Đang chờ",
        COMPLETED: "Đã phỏng vấn",
        CANCLE: "Đã hủy",
    };

    const handleStatusChange = async (id, newStatus) => {
        try {
            await updateInterviewStatus(id, newStatus);
            setInterviewSchedule((prev) =>
                prev.map((item) =>
                    item.id === id ? { ...item, status: newStatus } : item
                )
            );
            alert("Cập nhật trạng thái thành công!");
        } catch {
            alert("Lỗi khi cập nhật trạng thái!");
        }
    };

    const exportToExcel = () => {
        if (!interviewSchedule || interviewSchedule.length === 0) {
            alert("Không có dữ liệu để xuất.");
            return;
        }


        const exportData = interviewSchedule.map((job) => ({
            ID: job.recruitmentId,
            "Tiêu đề": job.title,
            "Địa điểm làm việc": job.workLocation,
            "Loại hình": job.employmentType,
            "Mô tả": job.jobDescription,
            "Yêu cầu": job.jobRequirement,
            "Quyền lợi": job.benefits,
            "Lương": job.minSalary + " - " + job.maxSalary + "VND",
            "Số lượng": job.quantity,
            "Ngày tạo": formatDate(job.createAt),
            "Ngày hết hạn": formatDate(job.expiredAt),
            "Trạng thái": job.status,
            "SL Ứng tuyển": job.candidateRecruitmentsId?.length || 0,
        }));

        const worksheet = XLSX.utils.json_to_sheet(exportData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Danh sách lịch phỏng vấn");

        const excelBuffer = XLSX.write(workbook, {
            bookType: "xlsx",
            type: "array",
        });
        const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
        saveAs(blob, "DanhSachLichPhongVan.xlsx");
    };

    useImperativeHandle(ref, () => ({
        exportToExcel,
    }));



    const handleDetailClick = (jobId) => {
        navigate(`/interviews-management/${jobId}`);
    };
    return (
        <div className="Interview-table-wrapper">
            <div className="Interview-table">
                <div className="Interview-table-header">
                    <div className="Interview-header-cell">Id</div>
                    <div className="Interview-header-cell">Nội dung tuyển dụng</div>
                    <div className="Interview-header-cell">Họ tên ứng viên</div>
                    <div className="Interview-header-cell">Ngày sinh</div>
                    <div className="Interview-header-cell">Phòng ban tuyển dụng</div>
                    <div className="Interview-header-cell">Thời gian phỏng vấn</div>
                    <div className="Interview-header-cell">Người phỏng vấn</div>
                    <div className="Interview-header-cell">Trạng thái</div>
                    <div className="Interview-header-cell">Action</div>
                </div>

                {filteredInterview.map((interview) => (
                    <div
                        key={interview.id}
                        className="Interview-table-row"
                    >
                        <div className="Interview-table-cell">{interview.id}</div>
                        <div className="Interview-table-cell">{interview.recruitmentTitle}</div>
                        <div className="Interview-table-cell">{interview.candidateName}</div>
                        <div className="Interview-table-cell">{formatDate(interview.dob)}</div>
                        <div className="Interview-table-cell">{interview.recruitmentDepartment}</div>
                        <div className="Interview-table-cell">{formatDateInterviewTime(interview.scheduledAt)}</div>
                        <div className="Interview-table-cell">{interview.interviewerName}</div>
                        <div className="Interview-table-cell">
                            <select
                                value={interview.status}
                                onChange={(e) => handleStatusChange(interview.id, e.target.value)}
                                className="form-select"
                            >
                                {Object.entries(statusOptions).map(([key, label]) => (
                                    <option key={key} value={key}>
                                        {label}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="Interview-table-cell">
                            <button className="viewdetail-button" onClick={() => handleDetailClick(interview.id)}>Xem chi tiết</button>

                        </div>
                    </div>
                ))}

            </div>
        </div>
    );
});

export default InterviewScheduleTable;