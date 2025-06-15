package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recruitment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long id;

    @NotBlank
    private String title;

    private String workLocation;

    private String employmentType;

    private String jobDescription;

    private String jobRequirement;

    private String benefits;

    private String salaryRange;

    @Min(1)
    private Integer quantity;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    // Quan hệ mới
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateRecruitment> candidateRecruitments;
}
