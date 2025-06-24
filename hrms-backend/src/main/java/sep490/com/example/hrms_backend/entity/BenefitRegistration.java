package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "benefit_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitRegistration {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_registration_id")
    private Long id;

    @PastOrPresent
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @NotBlank
    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;


    @ManyToOne
    @JoinColumn(name = "benefit_id")
    private Benefit benefit;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
