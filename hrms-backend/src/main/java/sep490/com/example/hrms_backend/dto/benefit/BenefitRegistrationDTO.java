package sep490.com.example.hrms_backend.dto.benefit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.dto.EmployeeDetailDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitRegistrationDTO {
    private Long id;
    private LocalDateTime registeredAt;

    @NotNull(message = "Register status is required")
    private Boolean isRegister;

    private EmployeeBasicDetailDTO employee;

}
