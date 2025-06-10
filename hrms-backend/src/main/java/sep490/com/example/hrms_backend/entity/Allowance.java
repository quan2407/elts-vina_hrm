package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "allowance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allowance {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allowance_id")
    private Long id;

    @NotBlank
    @Column(name = "allowance_type")
    private String allowanceType; // loại phụ cấp (VD: ăn trưa, xăng xe...)

    @DecimalMin(value = "0.0")
    @Column(name = "amount")
    private BigDecimal amount; // số tiền phụ cấp

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Khoản phụ cấp này thuộc về một bảng lương
    @ManyToOne
    @JoinColumn(name = "salary_id")
    private Salary salary;
}
