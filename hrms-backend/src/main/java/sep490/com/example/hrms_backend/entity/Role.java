package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "role")
@Getter
@Setter
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
    private String roleName; // tên role, ví dụ: ROLE_ADMIN, ROLE_EMPLOYEE

    @Column(name = "description")
    private String description; // mô tả vai trò

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    @OneToMany(mappedBy = "role")
    private List<Account> accounts; // role này có nhiều account
}
