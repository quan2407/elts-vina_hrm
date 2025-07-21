package sep490.com.example.hrms_backend.enums;

public enum ApplicationStatus {
    PENDING_MANAGER_APPROVAL("Chờ quản lý duyệt"),
    MANAGER_APPROVED("Quản lý đã duyệt"),
    MANAGER_REJECTED("Quản lý từ chối"),
    HR_APPROVED("HR đã duyệt"),
    HR_REJECTED("HR từ chối"),
    CANCELLED_BY_EMPLOYEE("Nhân viên hủy");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

