package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSchedule {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_schedule_id")
    private Long id;

    @NotNull
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt; // thời gian phỏng vấn

    @NotBlank
    private String status; // trạng thái buổi phỏng vấn (v.d. chờ phỏng vấn, đã hoàn tất...)

    private String feedback; // nhận xét từ người phỏng vấn

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "interviewer_id")
    private Employee interviewer;

    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment; // đợt tuyển dụng của buổi phỏng vấn
}
