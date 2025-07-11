import React from "react";
import "../../styles/Modal.css";

const SuccessModal = ({ title, message, onClose }) => {
    return (
        <div className="modal-overlay">
            <div className="modal-box success">
                <h3 className="modal-title">{title || "Thành công"}</h3>
                <p className="modal-content">{message}</p>
                <div className="modal-actions">
                    <button className="btn-confirm" onClick={onClose}>OK</button>
                </div>
            </div>
        </div>
    );
};

export default SuccessModal;
