import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import "../styles/ResetPasswordPage.css";

function ResetPasswordPage() {
  const [email, setEmail] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");

    try {
      await authService.resetPassword({ email });
      setSuccessMessage("Mật khẩu mới đã được gửi tới email của bạn.");
    } catch (err) {
      console.error("Reset password failed:", err.response || err);
      if (err.response && err.response.data && err.response.data.message) {
        setErrorMessage(err.response.data.message);
      } else {
        setErrorMessage("Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
      }
    }
  };

  return (
    <div className="resetpw-page">
      <div className="resetpw-container">
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/2d5bc447f9b1db2f7fd687b9d4fe361a0f951eac?placeholderIfAbsent=true&apiKey=305fd9be184a488087180f4b7cfd2d98"
          alt="Background"
          className="resetpw-background-image"
        />
        <div className="resetpw-overlay"></div>

        <form
          className="resetpw-form-wrapper"
          onSubmit={handleResetPassword}
        >
          <div className="resetpw-title">Reset mật khẩu</div>

          <div className="resetpw-form-field">
            <div className="resetpw-field-label">Email</div>
            <input
              type="email"
              className="resetpw-input-field"
              placeholder="Nhập email đăng ký của bạn"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          {errorMessage && (
            <div className="resetpw-error-inline form-level-error">
              {errorMessage}
            </div>
          )}
          {successMessage && (
            <div className="resetpw-success-inline form-level-success">
              {successMessage}
            </div>
          )}

          <div className="resetpw-form-field">
            <button
              type="submit"
              className="resetpw-submit-button"
            >
              Gửi mật khẩu mới
            </button>
          </div>

          <div
            className="resetpw-back-login-link"
            onClick={() => navigate("/login")}
            style={{ cursor: "pointer" }}
          >
            Quay lại đăng nhập
          </div>
        </form>
      </div>
    </div>
  );
}

export default ResetPasswordPage;
