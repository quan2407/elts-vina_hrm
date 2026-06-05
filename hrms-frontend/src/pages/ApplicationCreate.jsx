import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import applicationService from "../services/applicationService";
import ApplicationForm from "../components/ApplicationForm";
import SuccessModal from "../components/popup/SuccessModal";
function ApplicationCreate() {
  const query = new URLSearchParams(window.location.search);
  const type = query.get("type"); // 'leave' hoặc 'makeup'
  const dateParam = query.get("date");
  const initialDate = dateParam ? new Date(dateParam) : null;
  const [formErrors, setFormErrors] = useState({});
  const navigate = useNavigate();
  const roles = JSON.parse(localStorage.getItem("role") || "[]");
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "success", // "success" | "error"
  });
  const [nextRoute, setNextRoute] = useState("");

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

      let target = "";
      if (isCreateByAdmin) {
        if (roles.includes("ROLE_HR") || roles.includes("ROLE_HR_MANAGER")) {
          target = "/applications/approvals/hr";
        } else if (roles.includes("ROLE_PRODUCTION_MANAGER")) {
          target = "/applications/approvals/manager";
        } else {
          target = "/";
        }
      } else {
        target = "/my-applications";
      }
      setNextRoute(target);

      // Mở modal thành công; điều hướng sẽ thực hiện khi đóng modal
      setModal({
        open: true,
        title: "Tạo đơn",
        message: "Tạo đơn thành công!",
        type: "success",
      });
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
      {modal.open && (
        <SuccessModal
          title={modal.title}
          message={modal.message}
          type={modal.type}
          onClose={() => {
            setModal((m) => ({ ...m, open: false }));
            if (nextRoute) navigate(nextRoute);
          }}
        />
      )}
    </MainLayout>
  );
}

export default ApplicationCreate;
