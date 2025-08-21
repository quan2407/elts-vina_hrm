package sep490.com.example.hrms_backend.enums;

public enum NotificationType {
    // nghỉ phép
    LEAVE_REQUEST,
    // phê duyệt đơn từ
    APPROVAL,
    //Lịch phỏng vấn
    INTERVIEW_SCHEDULE,
    //Line làm việc
    LINE_CHANGED,
    //chuyển tổ trưởng
    LEADER_CHANGE,

    //Lịch làm việc
    SHIFT_CHANGED,
    APPLICATION_SUBMITTED,          // NV → QLSX
    APPLICATION_NEEDS_HR_APPROVAL,  // QLSX → HR
    APPLICATION_APPROVED,           // HR → NV (và/hoặc QLSX)
    APPLICATION_REJECTED,           // QLSX/HR → NV
}
