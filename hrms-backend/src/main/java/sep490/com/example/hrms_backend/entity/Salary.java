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



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long id;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "basic_salary")
    private BigDecimal basicSalary;

    @PastOrPresent
    @Column(name = "date_paid_at")
    private LocalDateTime datePaidAt;

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToOne
    @JoinColumn(name = "monthly_attendance_id")
    private MonthlyAttendance monthlyAttendance;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Allowance> allowances;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductions;
}
