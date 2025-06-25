import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import authService from "../services/authService";
import "../styles/ChangePassword.css";

function ChangePasswordPage() {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState("");

  const handleSubmit = async () => {
    setErrors({});
    setSuccessMessage("");
    if (!oldPassword || !newPassword || !confirmPassword) {
      const fieldErrors = {};
      if (!oldPassword) fieldErrors.oldPassword = "Vui lòng nhập mật khẩu cũ";
      if (!newPassword) fieldErrors.newPassword = "Vui lòng nhập mật khẩu mới";
      if (!confirmPassword)
        fieldErrors.confirmPassword = "Vui lòng nhập xác nhận mật khẩu mới";
      setErrors(fieldErrors);
      return;
    }

    try {
      await authService.changePassword({
        oldPassword,
        newPassword,
        confirmNewPassword: confirmPassword,
      });

      setSuccessMessage("Đổi mật khẩu thành công!");
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      console.error("Lỗi đổi mật khẩu:", err.response?.data);
      const message =
        err.response?.data?.message || "Có lỗi xảy ra. Vui lòng thử lại.";
      const backendErrors = {};

      if (message.includes("Mật khẩu cũ không chính xác")) {
        backendErrors.oldPassword = message;
      } else if (message.includes("Xác nhận mật khẩu mới không khớp")) {
        backendErrors.confirmPassword = message;
      } else {
        backendErrors.general = message;
      }

      setErrors(backendErrors);
    }
  };

  return (
    <MainLayout>
      <div className="change-password-container">
        <div className="change-password-card">
          <h2>Đổi mật khẩu</h2>

          {errors.general && (
            <div className="error-message form-level-error">
              {errors.general}
            </div>
          )}
          {successMessage && (
            <div className="success-message form-level-success">
              {successMessage}
            </div>
          )}

          <div className="form-group">
            <label>Mật khẩu cũ</label>
            <input
              type="password"
              value={oldPassword}
              placeholder="Nhập mật khẩu cũ"
              onChange={(e) => setOldPassword(e.target.value)}
            />
            {errors.oldPassword && (
              <div className="error-message">{errors.oldPassword}</div>
            )}
          </div>

          <div className="form-group">
            <label>Mật khẩu mới</label>
            <input
              type="password"
              value={newPassword}
              placeholder="Nhập mật khẩu mới"
              onChange={(e) => setNewPassword(e.target.value)}
            />
            {errors.newPassword && (
              <div className="error-message">{errors.newPassword}</div>
            )}
          </div>

          <div className="form-group">
            <label>Xác nhận mật khẩu mới</label>
            <input
              type="password"
              value={confirmPassword}
              placeholder="Nhập lại mật khẩu mới"
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            {errors.confirmPassword && (
              <div className="error-message">{errors.confirmPassword}</div>
            )}
          </div>

          <button
            className="change-pass-submit-button"
            onClick={handleSubmit}
          >
            Đổi mật khẩu
          </button>
        </div>
      </div>
    </MainLayout>
  );
}

export default ChangePasswordPage;
