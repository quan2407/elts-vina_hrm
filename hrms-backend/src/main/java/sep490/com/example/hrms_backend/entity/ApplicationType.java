package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "application_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationType {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_type_id")
    private Long id;

    @NotBlank
    @Column(name = "application_type_name", nullable = false)
    private String name; // tên loại đơn (VD: Nghỉ phép, OT, công tác...)

    @Column(name = "description")
    private String description; // mô tả chi tiết (nếu cần)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một loại đơn có thể có nhiều đơn được gửi thuộc loại đó
    @OneToMany(mappedBy = "applicationType")
    private List<Application> applications;
}
