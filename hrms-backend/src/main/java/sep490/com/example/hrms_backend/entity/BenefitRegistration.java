package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @Column(name = "note")
    private String note; // ghi chú (nếu có)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Đăng ký này thuộc về một phúc lợi
    @ManyToOne
    @JoinColumn(name = "benefit_id")
    private Benefit benefit;

    // Đăng ký này được tạo bởi một nhân viên
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
