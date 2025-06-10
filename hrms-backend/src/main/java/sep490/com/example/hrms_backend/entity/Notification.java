package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // nội dung thông báo

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt; // thời điểm tạo thông báo

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Thông báo này được gửi đến một tài khoản cụ thể
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
