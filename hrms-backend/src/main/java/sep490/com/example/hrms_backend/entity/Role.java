package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Set;

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

//    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======
//
// 🔗 Quan hệ ngược lại với Account (many-to-many)
    @ManyToMany(mappedBy = "roles")
    private Set<Account> accounts;
}
