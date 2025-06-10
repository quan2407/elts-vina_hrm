package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @NotBlank
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName; // tên vai trò (admin, hr, employee...)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một vai trò có thể được gán cho nhiều tài khoản (vai trò chính)
    @OneToMany(mappedBy = "role")
    private List<Account> accounts;

    // Một vai trò có thể là vai trò phụ cho nhiều tài khoản (thông qua bảng trung gian)
    @OneToMany(mappedBy = "role")
    private List<AccountRole> accountRoles;
}
