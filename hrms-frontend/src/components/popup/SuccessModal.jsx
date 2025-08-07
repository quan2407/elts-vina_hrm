import React from "react";
import "../../styles/Modal.css";
import { FaCheckCircle, FaTimesCircle } from "react-icons/fa";

const SuccessModal = ({ title, message, onClose, type = "success" }) => {
    const buttonClass = type === "success" ? "btn-confirm" : "btn-danger";
    const Icon = type === "success" ? FaCheckCircle : FaTimesCircle;
    const iconColor = type === "success" ? "#4caf50" : "#e53935";

    return (
        <div className="modal-overlay">
            <div className={`modal-box ${type}`}>
                <Icon size={48} color={iconColor} style={{ marginBottom: 16 }} />
                <h3 className="modal-title">{title || "Thông báo"}</h3>
                <p className="modal-content">{message}</p>
                <div className="modal-actions">
                    <button className={buttonClass} onClick={onClose}>OK</button>
                </div>
            </div>
        </div>
    );
};
export default SuccessModal;
