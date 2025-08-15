package sep490.com.example.hrms_backend.dto.benefit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionRegistrationStatsDTO {
    private Long benefitId;
    private Long positionId;
    private long totalEmployees;
    private long totalRegistered;
}
