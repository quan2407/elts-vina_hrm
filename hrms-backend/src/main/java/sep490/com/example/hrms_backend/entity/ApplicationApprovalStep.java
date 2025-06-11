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

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_approval_step_id")
    private Long id;

    @Min(1)
    @Column(name = "step")
    private int step; // thứ tự bước phê duyệt

    @NotBlank
    @Column(name = "status")
    private String status; // trạng thái bước này (đang chờ, đã duyệt, từ chối...)

    @Column(name = "note")
    private String note; // ghi chú hoặc nhận xét của người duyệt

    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // thời điểm duyệt

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Bước duyệt này thuộc về một đơn cụ thể
    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    // Người phê duyệt là một nhân viên trong công ty
    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Employee approver;
}
