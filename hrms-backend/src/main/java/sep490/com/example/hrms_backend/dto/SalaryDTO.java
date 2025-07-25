package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDTO {


    private String employeeCode;
    private String employeeName;
    private String positionName;


    private BigDecimal basicSalary;


    private BigDecimal allowancePhone;
    private BigDecimal allowanceMeal;
    private BigDecimal allowanceAttendance;
    private BigDecimal allowanceTransport;


    private Float workingDays;
    private BigDecimal productionSalary;


    private Float overtimeHours;
    private BigDecimal overtimeSalary;


    private BigDecimal socialInsurance;
    private BigDecimal healthInsurance;
    private BigDecimal unemploymentInsurance;
    private BigDecimal unionFee;
    private BigDecimal totalDeduction;

    // ✔️ Phần F: Tổng thu nhập
    private BigDecimal totalIncome;

    // ✔️ Phần G: Mốc thời gian
    private LocalDate salaryMonth;
    private boolean locked;
}
