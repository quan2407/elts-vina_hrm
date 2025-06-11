import React from "react";
import "./Login.css";

function Login() {
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
        <div className="login-form-wrapper">
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
              placeholder="Enter your email"
            />
          </div>
          <div className="form-field">
            <div className="field-label">Password</div>
            <input
              type="password"
              className="input-field password-input"
              placeholder="Enter your password"
            />
          </div>
          <div className="form-options">
            <div className="remember-me-section">
              <div className="checkbox-field"></div>
              <div className="remember-me-text">Remember me</div>
            </div>
            <div className="reset-password-link">Reset Password?</div>
          </div>
          <div className="form-field">
            <button className="sign-in-button">Sign In</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
