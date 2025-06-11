package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title; // tiêu đề đơn (VD: Đơn xin nghỉ phép)

    @NotBlank
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // nội dung lý do xin

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate; // ngày bắt đầu áp dụng đơn

    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate; // ngày kết thúc áp dụng đơn

    @NotBlank
    @Column(name = "status")
    private String status; // trạng thái đơn (đang chờ, đã duyệt, từ chối...)

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt; // thời điểm gửi đơn

    @PastOrPresent
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // thời điểm cập nhật gần nhất

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Đơn được gửi bởi một nhân viên
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Đơn thuộc về một loại đơn xác định
    @ManyToOne
    @JoinColumn(name = "application_type_id")
    private ApplicationType applicationType;

    // Đơn này có thể có nhiều bước duyệt
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationApprovalStep> approvalSteps;
}
