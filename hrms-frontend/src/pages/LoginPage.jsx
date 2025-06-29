import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import { jwtDecode } from "jwt-decode";
import "../styles/LoginPage.css";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [errorFields, setErrorFields] = useState({});
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setErrorFields({});

    try {
      const res = await authService.login({ usernameOrEmail: email, password });
      console.log("Login response:", res.data);

      const token = res.data.accessToken;
      if (!token) {
        setErrorMessage("Server không trả về accessToken");
        return;
      }

      localStorage.setItem("accessToken", token);

      const decoded = jwtDecode(token);
      console.log("Decoded token:", decoded);

      const roles = decoded.roles || [];
      console.log("Roles:", roles);

      if (roles.includes("ROLE_ADMIN")) {
        navigate("/accounts");
      } else if (roles.includes("ROLE_HR")) {
        navigate("/employee-management");
      } else {
        navigate("/unauthorized");
      }
    } catch (err) {
      console.error("Login failed:", err.response || err);
      if (err.response && err.response.data) {
        const serverData = err.response.data;

        if (serverData.errors) {
          setErrorFields(serverData.errors);
        } else if (
          typeof serverData === "object" &&
          (serverData.password || serverData.usernameOrEmail)
        ) {
          setErrorFields(serverData);
        } else if (serverData.message) {
          setErrorMessage(serverData.message);
        } else {
          setErrorMessage(
            "Đăng nhập thất bại! Vui lòng kiểm tra lại thông tin."
          );
        }
      } else {
        setErrorMessage("Không thể kết nối server. Vui lòng thử lại sau.");
      }
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/2d5bc447f9b1db2f7fd687b9d4fe361a0f951eac?placeholderIfAbsent=true&apiKey=305fd9be184a488087180f4b7cfd2d98"
          alt="Background"
          className="background-image"
        />
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/7b0992b36149c43e127ce390c6b217aa4b75a7e6?placeholderIfAbsent=true&apiKey=305fd9be184a488087180f4b7cfd2d98"
          alt="Header Logo"
          className="header-logo"
        />
        <div className="overlay"></div>
        <form
          className="login-form-wrapper"
          onSubmit={handleLogin}
        >
          <img
            src="https://cdn.builder.io/api/v1/image/assets/TEMP/11da01531e0457dff7f19452706b1cf360116459?placeholderIfAbsent=true&apiKey=305fd9be184a488087180f4b7cfd2d98"
            alt="Company Logo"
            className="company-logo"
          />
          <div className="login-title">Đăng nhập tài khoản.</div>

          <div className="form-field">
            <div className="field-label">Tên đăng nhập hoặc email</div>
            <input
              type="text" // ✅ Cho phép nhập cả username hoặc email
              className="input-field email-input"
              placeholder="Nhập tên đăng nhập của bạn hoặc email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            {errorFields.usernameOrEmail && (
              <div className="login-error-inline">
                {errorFields.usernameOrEmail.join(", ")}
              </div>
            )}
          </div>

          <div className="form-field">
            <div className="field-label">Mật khẩu</div>{" "}
            {/* ✅ Đổi nhãn thành Mật khẩu */}
            <input
              type="password"
              className="input-field password-input"
              placeholder="Nhập mật khẩu của bạn (độ dài từ 4 - 50 ký tự)"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {errorFields.password && (
              <div className="login-error-inline">
                {errorFields.password.join(", ")}
              </div>
            )}
          </div>

          {errorMessage && (
            <div className="login-error-inline form-level-error">
              {errorMessage}
            </div>
          )}

          <div className="form-options">
            <div className="remember-me-section">
              <input
                type="checkbox"
                id="remember"
                className="checkbox-field"
              />
              <label
                htmlFor="remember"
                className="remember-me-text"
              >
                Remember me
              </label>
            </div>
            <div
              className="reset-password-link"
              onClick={() => navigate("/reset-password")}
              style={{ cursor: "pointer" }}
            >
              Reset Password?
            </div>
          </div>

          <div className="form-field">
            <button
              type="submit"
              className="sign-in-button"
            >
              Đăng nhập
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
