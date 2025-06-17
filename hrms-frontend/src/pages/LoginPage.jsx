import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";
import { jwtDecode } from "jwt-decode";
import "../styles/LoginPage.css";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await authService.login({ usernameOrEmail: email, password });
      console.log("Login response:", res.data);

      const token = res.data.accessToken;
      if (!token) {
        alert("Server không trả về accessToken");
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
        console.log("Không tìm thấy role phù hợp, chuyển unauthorized");
        navigate("/unauthorized");
      }
    } catch (err) {
      console.error("Login failed:", err.response || err);
      alert("Đăng nhập thất bại! Vui lòng kiểm tra lại email hoặc mật khẩu.");
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
          <div className="login-title">Login to your account.</div>

          <div className="form-field">
            <div className="field-label">E-mail Address</div>
            <input
              type="email"
              className="input-field email-input"
              placeholder="Enter your email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-field">
            <div className="field-label">Password</div>
            <input
              type="password"
              className="input-field password-input"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

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
              Sign In
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
