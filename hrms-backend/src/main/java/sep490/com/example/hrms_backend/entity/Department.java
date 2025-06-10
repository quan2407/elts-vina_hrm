package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "department")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    @NotBlank
    @Column(name = "department_name", nullable = false, unique = true)
    private String departmentName; // tên phòng ban (ví dụ: Kế toán, IT, Nhân sự)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một phòng ban có thể có nhiều line
    @OneToMany(mappedBy = "department")
    private List<Line> lines;

    // Một phòng ban có thể có nhiều nhân viên
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    // Một phòng ban có thể có nhiều đợt tuyển dụng
    @OneToMany(mappedBy = "department")
    private List<Recruitment> recruitments;
}
