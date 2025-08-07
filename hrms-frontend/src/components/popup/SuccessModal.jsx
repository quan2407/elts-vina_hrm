import React from "react";
import "../../styles/Modal.css";

const SuccessModal = ({ title, message, onClose, type = "success" }) => {
    const buttonClass = type === "success" ? "btn-confirm" : "btn-danger";

    return (
        <div className="modal-overlay">
            <div className={`modal-box ${type}`}>
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
