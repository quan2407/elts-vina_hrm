package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // ✅ [A] Phần cố định
    @DecimalMin("0.0")
    @Column(name = "basic_salary", nullable = false)
    private BigDecimal basicSalary;

    // ✅ [B] Phụ cấp
    @DecimalMin("0.0")
    @Column(name = "allowance_phone")
    private BigDecimal allowancePhone;

    @DecimalMin("0.0")
    @Column(name = "allowance_meal")
    private BigDecimal allowanceMeal;

    @DecimalMin("0.0")
    @Column(name = "allowance_attendance")
    private BigDecimal allowanceAttendance;

    @DecimalMin("0.0")
    @Column(name = "allowance_transport")
    private BigDecimal allowanceTransport;

    // ✅ [C] Lương sản xuất
    @Column(name = "working_days")
    private Float workingDays;

    @DecimalMin("0.0")
    @Column(name = "production_salary")
    private BigDecimal productionSalary;

    // ✅ [D] Lương thêm giờ
    @Column(name = "overtime_hours")
    private Float overtimeHours;

    @DecimalMin("0.0")
    @Column(name = "overtime_salary")
    private BigDecimal overtimeSalary;

    // ✅ [E] Các khoản khấu trừ
    @DecimalMin("0.0")
    @Column(name = "social_insurance") // BHXH 8%
    private BigDecimal socialInsurance;

    @DecimalMin("0.0")
    @Column(name = "health_insurance") // BHYT 1.5%
    private BigDecimal healthInsurance;

    @DecimalMin("0.0")
    @Column(name = "unemployment_insurance") // BHTN 1%
    private BigDecimal unemploymentInsurance;

    @DecimalMin("0.0")
    @Column(name = "union_fee") // Đoàn phí
    private BigDecimal unionFee;

    @DecimalMin("0.0")
    @Column(name = "total_deduction") // Tổng trừ
    private BigDecimal totalDeduction;

    // ✅ [F] Tổng thu nhập
    @DecimalMin("0.0")
    @Column(name = "total_income")
    private BigDecimal totalIncome;

    // ✅ [G] Mốc thời gian
    @NotNull
    @Column(name = "salary_month")
    private LocalDate salaryMonth;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "attendance_record_id")
    private AttendanceRecord attendanceRecord;
}


