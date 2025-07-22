package sep490.com.example.hrms_backend.dto;

import lombok.Builder;
import lombok.Data;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApplicationDetailDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long applicationTypeId;
    private String applicationTypeName;
    private ApplicationStatus status;
    private String statusLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String attachmentPath;
    private String rejectReason;

    private List<ApprovalStepDTO> approvalSteps;
}
