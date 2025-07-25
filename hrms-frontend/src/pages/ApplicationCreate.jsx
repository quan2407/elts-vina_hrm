import React from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import applicationService from "../services/applicationService";
import ApplicationForm from "../components/ApplicationForm";

function ApplicationCreate() {
  const query = new URLSearchParams(window.location.search);
  const type = query.get("type"); // 'leave' hoặc 'makeup'
  const dateParam = query.get("date");
  const initialDate = dateParam ? new Date(dateParam) : null;

  const navigate = useNavigate();

  const handleSubmit = async (form) => {
    const formData = new FormData();
    formData.append("title", form.title?.trim());
    formData.append("content", form.content?.trim());
    const formatDate = (date) => (date ? date.toISOString().split("T")[0] : "");

    formData.append("startDate", formatDate(form.startDate));
    formData.append(
      "endDate",
      type === "leave" ? formatDate(form.endDate) : formatDate(form.startDate)
    );

    formData.append("applicationTypeId", type === "makeup" ? 2 : 1);
    formData.append("leaveCode", type === "leave" ? form.leaveCode : "");
    formData.append("isHalfDay", form.isHalfDay);
    formData.append("halfDayType", form.isHalfDay ? form.halfDayType : "");
    if (type === "makeup") {
      formData.append("checkIn", form.checkIn || "");
      formData.append("checkOut", form.checkOut || "");
    }

    if (form.attachment) {
      formData.append("attachment", form.attachment);
    }
    for (let [key, value] of formData.entries()) {
      console.log(`${key}: ${value}`);
    }

    try {
      await applicationService.createApplication(formData);
      alert("Tạo đơn thành công!");
      navigate("/my-applications");
    } catch (err) {
      console.error("Lỗi tạo đơn:", err);
      alert("Có lỗi xảy ra khi tạo đơn.");
    }
  };

  return (
    <MainLayout>
      <div className="content-wrapper">
        <h1 className="page-title">
          TẠO ĐƠN {type === "makeup" ? "BÙ CÔNG" : "NGHỈ PHÉP"}
        </h1>
        <ApplicationForm
          mode="create"
          type={type}
          initialDate={initialDate}
          onSubmit={handleSubmit}
        />
      </div>
    </MainLayout>
  );
}

export default ApplicationCreate;
