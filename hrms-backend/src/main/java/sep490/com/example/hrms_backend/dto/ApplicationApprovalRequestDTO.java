package sep490.com.example.hrms_backend.dto;

import lombok.Data;

@Data
public class ApplicationApprovalRequestDTO {
    private boolean approved;
    private String note;
}
