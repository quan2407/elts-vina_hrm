package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long id;

    @NotBlank
    @Column(name = "candidate_name", nullable = false)
    private String candidateName; // tên ứng viên

    @Email
    @Column(name = "email")
    private String email; // email ứng viên

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$")
    @Column(name = "phone_number")
    private String phoneNumber; // số điện thoại ứng viên

    @Column(name = "note")
    private String note; // ghi chú nội bộ

    @NotBlank
    @Column(name = "status")
    private String status; // trạng thái ứng viên (ví dụ: đang xét, bị loại,...)

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // thời điểm ứng viên ứng tuyển

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một ứng viên ứng tuyển vào một đợt tuyển dụng
    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;

    // Một ứng viên có thể được phỏng vấn nhiều lần (1 - n)
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<InterviewSchedule> interviewSchedules;
}
