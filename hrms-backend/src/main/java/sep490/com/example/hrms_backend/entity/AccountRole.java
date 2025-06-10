//package sep490.com.example.hrms_backend.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "account_role")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class AccountRole {
//
//    // 🧩 ====== KHÓA CHÍNH (TỰ TẠO ID) ======
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "account_role_id")
//    private Long accountRoleId;
//
//    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======
//
//    // Tài khoản được gán vai trò phụ
//    @ManyToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account;
//
//    // Vai trò phụ được gán cho tài khoản
//    @ManyToOne
//    @JoinColumn(name = "role_id", nullable = false)
//    private Role role;
//}
