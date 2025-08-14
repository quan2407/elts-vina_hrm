package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDTO {

    private String employeeCode;
    private String employeeName;
    private String positionName;
    private String departmentName;
    private String lineName;
    private BigDecimal basicSalary;
    private Float workingDays;
    private BigDecimal productionSalary;
    private Float overtimeHours;
    private BigDecimal overtimeSalary;

    private BigDecimal totalDeduction;
    private BigDecimal totalIncome;
    private LocalDate salaryMonth;
    private boolean locked;

    private List<SalaryBenefitDTO> appliedBenefits;
}

