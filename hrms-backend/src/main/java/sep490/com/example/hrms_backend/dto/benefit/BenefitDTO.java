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

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Max participants is required")
    @Min(value = 1, message = "Max participants must be at least 1")
    private Integer maxParticipants;

    @NotNull(message = "Active status is required")
    private Boolean isActive;


    private BenefitType benefitType;

    @NotNull(message = "defaultFormulaValue is required")
    private BigDecimal defaultFormulaValue;

    @NotNull(message = "defaultFormulaType is required")
    private FormulaType defaultFormulaType;


    private String detail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PositionRegistrationDTO> positions;

}
