package sep490.com.example.hrms_backend.dto;

import lombok.*;
import sep490.com.example.hrms_backend.enums.BenefitType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryBenefitDTO {
    private String title;
    private BenefitType type;
    private BigDecimal amount;
}

