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

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long id;

    @NotBlank
    private String title; // tiêu đề đợt tuyển dụng

    private String workLocation; // địa điểm làm việc

    private String employmentType; // loại hình (toàn thời gian, part-time,...)

    private String jobDescription; // mô tả công việc

    private String jobRequirement; // yêu cầu tuyển dụng

    private String benefits; // quyền lợi

    private String salaryRange; // khoảng lương

    @Min(1)
    private Integer quantity; // số lượng cần tuyển

    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // ngày hết hạn tuyển dụng

    @Column(name = "create_at")
    private LocalDateTime createAt; // ngày tạo

    @Column(name = "update_at")
    private LocalDateTime updateAt; // ngày cập nhật

    @NotBlank
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status; // trạng thái (đang mở, đã đóng, v.v.)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Mỗi đợt tuyển dụng thuộc về 1 phòng ban
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Người tạo là một nhân viên cụ thể
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    // Một đợt tuyển dụng có nhiều ứng viên
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL)
    private List<Candidate> candidates;
}
