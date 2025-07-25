package sep490.com.example.hrms_backend.dto;

import lombok.Builder;
import lombok.Data;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationApprovalListItemDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private String applicationTypeName;
    private ApplicationStatus status;
    private String statusLabel;
    private LocalDateTime createdAt;

    private String employeeCode;
    private String employeeName;
    private String positionName;
    private String departmentName;
    private String lineName;
}

