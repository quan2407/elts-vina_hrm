package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.enums.FormulaType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitPositionUpdateDTO {
    private Long benefitId;
    private Long positionId;
    private BigDecimal formulaValue;
    private FormulaType formulaType;

}
