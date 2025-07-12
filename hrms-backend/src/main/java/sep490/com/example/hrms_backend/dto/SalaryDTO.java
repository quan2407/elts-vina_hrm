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

    // ✔️ Thông tin nhân viên
    private String employeeCode;
    private String employeeName;
    private String positionName;

    // ✔️ Phần A: Mức lương cơ bản
    private BigDecimal basicSalary;

    // ✔️ Phần B: Phụ cấp
    private BigDecimal allowancePhone;
    private BigDecimal allowanceMeal;
    private BigDecimal allowanceAttendance;
    private BigDecimal allowanceTransport;

    // ✔️ Phần C: Lương sản xuất
    private Float workingDays;
    private BigDecimal productionSalary;

    // ✔️ Phần D: Lương thêm giờ
    private Float overtimeHours;
    private BigDecimal overtimeSalary;

    // ✔️ Phần E: Khấu trừ
    private BigDecimal socialInsurance;        // BHXH 8%
    private BigDecimal healthInsurance;        // BHYT 1.5%
    private BigDecimal unemploymentInsurance;  // BHTN 1%
    private BigDecimal unionFee;               // Đoàn phí
    private BigDecimal totalDeduction;

    // ✔️ Phần F: Tổng thu nhập
    private BigDecimal totalIncome;

    // ✔️ Phần G: Mốc thời gian
    private LocalDate salaryMonth;
}
