package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "salary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long id;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "basic_salary")
    private BigDecimal basicSalary; // lương cơ bản

    @PastOrPresent
    @Column(name = "date_paid_at")
    private LocalDateTime datePaidAt; // ngày trả lương

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt; // ngày tạo

    @PastOrPresent
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // ngày cập nhật gần nhất

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Lương này gắn với một bảng công tháng
    @OneToOne
    @JoinColumn(name = "monthly_attendance_id")
    private MonthlyAttendance monthlyAttendance;

    // Một bảng lương có thể có nhiều khoản phụ cấp
    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Allowance> allowances;

    // Một bảng lương có thể có nhiều khoản trừ
    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductions;
}
