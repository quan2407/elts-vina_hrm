package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @NotBlank
    @Column(name = "position_name", nullable = false, unique = true)
    private String positionName; // Tên chức vụ

    @Column(name = "description")
    private String description; // Mô tả chức vụ

    // Một chức vụ có thể gán cho nhiều nhân viên
    @OneToMany(mappedBy = "position")
    private List<Employee> employees;

    // Nhiều chức vụ thuộc nhiều phòng ban
    @ManyToMany
    @JoinTable(
            name = "department_position",
            joinColumns = @JoinColumn(name = "position_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> departments;
}
