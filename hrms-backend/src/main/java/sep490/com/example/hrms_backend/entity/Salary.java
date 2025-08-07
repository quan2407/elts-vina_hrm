package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "locked")
    private boolean locked = false;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalaryBenefit> salaryBenefits = new ArrayList<>();

}


