package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "attendance_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_type_id")
    private Long id;

    @NotBlank
    @Column(name = "attendance_type_name", nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "attendance_ratio")
    private Double ratio;
}
