package sep490.com.example.hrms_backend.dto;

import lombok.Builder;
import lombok.Data;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class ApplicationDetailDTO {
    private Long id;
    private String employeeId;
    private String employeeCode;
    private String employeeName;
    private String positionName;
    private String departmentName;
    private String lineName;
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
    private String leaveCode;
    private Boolean isHalfDay;
    private String halfDayType;
    private LocalTime checkIn;
    private LocalTime checkOut;

    private List<ApprovalStepDTO> approvalSteps;
    private boolean isCreator;

}
