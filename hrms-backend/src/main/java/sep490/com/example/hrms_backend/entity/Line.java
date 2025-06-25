package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "`lines`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Line {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    @NotBlank
    @Column(name = "line_name", nullable = false)
    private String lineName;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private Employee leader;

    @OneToMany(mappedBy = "line")
    private List<Employee> employees;

    @OneToMany(mappedBy = "line")
    private List<WorkScheduleDetail> workScheduleDetails;

}
