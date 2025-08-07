package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.entity.Employee;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBasicDetailDTO {
    private Long employeeId;
    private String employeeName;
    private String email;
    private BigDecimal basicSalary;
    private DepartmentDTO department;
    private PositionDTO position;

    public static EmployeeBasicDetailDTO fromEntity(Employee e) {
        DepartmentDTO departmentDTO = DepartmentDTO.fromEntity(e.getDepartment());
        PositionDTO positionDTO = PositionDTO.fromEntity(e.getPosition());
        return new EmployeeBasicDetailDTO(
                e.getEmployeeId(),
                e.getEmployeeName(),
                e.getEmail(),
                e.getBasicSalary(),
                departmentDTO,
                positionDTO
        );
    }
}
