package sep490.com.example.hrms_backend.dto;

import lombok.Builder;
import lombok.Data;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalStepDTO {
    private int step;
    private String approverName;
    private ApprovalStepStatus status;
    private String note;
    private LocalDateTime approvedAt;
}
