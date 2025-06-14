package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username; // tên đăng nhập

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // mật khẩu đã mã hoá

    @Email
    @Column(name = "email")
    private String email; // email dùng để khôi phục mật khẩu

    @Column(name = "is_active")
    private Boolean isActive; // tài khoản có đang hoạt động không

    @Column(name = "created_at")
    private LocalDateTime createdAt; // thời điểm tạo tài khoản

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // thời điểm cập nhật gần nhất

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt; // lần đăng nhập cuối

    @Column(name = "login_attempts")
    private Integer loginAttempts; // số lần đăng nhập thất bại

    @Column(name = "must_change_password")
    private Boolean mustChangePassword; // có yêu cầu đổi mật khẩu khi đăng nhập?

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role; // tài khoản thuộc 1 vai trò

    @OneToMany(mappedBy = "account")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "account")
    private List<SystemLog> systemLogs;
}
