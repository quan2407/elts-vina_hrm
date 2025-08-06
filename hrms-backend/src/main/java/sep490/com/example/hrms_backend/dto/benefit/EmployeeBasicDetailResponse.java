package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.entity.Employee;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBasicDetailResponse {
    private Long employeeId;
    private String employeeName;
    private String email;
    private DepartmentDTO department;
    private PositionDTO position;


    public static EmployeeBasicDetailResponse fromEntity(Employee e) {
        DepartmentDTO departmentDTO = DepartmentDTO.fromEntity(e.getDepartment());
        PositionDTO positionDTO = PositionDTO.fromEntity(e.getPosition());
        return new EmployeeBasicDetailResponse(
                e.getEmployeeId(),
                e.getEmployeeName(),
                e.getEmail(),
                departmentDTO,
                positionDTO
        );
    }

}
