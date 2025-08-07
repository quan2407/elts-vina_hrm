package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.entity.Department;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;
    private String name;
    public static DepartmentDTO fromEntity(Department d) {
        if (d == null) return null;
        return new DepartmentDTO(d.getDepartmentId(), d.getDepartmentName());
    }
}
