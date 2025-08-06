package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import sep490.com.example.hrms_backend.enums.BenefitType;

import java.math.BigDecimal;

@Entity
@Table(name = "salary_benefit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    @Column(name = "benefit_title", nullable = false)
    private String benefitTitle; // tiện lợi khi export

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}

