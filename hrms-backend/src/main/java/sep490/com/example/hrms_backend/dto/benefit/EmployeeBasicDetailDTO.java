package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBasicDetailDTO {
    private Long employeeId;
    private String employeeName;
    private String email;
    private DepartmentDTO department;
}
