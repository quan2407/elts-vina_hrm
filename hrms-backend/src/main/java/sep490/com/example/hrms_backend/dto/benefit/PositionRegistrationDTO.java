package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.Formula;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.enums.FormulaType;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionRegistrationDTO {
    private Long positionId;
    private String positionName;
    private BigDecimal formulaValue;
    private FormulaType formulaType;

    private List<DepartmentDTO> departments;

    private List<BenefitRegistrationDTO> registrations;

}
