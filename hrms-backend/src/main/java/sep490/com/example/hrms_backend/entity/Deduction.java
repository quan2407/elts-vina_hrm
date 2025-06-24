package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "deduction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deduction {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deduction_id")
    private Long id;

    @NotBlank
    @Column(name = "deduction_type")
    private String deductionType;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "amount")
    private Double amount;


    @ManyToOne
    @JoinColumn(name = "salary_id")
    private Salary salary;
}
