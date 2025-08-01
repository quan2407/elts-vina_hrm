package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import sep490.com.example.hrms_backend.enums.BenefitType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "benefit_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitRegistration {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_registration_id")
    private Long id;

    @PastOrPresent
    @CreationTimestamp
    @Column(name = "registered_at")
    private LocalDateTime registeredAt; // thời điểm đăng ký

    @NotNull
    @Column(name = "is_register")
    private Boolean isRegister = false ;

    @ManyToMany
    @JoinTable(
            name = "benefit_registration_employee", // Tên bảng nối
            joinColumns = @JoinColumn(name = "benefit_registration_id"), // Khóa chính của bảng BenefitRegistration
            inverseJoinColumns = @JoinColumn(name = "employee_id") // Khóa chính của bảng Employee
    )
    private List<Employee> registrations; // Danh sách Employee đăng ký

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "benefit_id", nullable = false, updatable = false)
//    private Benefit benefit;

    //  Đăng ký này thuộc về một BenefitPosition
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_position_id", nullable = false, updatable = false)
    private BenefitPosition benefitPosition;

    //AI là người đăng kí
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false, updatable = false)
    private Employee employee;
}
