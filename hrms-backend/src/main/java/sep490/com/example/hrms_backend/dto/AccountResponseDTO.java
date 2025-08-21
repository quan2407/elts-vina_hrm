package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long accountId;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private String role;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String positionName;
    private String departmentName;
    private String lineName;
}

