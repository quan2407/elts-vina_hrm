package sep490.com.example.hrms_backend.dto.benefit;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.enums.FormulaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitDTO {
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;






    private Boolean isActive;

    private Integer numberOfEmployee;

    private BenefitType benefitType;

    private BigDecimal defaultFormulaValue;

    private FormulaType defaultFormulaType;


    private String detail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PositionRegistrationDTO> positions;

}
