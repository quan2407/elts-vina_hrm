package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitRegistrationDTO {
    private Long id;
    private LocalDateTime registeredAt;
    private String status;
    private String note;
}
