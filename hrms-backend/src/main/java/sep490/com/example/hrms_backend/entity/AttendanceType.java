package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "attendance_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceType {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_type_id")
    private Long id;

    @NotBlank
    @Column(name = "attendance_type_name", nullable = false)
    private String name; // tên loại công (ví dụ: Công thường, Nghỉ phép, OT, v.v.)

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "attendance_ratio")
    private Double ratio; // hệ số tính công (ví dụ: 1.0, 0.5)
}
