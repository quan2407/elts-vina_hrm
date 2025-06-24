package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_approval_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationApprovalStep {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_approval_step_id")
    private Long id;

    @Min(1)
    @Column(name = "step")
    private int step;

    @NotBlank
    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Employee approver;
}
