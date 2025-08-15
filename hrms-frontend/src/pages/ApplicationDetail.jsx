import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import MainLayout from "../components/MainLayout";
import applicationService from "../services/applicationService";
import ApplicationForm from "../components/ApplicationForm";
import { format } from "date-fns";
import SuccessModal from "../components/popup/SuccessModal";

function ApplicationDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState({
    open: false,
    title: "",
    message: "",
    type: "success", // "success" | "error"
  });
  const [formErrors, setFormErrors] = useState({});
  const [nextRoute, setNextRoute] = useState("");

  useEffect(() => {
    applicationService
      .getApplicationDetail(id)
      .then((res) => {
        setData(res.data);
      })
      .catch((err) => {
        console.error("❌ Lỗi khi tải chi tiết đơn:", err);
        setModal({
          open: true,
          title: "Lỗi tải dữ liệu",
          message: "Không thể tải thông tin đơn.",
          type: "error",
        });
      })

      .finally(() => {
        setLoading(false);
      });
  }, [id]);

  const handleUpdate = async (form) => {
    const formData = new FormData();

    formData.append("title", form.title);
    formData.append("content", form.content);

    const formattedStartDate = format(form.startDate, "yyyy-MM-dd");
    const formattedEndDate = form.endDate
      ? format(form.endDate, "yyyy-MM-dd")
      : formattedStartDate;

    formData.append("startDate", formattedStartDate);
    formData.append("endDate", formattedEndDate);
    formData.append("leaveCode", form.leaveCode || "");
    formData.append("isHalfDay", form.isHalfDay);
    formData.append("halfDayType", form.halfDayType);
    formData.append("applicationTypeId", data.applicationTypeId);
    if (data.applicationTypeName === "Bù công") {
      formData.append("checkIn", form.checkIn || "");
      formData.append("checkOut", form.checkOut || "");
    }

    if (form.attachment) {
      formData.append("attachment", form.attachment);
    }

    console.log("applicationTypeId gửi lên:", data.applicationTypeId);
    for (let [key, value] of formData.entries()) {
      console.log(`🧾 ${key}:`, value);
    }

    try {
      await applicationService.updateApplication(id, formData);
      setNextRoute("/my-applications");
      setModal({
        open: true,
        title: "Cập nhật đơn",
        message: "Cập nhật đơn thành công!",
        type: "success",
      });
    } catch (err) {
      console.error("❌ Lỗi khi cập nhật đơn:", err);
      setModal({
        open: true,
        title: "Lỗi cập nhật",
        message: "Có lỗi xảy ra khi cập nhật đơn.",
        type: "error",
      });
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
      throw err;
    }
  };

  if (loading) return <div>Đang tải dữ liệu đơn...</div>;
  if (!data) return <div>Không tìm thấy đơn.</div>;

  return (
    <MainLayout>
      <div className="content-wrapper">
        <h1 className="page-title">Chi tiết đơn</h1>
        <ApplicationForm
          mode="detail"
          data={data}
          onSubmit={handleUpdate}
          type={data.applicationTypeName === "Bù công" ? "makeup" : "leave"}
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

export default ApplicationDetail;
