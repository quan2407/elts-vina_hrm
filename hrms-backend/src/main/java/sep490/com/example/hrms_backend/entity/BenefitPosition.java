package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.enums.FormulaType;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BenefitPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_position_id")
    private Long id;

    @Column(name = "formula_value")
    private BigDecimal formulaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "formula_type")
    private FormulaType formulaType;

    //##########################################
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;

    //  Một vị trí có thể có nhiều lượt đăng ký phúc lợi
    @OneToMany(mappedBy = "benefitPosition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BenefitRegistration> registrations;


}
