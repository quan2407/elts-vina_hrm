import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import "../styles/LoginPage.css"; // ✅ Dùng lại style của login

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
      setSuccessMessage(
        "Yêu cầu reset mật khẩu đã được gửi. Vui lòng chờ admin phê duyệt."
      );
    } catch (err) {
      console.error("Reset password failed:", err.response || err);
      if (err.response?.data?.message) {
        setErrorMessage(err.response.data.message);
      } else {
        setErrorMessage("Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
      }
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <form
          className="login-form-wrapper"
          onSubmit={handleResetPassword}
        >
          <img
            src="/logo.jpg"
            alt="Logo ELTS VINA"
            className="login-form-logo"
          />

          <div className="login-title">Reset mật khẩu</div>

          <div className="form-field">
            <div className="field-label">Email</div>
            <input
              type="email"
              className="input-field"
              placeholder="Nhập email đăng ký của bạn"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          {errorMessage && (
            <div className="login-error-inline form-level-error">
              {errorMessage}
            </div>
          )}
          {successMessage && (
            <div
              className="login-error-inline form-level-error"
              style={{ color: "#43a047" }} // ✅ màu xanh báo thành công
            >
              {successMessage}
            </div>
          )}

          <div className="form-field">
            <button
              type="submit"
              className="sign-in-button"
            >
              Gửi yêu cầu reset
            </button>
          </div>

          <div
            className="reset-password-link"
            onClick={() => navigate("/login")}
            style={{ marginTop: "16px", textAlign: "center" }}
          >
            Quay lại đăng nhập
          </div>
        </form>
      </div>
    </div>
  );
}

export default ResetPasswordPage;
