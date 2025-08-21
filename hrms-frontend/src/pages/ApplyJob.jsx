import React, { useEffect, useState } from "react";
import "../styles/ApplyJob.css"; // lưu ý: dùng file CSS mới bên dưới
import { getRecruitmentById } from "../services/recruitmentService";
import { useParams, useNavigate } from "react-router-dom";
import HeaderRecruitment from "../components/HeaderRecruitment";
import FooterRecruitment from "../components/FooterRecruitment";
import { applyJob } from "../services/candidateRecruitmentService";
import Breadcrumb from "../components/Breadcrumb";
import SuccessModal from "../components/popup/SuccessModal.jsx";

const formatDate = (dateString) => {
  if (!dateString) return "";
  const [year, month, day] = dateString.split("-");
  return `${year}/${month}/${day}`;
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

export default function ApplyJob(){
  const { id } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [error, setError] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    candidateName: "",
    gender: "nam",
    dob: "",
    phoneNumber: "",
    email: "",
  });
  const [isSuccessOpen, setIsSuccessOpen] = useState(false);
  const [isFailedOpen, setIsFailedOpen] = useState(false);
  const [failMessage, setFailMessage] = useState("");

  useEffect(() => {
    (async () => {
      try { setJob(await getRecruitmentById(id)); } catch (e) { console.error(e); }
    })();
  }, [id]);

  const onChange = (e) => setFormData((p) => ({ ...p, [e.target.name]: e.target.value }));

  const onSubmit = async () => {
    setSubmitting(true);
    try {
      const payload = { ...formData, dob: formatDate(formData.dob) };
      await applyJob(id, payload);
      setError({});
      setIsSuccessOpen(true);
    } catch (err) {
      const d = err.response?.data || {};
      setError(typeof d === "object" ? d : {});
      if (typeof d === "string") { setFailMessage(d); setIsFailedOpen(true); }
    } finally { setSubmitting(false); }
  };

  if (!job) {
    return (
      <div className="aj-page">
        <HeaderRecruitment />
        <main className="aj-container aj-skeleton">
          <div className="aj-skel-left" />
          <div className="aj-skel-right" />
        </main>
      </div>
    );
  }

  return (
    <div className="aj-page">
      <HeaderRecruitment />

      <div className="aj-breadcrumb-wrap">
        <Breadcrumb paths={[{ name: "Danh sách công việc", url: "/jobs" }, { name: `Chi tiết ${job.title}`, url: `/jobs/${id}` }, { name: "Ứng tuyển" }]} />
      </div>

      <main className="aj-container">
        <aside className="aj-side">
          <div className="aj-card">
            <h4>Tổng quan</h4>
            <div className="aj-row aj-row--hi"><span className="aj-chip aj-chip--pri">Mức lương</span><span className="aj-val">{formatSalary(job.minSalary, job.maxSalary)}</span></div>
            <div className="aj-row"><span className="aj-chip">Loại hình</span><span className="aj-val">{job.employmentType}</span></div>
            <div className="aj-row"><span className="aj-chip">Số lượng</span><span className="aj-val">{job.quantity}</span></div>
            <div className="aj-row"><span className="aj-chip">Bắt đầu</span><span className="aj-val">{new Date(job.createAt).toLocaleDateString("vi-VN")}</span></div>
            <div className="aj-row"><span className="aj-chip">Hết hạn</span><span className="aj-val">{new Date(job.expiredAt).toLocaleDateString("vi-VN")}</span></div>
          </div>
          <div className="aj-actions">
            <button type="button" className="aj-btn aj-btn--ghost" onClick={() => navigate(-1)}>Quay lại</button>
          </div>
        </aside>

        <section className="aj-content">
          <header className="aj-head">
            <h1 className="aj-title">{job.title}</h1>
            <span className={`aj-badge ${job.status === "OPEN" ? "aj-badge--open" : ""}`}>{job.status === "OPEN" ? "Đang tuyển" : "Đã đóng"}</span>
          </header>

          <div className="aj-formCard">
            <h3>Thông tin ứng tuyển</h3>
            <div className="aj-grid">
              <div className="aj-field">
                <label htmlFor="ajName">Họ và tên <span className="aj-req">*</span></label>
                <input id="ajName" name="candidateName" value={formData.candidateName} onChange={onChange} placeholder="Họ và tên" className={error.candidateName ? "aj-invalid" : ""}/>
                {error.candidateName && <div className="aj-error">{error.candidateName}</div>}
              </div>

              <div className="aj-field">
                <label htmlFor="ajGender">Giới tính <span className="aj-req">*</span></label>
                <select id="ajGender" name="gender" value={formData.gender} onChange={onChange} className={error.gender ? "aj-invalid" : ""}>
                  <option value="nam">Nam</option>
                  <option value="nữ">Nữ</option>
                </select>
                {error.gender && <div className="aj-error">{error.gender}</div>}
              </div>

              <div className="aj-field">
                <label htmlFor="ajDob">Ngày sinh <span className="aj-req">*</span></label>
                <input id="ajDob" type="date" name="dob" value={formData.dob} onChange={onChange} className={error.dob ? "aj-invalid" : ""}/>
                {error.dob && <div className="aj-error">{error.dob}</div>}
              </div>

              <div className="aj-field">
                <label htmlFor="ajPhone">Số điện thoại <span className="aj-req">*</span></label>
                <input id="ajPhone" name="phoneNumber" value={formData.phoneNumber} onChange={onChange} placeholder="Nhập số điện thoại" className={error.phoneNumber ? "aj-invalid" : ""}/>
                {error.phoneNumber && <div className="aj-error">{error.phoneNumber}</div>}
              </div>

              <div className="aj-field aj-span2">
                <label htmlFor="ajEmail">Email <span className="aj-req">*</span></label>
                <input id="ajEmail" type="email" name="email" value={formData.email} onChange={onChange} placeholder="example@email.com" className={error.email ? "aj-invalid" : ""}/>
                {error.email && <div className="aj-error">{error.email}</div>}
              </div>
            </div>

            <div className="aj-formActions">
              <button type="button" className="aj-btn aj-btn--pri aj-btn--lg" onClick={onSubmit} disabled={submitting}>
                {submitting ? "Đang gửi…" : "Ứng tuyển"}
              </button>
            </div>
          </div>
        </section>
      </main>

      {isSuccessOpen && (
        <SuccessModal type="success" onClose={() => { setIsSuccessOpen(false); navigate(-1); }} title="Ứng tuyển thành công" message="Cảm ơn bạn đã ứng tuyển vào vị trí này. Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất." />
      )}

      {isFailedOpen && (
        <SuccessModal type="fail" onClose={() => { setIsFailedOpen(false); }} title="Ứng tuyển thất bại" message={failMessage} />
      )}

      <FooterRecruitment />
    </div>
  );
}
