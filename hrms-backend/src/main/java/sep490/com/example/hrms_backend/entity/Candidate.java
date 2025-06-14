package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import sep490.com.example.hrms_backend.enums.CandidateStatus;

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

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CandidateStatus status; // trạng thái ứng viên (ví dụ: đang xét, bị loại,...)

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // thời điểm ứng viên ứng tuyển

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    @ManyToMany(mappedBy = "candidates")
    private List<Recruitment> recruitments;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<InterviewSchedule> interviewSchedules;
}
