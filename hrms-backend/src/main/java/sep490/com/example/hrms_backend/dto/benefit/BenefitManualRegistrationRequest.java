package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitManualRegistrationRequest {
    private Long benefitId;
    private Long positionId;
    private List<String> keywords; // email hoặc username
}
