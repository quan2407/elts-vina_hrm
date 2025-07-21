package sep490.com.example.hrms_backend.enums;

public enum ApplicationStatus {
    PENDING_MANAGER_APPROVAL,   // Nhân viên vừa gửi, chờ quản lý duyệt
    MANAGER_APPROVED,           // Quản lý đã duyệt, chờ HR duyệt
    MANAGER_REJECTED,           // Quản lý từ chối
    HR_APPROVED,                // HR đã duyệt xong, đơn được chấp nhận
    HR_REJECTED,                // HR từ chối
    CANCELLED_BY_EMPLOYEE       // Nhân viên tự hủy đơn trước khi xử lý xong
}

