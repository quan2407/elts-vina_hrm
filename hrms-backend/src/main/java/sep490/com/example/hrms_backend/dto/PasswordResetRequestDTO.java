package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetRequestDTO {
    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String departmentName;
    private String positionName;
    private String lineName;
    private String email;
    private LocalDateTime requestedAt;
    private Boolean approved;
}
