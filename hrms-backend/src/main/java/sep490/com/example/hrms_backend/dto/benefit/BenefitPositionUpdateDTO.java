package sep490.com.example.hrms_backend.dto.benefit;

import jakarta.validation.constraints.*;
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

    @NotNull(message = "formulaValue không được null")
    @PositiveOrZero(message = "formulaValue phải >= 0")
    @Digits(integer = 8, fraction = 0, message = "Chỉ cho phép số, tối đa 8 chữ số")
    @DecimalMax(value = "10000000", inclusive = true, message = "Giá trị tối đa là 10,000,000")
    private BigDecimal formulaValue;
    private FormulaType formulaType;

}
