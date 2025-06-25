package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "allowance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allowance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allowance_id")
    private Long id;

    @NotBlank
    @Column(name = "allowance_type")
    private String allowanceType;

    @DecimalMin(value = "0.0")
    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "salary_id")
    private Salary salary;
}
