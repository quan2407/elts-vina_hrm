package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    @NotBlank
    @Column(name = "department_name", nullable = false, unique = true)
    private String departmentName;

    @OneToMany(mappedBy = "department")
    private List<Line> lines;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    @OneToMany(mappedBy = "department")
    private List<Recruitment> recruitments;

    @ManyToMany(mappedBy = "departments")
    private List<Position> positions;
    @OneToMany(mappedBy = "department")
    private List<WorkSchedule> workSchedules;
}
