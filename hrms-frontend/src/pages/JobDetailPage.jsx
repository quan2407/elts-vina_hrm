import React, { useState } from "react";
import "../assets/styles/JobDetailPage.css";
import Header from "../component/Header.jsx";

const JobDetailPage = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    subject: "",
    message: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Contact form submitted:", formData);
    // Here you would typically send the data to your backend
    alert(
      "Cảm ơn bạn đã liên hệ với chúng tôi! Chúng tôi sẽ phản hồi sớm nhất có thể.",
    );
    setFormData({
      name: "",
      email: "",
      phone: "",
      subject: "",
      message: "",
    });
  };

  return (
    <div className="contact-page">
      
<Header />

      <main className="contact-content">
        <div className="contact-container">
          <div className="contact-info-section">
            <h2 className="section-title">Thông tin liên hệ</h2>

            <div className="contact-item">
              <div className="contact-icon">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M12 2C8.13 2 5 5.13 5 9C5 14.25 12 22 12 22S19 14.25 19 9C19 5.13 15.87 2 12 2ZM12 11.5C10.62 11.5 9.5 10.38 9.5 9S10.62 6.5 12 6.5S14.5 7.62 14.5 9S13.38 11.5 12 11.5Z"
                    fill="#132745"
                  />
                </svg>
              </div>
              <div className="contact-details">
                <h3 className="contact-label">Địa chỉ</h3>
                <p className="contact-value">
                  123 Đường ABC, Quận 1, TP.HCM, Việt Nam
                </p>
              </div>
            </div>

            <div className="contact-item">
              <div className="contact-icon">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M6.62 10.79C8.06 13.62 10.38 15.94 13.21 17.38L15.41 15.18C15.69 14.9 16.08 14.82 16.43 14.93C17.55 15.3 18.75 15.5 20 15.5C20.55 15.5 21 15.95 21 16.5V20C21 20.55 20.55 21 20 21C10.61 21 3 13.39 3 4C3 3.45 3.45 3 4 3H7.5C8.05 3 8.5 3.45 8.5 4C8.5 5.25 8.7 6.45 9.07 7.57C9.18 7.92 9.1 8.31 8.82 8.59L6.62 10.79Z"
                    fill="#132745"
                  />
                </svg>
              </div>
              <div className="contact-details">
                <h3 className="contact-label">Điện thoại</h3>
                <p className="contact-value">0987654321</p>
              </div>
            </div>

            <div className="contact-item">
              <div className="contact-icon">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M20 4H4C2.9 4 2.01 4.9 2.01 6L2 18C2 19.1 2.9 20 4 20H20C21.1 20 22 19.1 22 18V6C22 4.9 21.1 4 20 4ZM20 8L12 13L4 8V6L12 11L20 6V8Z"
                    fill="#132745"
                  />
                </svg>
              </div>
              <div className="contact-details">
                <h3 className="contact-label">Email</h3>
                <p className="contact-value">contact@eltsvina.com</p>
              </div>
            </div>

            <div className="contact-item">
              <div className="contact-icon">
                <svg
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M12 2C6.48 2 2 6.48 2 12S6.48 22 12 22S22 17.52 22 12S17.52 2 12 2ZM11 19.93C7.05 19.44 4 16.08 4 12C4 11.38 4.08 10.79 4.21 10.21L9 15V16C9 17.1 9.9 18 11 18V19.93ZM17.9 17.39C17.64 16.58 16.9 16 16 16H15V13C15 12.45 14.55 12 14 12H8V10H10C10.55 10 11 9.55 11 9V7H13C14.1 7 15 6.1 15 5V4.59C17.93 5.77 20 8.64 20 12C20 14.08 19.2 15.97 17.9 17.39Z"
                    fill="#132745"
                  />
                </svg>
              </div>
              <div className="contact-details">
                <h3 className="contact-label">Website</h3>
                <p className="contact-value">www.eltsvina.com</p>
              </div>
            </div>
          </div>

          <div className="contact-form-section">
            <h2 className="section-title">Gửi tin nhắn cho chúng tôi</h2>

            <form className="contact-form" onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-field">
                  <label className="field-label">Họ và tên *</label>
                  <input
                    type="text"
                    name="name"
                    className="input-field"
                    placeholder="Nhập họ và tên của bạn"
                    value={formData.name}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-field">
                  <label className="field-label">Email *</label>
                  <input
                    type="email"
                    name="email"
                    className="input-field"
                    placeholder="Nhập địa chỉ email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-field">
                  <label className="field-label">Số điện thoại</label>
                  <input
                    type="tel"
                    name="phone"
                    className="input-field"
                    placeholder="Nhập số điện thoại"
                    value={formData.phone}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-field">
                  <label className="field-label">Chủ đề *</label>
                  <input
                    type="text"
                    name="subject"
                    className="input-field"
                    placeholder="Nhập chủ đề"
                    value={formData.subject}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-field full-width">
                <label className="field-label">Tin nhắn *</label>
                <textarea
                  name="message"
                  className="textarea-field"
                  placeholder="Nhập tin nhắn của bạn..."
                  rows="6"
                  value={formData.message}
                  onChange={handleInputChange}
                  required
                ></textarea>
              </div>

              <button type="submit" className="submit-button">
                Gửi tin nhắn
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 20 20"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M2.5 10L17.5 10M17.5 10L12.5 5M17.5 10L12.5 15"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              </button>
            </form>
          </div>
        </div>
      </main>

      <footer className="footer">
        <div className="footer-brand">ELTS VINA</div>
        <div className="footer-credits">
          <div className="credits-top">Điện thoại liên hệ</div>
          <div className="credits-bottom">0987654321</div>
        </div>
      </footer>
    </div>
  );
};

export default JobDetailPage ;
