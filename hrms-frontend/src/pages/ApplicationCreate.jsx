import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import applicationService from "../services/applicationService";
import ApplicationForm from "../components/ApplicationForm";

function ApplicationCreate() {
  const query = new URLSearchParams(window.location.search);
  const type = query.get("type"); // 'leave' hoặc 'makeup'
  const dateParam = query.get("date");
  const initialDate = dateParam ? new Date(dateParam) : null;
  const [formErrors, setFormErrors] = useState({});
  const navigate = useNavigate();
  const roles = JSON.parse(localStorage.getItem("role") || "[]");

  const handleSubmit = async (form) => {
    const formData = new FormData();
    formData.append("title", form.title?.trim());
    formData.append("content", form.content?.trim());

    const formatDate = (date) =>
      date
        ? `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
            2,
            "0"
          )}-${String(date.getDate()).padStart(2, "0")}`
        : "";

    formData.append("startDate", formatDate(form.startDate));
    formData.append(
      "endDate",
      form.type === "leave"
        ? formatDate(form.endDate)
        : formatDate(form.startDate)
    );

    formData.append("applicationTypeId", form.type === "makeup" ? 2 : 1);
    formData.append("leaveCode", form.type === "leave" ? form.leaveCode : "");

    formData.append("isHalfDay", form.isHalfDay);
    formData.append("halfDayType", form.isHalfDay ? form.halfDayType : "");

    if (form.type === "makeup") {
      formData.append("checkIn", form.checkIn || "");
      formData.append("checkOut", form.checkOut || "");
    }

    if (form.attachment) {
      formData.append("attachment", form.attachment);
    }

    // 👇 NẾU HR / QLSX tạo hộ => thêm employeeId và gọi API khác
    const isCreateByAdmin = form.selectedEmployee?.id;
    if (isCreateByAdmin) {
      formData.append("employeeId", form.selectedEmployee.id);
    }

    try {
      if (isCreateByAdmin) {
        for (let [key, value] of formData.entries()) {
          console.log(`${key}:`, value);
        }

        await applicationService.createApplicationAsAdmin(formData);
      } else {
        await applicationService.createApplication(formData);
      }

      alert("Tạo đơn thành công!");
      if (isCreateByAdmin) {
        if (roles.includes("ROLE_HR")) {
          navigate("/applications/approvals/hr");
        } else if (roles.includes("ROLE_PRODUCTION_MANAGER")) {
          navigate("/applications/approvals/manager");
        } else {
          navigate("/");
        }
      } else {
        navigate("/my-applications");
      }
    } catch (err) {
      console.error("Lỗi tạo đơn:", err);

      if (err.response?.data) {
        const data = err.response.data;
        if (typeof data === "object" && !Array.isArray(data)) {
          setFormErrors(data);
        } else if (data.message) {
          setFormErrors({ general: data.message });
        } else {
          setFormErrors({ general: "Có lỗi xảy ra khi gửi đơn." });
        }
      } else {
        setFormErrors({ general: "Có lỗi xảy ra khi gửi đơn." });
      }
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
          externalErrors={formErrors}
        />
      </div>
    </MainLayout>
  );
}

export default ApplicationCreate;
