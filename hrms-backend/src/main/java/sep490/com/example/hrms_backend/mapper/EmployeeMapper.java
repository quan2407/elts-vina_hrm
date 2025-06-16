package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.entity.Employee;

public class EmployeeMapper {

    public static EmployeeResponseDTO mapToEmployeeResponseDTO(Employee employee) {
        return EmployeeResponseDTO.builder()
                .employeeId(employee.getEmployeeId())
                .employeeCode(employee.getEmployeeCode())
                .employeeName(employee.getEmployeeName())
                .gender(employee.getGender())
                .dob(employee.getDob())
                .placeOfBirth(employee.getPlaceOfBirth())
                .image(employee.getImage())
                .nationality(employee.getNationality())
                .address(employee.getAddress())
                .startWorkAt(employee.getStartWorkAt())
                .phoneNumber(employee.getPhoneNumber())
                .citizenId(employee.getCitizenId())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
                .lineName(employee.getLine() != null ? employee.getLine().getLineName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .accountUsername(employee.getAccount() != null ? employee.getAccount().getUsername() : null)
                .build();
    }
}
