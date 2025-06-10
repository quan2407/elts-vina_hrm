package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "`lines`")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Line {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    @NotBlank
    @Column(name = "line_name", nullable = false)
    private String lineName; // tên đội nhóm (ví dụ: Line A, Line B)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Line thuộc về một phòng ban
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Một line có thể có một trưởng nhóm (employee)
    @OneToOne
    @JoinColumn(name = "leader_id")
    private Employee leader;

    // Một line có nhiều nhân viên
    @OneToMany(mappedBy = "line")
    private List<Employee> employees;
}
