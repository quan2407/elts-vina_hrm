package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.enums.FormulaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "benefit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Benefit {

    // === Core fields ===
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_id")
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;


    @Column(name = "description")
    private String description;

    @Column(name = "detail")
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;


    @Column(name = "default_formula_value")
    private BigDecimal defaultFormulaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_formula_type")
    private FormulaType defaultFormulaType;


    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate; // ngày bắt đầu áp dụng


    @Column(name = "end_date")
    private LocalDate endDate; // ngày kết thúc áp dụng

    @Min(1)
    @Column(name = "max_participants")
    private Integer maxParticipants; // số người tối đa được hưởng

    @Column(name = "is_active")
    private Boolean isActive; // trạng thái hoạt động

    // ===Audit==
    @CreatedDate
    @PastOrPresent
    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // thời điểm tạo

    @LastModifiedDate
    @UpdateTimestamp
    @PastOrPresent
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // thời điểm cập nhật gần nhất



    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một phúc lợi có thể được nhiều nhân viên đăng ký
//    @OneToMany(mappedBy = "benefit", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<BenefitRegistration> registrations;

    @OneToMany(mappedBy = "benefit",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BenefitPosition> benefitPositions;

}
