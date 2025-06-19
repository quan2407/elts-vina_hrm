package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;
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
    public static Employee mapToEmployee(EmployeeRequestDTO dto) {
        return Employee.builder()
                .employeeCode(dto.getEmployeeCode())
                .employeeName(dto.getEmployeeName())
                .gender(dto.getGender().name())
                .dob(dto.getDob())
                .placeOfBirth(dto.getPlaceOfBirth())
                .originPlace(dto.getOriginPlace())
                .nationality(dto.getNationality())
                .citizenId(dto.getCitizenId())
                .citizenIssueDate(dto.getCitizenIssueDate())
                .citizenExpiryDate(dto.getCitizenExpiryDate())
//                .citizenIssuePlace(dto.getCitizenIssuePlace())
                .address(dto.getAddress())
                .image(dto.getImage())
                .startWorkAt(dto.getStartWorkAt())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .build();
    }
    public static void updateEmployeeFromUpdateDTO(EmployeeUpdateDTO dto, Employee employee) {
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setGender(dto.getGender().name());
        employee.setDob(dto.getDob());
        employee.setPlaceOfBirth(dto.getPlaceOfBirth());
        employee.setOriginPlace(dto.getOriginPlace());
        employee.setNationality(dto.getNationality());
        employee.setCitizenId(dto.getCitizenId());
        employee.setCitizenIssueDate(dto.getCitizenIssueDate());
        employee.setCitizenExpiryDate(dto.getCitizenExpiryDate());
//        employee.setCitizenIssuePlace(dto.getCitizenIssuePlace());
        employee.setAddress(dto.getAddress());
        employee.setImage(dto.getImage());
        employee.setStartWorkAt(dto.getStartWorkAt());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
    }


}
