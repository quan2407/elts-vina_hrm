import React, { useState } from "react";
import axiosClient from "../services/axiosClient";
import "../styles/OcrModal.css";

const CCCDModal = ({ onClose, onApply }) => {
  const [frontFile, setFrontFile] = useState(null);
  const [backFile, setBackFile] = useState(null);
  const [frontPreview, setFrontPreview] = useState(null);
  const [backPreview, setBackPreview] = useState(null);
  const [ocrStatus, setOcrStatus] = useState(null); // "success" | "fail" | null
  const [ocrError, setOcrError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleFileChange = (e, isFront) => {
    const file = e.target.files[0];
    if (!file) return;

    const previewUrl = URL.createObjectURL(file);
    if (isFront) {
      setFrontFile(file);
      setFrontPreview(previewUrl);
      setOcrStatus(null);
      setOcrError("");
    } else {
      setBackFile(file);
      setBackPreview(previewUrl);
    }
  };

  const handleExtract = async () => {
    if (!frontFile && !backFile) {
      alert("Vui lòng chọn ít nhất một ảnh mặt trước hoặc mặt sau");
      return;
    }

    const formData = new FormData();
    if (frontFile) formData.append("front", frontFile);
    if (backFile) formData.append("back", backFile);

    try {
      setLoading(true);
      setOcrStatus(null);
      setOcrError("");

      const res = await axiosClient.post("/ocr/scan-cccd", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setOcrStatus("success");
      onApply(res.data);
    } catch (err) {
      setOcrStatus("fail");
      const backendMessage =
        err?.response?.data?.message ||
        "Không thể trích xuất. Vui lòng thử lại.";
      setOcrError(backendMessage);
      console.error("OCR lỗi:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="ocr-modal-overlay">
      <div className="ocr-modal-content">
        <h2>Trích xuất từ CCCD</h2>

        {/* Ảnh mặt trước */}
        <div className="ocr-section">
          <label>Ảnh mặt trước:</label>
          <div className="ocr-frame">
            {frontPreview ? (
              <div className="ocr-preview-container">
                <img
                  src={frontPreview}
                  alt="preview front"
                />
                <div className="ocr-overlay">
                  <label className="ocr-reupload">
                    <input
                      type="file"
                      accept="image/*"
                      hidden
                      onChange={(e) => handleFileChange(e, true)}
                    />
                    Chọn lại ảnh
                  </label>
                </div>
              </div>
            ) : (
              <label className="ocr-upload-button">
                <input
                  type="file"
                  accept="image/*"
                  hidden
                  onChange={(e) => handleFileChange(e, true)}
                />
                <i className="fa fa-camera" /> Chọn ảnh mặt trước
              </label>
            )}
          </div>
          {ocrStatus === "success" && (
            <p className="ocr-valid-text">✔ Ảnh hợp lệ</p>
          )}
          {ocrStatus === "fail" && (
            <p className="ocr-invalid-text">{ocrError}</p>
          )}
        </div>

        {/* Ảnh mặt sau */}
        <div className="ocr-section">
          <label>Ảnh mặt sau (không bắt buộc):</label>
          <div className="ocr-frame">
            {backPreview ? (
              <div className="ocr-preview-container">
                <img
                  src={backPreview}
                  alt="preview back"
                />
                <div className="ocr-overlay">
                  <label className="ocr-reupload">
                    <input
                      type="file"
                      accept="image/*"
                      hidden
                      onChange={(e) => handleFileChange(e, false)}
                    />
                    Chọn lại ảnh
                  </label>
                </div>
              </div>
            ) : (
              <label className="ocr-upload-button">
                <input
                  type="file"
                  accept="image/*"
                  hidden
                  onChange={(e) => handleFileChange(e, false)}
                />
                <i className="fa fa-camera" /> Chọn ảnh mặt sau
              </label>
            )}
          </div>
        </div>

        <div className="ocr-button-group">
          <button
            onClick={handleExtract}
            disabled={loading}
          >
            {loading ? "Đang trích xuất..." : "Trích xuất"}
          </button>
          <button
            className="cancel-btn"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default CCCDModal;
