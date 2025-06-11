package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_log_id")
    private Long id;

    @NotBlank
    @Column(name = "action")
    private String action; // hành động (VD: CREATE, UPDATE, DELETE)

    @NotBlank
    @Column(name = "table_name", nullable = false)
    private String tableName;
    // bảng bị ảnh hưởng (VD: employee, salary...)

    @Column(name = "attribute_name")
    private String attribute; // thuộc tính cụ thể (VD: email, basic_salary...)

    @PastOrPresent
    @Column(name = "action_at")
    private LocalDateTime actionAt; // thời điểm xảy ra hành động

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Hành động này được thực hiện bởi một tài khoản
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
