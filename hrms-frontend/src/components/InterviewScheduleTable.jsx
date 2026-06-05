import React, {
  useEffect,
  useState,
  useImperativeHandle,
  forwardRef,
} from "react";
import "../styles/InterviewTable.css";
import {
  getAllInterviews,
  updateInterviewStatus,
  updateInterviewResult,
} from "../services/interviewScheduleService";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router-dom";
import SuccessModal from "../components/popup/SuccessModal";

// Hàm xoá dấu tiếng Việt
function removeVietnameseTones(str) {
  return str
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/Đ/g, "D");
}

const InterviewScheduleTable = forwardRef(
  ({ searchTerm, sortOrder, statusFilter = "ALL" }, ref) => {
    const [interviewSchedule, setInterviewSchedule] = useState([]);
    const navigate = useNavigate();
    const [RsuccessModal, setRSuccessModal] = useState(false);
    const [RfailModal, setRFailModal] = useState(false);
    const [SsuccessModal, setSSuccessModal] = useState(false);
    const [SFailModal, setSFailModal] = useState(false);

    useEffect(() => {
      const fetchInterviews = async () => {
        try {
          const data = await getAllInterviews();
          setInterviewSchedule(data || []);
        } catch (error) {
          console.error("Lỗi khi load danh sách lịch phỏng vấn:", error);
        }
      };
      fetchInterviews();
    }, []);

    const formatDate = (isoDate) => {
      if (!isoDate) return "";
      const date = new Date(isoDate);
      return date.toLocaleDateString("vi-VN");
    };

    const formatDateInterviewTime = (isoDate) => {
      if (!isoDate) return "";
      const date = new Date(isoDate);
      return date.toLocaleString("vi-VN", {
        hour12: false,
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    };

    const statusOptions = {
      WAITING_INTERVIEW: "Đang chờ phỏng vấn",
      INTERVIEWED: "Đã phỏng vấn",
      CANCEL: "Đã hủy",
    };

    // ===== Lọc + Sắp xếp =====
    const normalizedKw = removeVietnameseTones(searchTerm.toLowerCase());

    const compareFn = (a, b) => {
      // 1) Sắp theo trạng thái nếu sortOrder yêu cầu
      if (sortOrder === "status-asc" || sortOrder === "status-desc") {
        const x = (a.status || "").toString();
        const y = (b.status || "").toString();
        const cmp = x.localeCompare(y);
        return sortOrder === "status-asc" ? cmp : -cmp;
      }
      // 2) Mặc định: sắp theo tên ứng viên
      const nameA = (a.candidateName || "").toLowerCase();
      const nameB = (b.candidateName || "").toLowerCase();
      const cmp = nameA.localeCompare(nameB, "vi");
      return sortOrder === "asc" ? cmp : -cmp;
      // Nếu bạn muốn sắp theo thời gian phỏng vấn, thay block trên bằng:
      // const dateA = new Date(a.scheduledAt); const dateB = new Date(b.scheduledAt);
      // return sortOrder === "asc" ? dateA - dateB : dateB - dateA;
    };

    const filteredInterview = interviewSchedule
      // Lọc theo từ khoá (tên + tiêu đề tuyển dụng)
      .filter((interview) =>
        removeVietnameseTones(
          `${interview?.candidateName?.toLowerCase() || ""} ${
            interview?.recruitmentTitle?.toLowerCase() || ""
          }`
        ).includes(normalizedKw)
      )
      // Lọc theo trạng thái (ALL = không lọc)
      .filter((interview) =>
        statusFilter === "ALL" ? true : interview.status === statusFilter
      )
      // Sắp xếp
      .sort(compareFn);

    // ===== Actions =====
    const handleResultChange = async (id, result) => {
      try {
        await updateInterviewResult(id, result);
        setInterviewSchedule((prev) =>
          prev.map((item) => (item.id === id ? { ...item, result } : item))
        );
        setRSuccessModal(true);
      } catch {
        setRFailModal(true);
      }
    };

    const handleStatusChange = async (id, newStatus) => {
      try {
        await updateInterviewStatus(id, newStatus);
        setInterviewSchedule((prev) =>
          prev.map((item) =>
            item.id === id ? { ...item, status: newStatus } : item
          )
        );
        setSSuccessModal(true);
      } catch {
        setSFailModal(true);
      }
    };

    // ===== Export (toàn bộ danh sách hiện có; muốn chỉ export phần đã lọc thì đổi nguồn) =====
    const exportToExcel = () => {
      const source = interviewSchedule; // đổi thành filteredInterview nếu muốn export theo bộ lọc
      if (!source || source.length === 0) {
        alert("Không có dữ liệu để xuất.");
        return;
      }

      const exportData = source.map((job) => ({
        ID: job.recruitmentId,
        "Tiêu đề": job.title,
        "Nội dung tuyển dụng": job.recruitmentTitle,
        "Họ tên ứng viên": job.candidateName,
        "Ngày sinh": formatDate(job.dob),
        "Phòng ban tuyển dụng": job.recruitmentDepartment,
        "Thời gian phỏng vấn": formatDateInterviewTime(job.scheduledAt),
        "Người phỏng vấn": job.interviewerName,
        "Trạng thái": job.status,
        "Kết quả": job.result,
      }));

      const worksheet = XLSX.utils.json_to_sheet(exportData);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(
        workbook,
        worksheet,
        "Danh sách lịch phỏng vấn"
      );

      const excelBuffer = XLSX.write(workbook, {
        bookType: "xlsx",
        type: "array",
      });
      const blob = new Blob([excelBuffer], {
        type: "application/octet-stream",
      });
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
            <div className="Interview-header-cell">Kết quả phỏng vấn</div>
            <div className="Interview-header-cell">Action</div>
          </div>

          {filteredInterview.map((interview) => (
            <div key={interview.id} className="Interview-table-row">
              <div className="Interview-table-cell">{interview.id}</div>
              <div className="Interview-table-cell">
                {interview.recruitmentTitle}
              </div>
              <div className="Interview-table-cell">
                {interview.candidateName}
              </div>
              <div className="Interview-table-cell">
                {formatDate(interview.dob)}
              </div>
              <div className="Interview-table-cell">
                {interview.recruitmentDepartment}
              </div>
              <div className="Interview-table-cell">
                {formatDateInterviewTime(interview.scheduledAt)}
              </div>
              <div className="Interview-table-cell">
                {interview.interviewerName}
              </div>

              <div className="Interview-table-cell">
                <select
                  value={interview.status}
                  onChange={(e) =>
                    handleStatusChange(interview.id, e.target.value)
                  }
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
                {interview.status === "INTERVIEWED" ? (
                  <div className="radio-group">
                    <label>
                      <input
                        type="radio"
                        name={`result-${interview.id}`}
                        value="PASS"
                        checked={interview.result === "PASS"}
                        onChange={() =>
                          handleResultChange(interview.id, "PASS")
                        }
                      />
                      Vượt qua
                    </label>
                    <label>
                      <input
                        type="radio"
                        name={`result-${interview.id}`}
                        value="FAIL"
                        checked={interview.result === "FAIL"}
                        onChange={() =>
                          handleResultChange(interview.id, "FAIL")
                        }
                      />
                      Trượt
                    </label>
                  </div>
                ) : (
                  <span style={{ color: "#999" }}>Chưa phỏng vấn</span>
                )}
              </div>

              <div className="Interview-table-cell">
                <button
                  className="viewdetail-button"
                  onClick={() => handleDetailClick(interview.id)}
                >
                  Xem chi tiết
                </button>
              </div>
            </div>
          ))}
        </div>

        {RsuccessModal && (
          <SuccessModal
            title="Cập nhật kết quả phỏng vấn"
            message="Cập nhật kết quả phỏng vấn thành công!"
            type="success"
            onClose={() => setRSuccessModal(false)}
          />
        )}
        {RfailModal && (
          <SuccessModal
            title="Cập nhật kết quả phỏng vấn"
            type="fail"
            message="Cập nhật kết quả phỏng vấn thất bại!"
            onClose={() => setRFailModal(false)}
          />
        )}
        {SsuccessModal && (
          <SuccessModal
            title="Cập nhật trạng thái phỏng vấn"
            type="success"
            message="Cập nhật trạng thái phỏng vấn thành công!"
            onClose={() => setSSuccessModal(false)}
          />
        )}
        {SFailModal && (
          <SuccessModal
            title="Cập nhật trạng thái phỏng vấn"
            type="fail"
            message="Cập nhật trạng thái phỏng vấn thất bại!"
            onClose={() => setSFailModal(false)}
          />
        )}
      </div>
    );
  }
);

export default InterviewScheduleTable;
