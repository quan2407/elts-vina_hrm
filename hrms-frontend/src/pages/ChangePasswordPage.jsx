import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import authService from "../services/authService";
import "../styles/ChangePassword.css";

function ChangePasswordPage() {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState({});

  const handleSubmit = async () => {
    const newErrors = {};
    if (!oldPassword) newErrors.oldPassword = "Vui lòng nhập mật khẩu cũ";
    if (!newPassword) newErrors.newPassword = "Vui lòng nhập mật khẩu mới";
    if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = "Mật khẩu xác nhận không khớp";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    try {
      await authService.changePassword({
        oldPassword,
        newPassword,
      });
      alert("Đổi mật khẩu thành công!");
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
      setErrors({});
    } catch (err) {
      console.error("❌ Lỗi đổi mật khẩu:", err);
      alert(
        err.response?.data?.message ||
          "Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại."
      );
    }
  };

  return (
    <MainLayout>
      <div className="change-password-container">
        <div className="change-password-card">
          <h2>Đổi mật khẩu</h2>
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
            className="submit-button"
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
