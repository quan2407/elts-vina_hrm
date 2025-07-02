package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.EmployeeDetailDTO;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Position;

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
                .address(dto.getAddress())
                .currentAddress(dto.getCurrentAddress())
                .ethnicity(dto.getEthnicity())
                .religion(dto.getReligion())
                .educationLevel(dto.getEducationLevel())
                .specializedLevel(dto.getSpecializedLevel())
                .trainingType(dto.getTrainingType())
                .trainingMajor(dto.getTrainingMajor())
                .foreignLanguages(dto.getForeignLanguages())
                .image(dto.getImage())
                .startWorkAt(dto.getStartWorkAt())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .isDeleted(false)
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
        employee.setAddress(dto.getAddress());
        employee.setCurrentAddress(dto.getCurrentAddress());
        employee.setEthnicity(dto.getEthnicity());
        employee.setReligion(dto.getReligion());
        employee.setEducationLevel(dto.getEducationLevel());
        employee.setSpecializedLevel(dto.getSpecializedLevel());
        employee.setForeignLanguages(dto.getForeignLanguages());
        employee.setTrainingType(dto.getTrainingType());
        employee.setTrainingMajor(dto.getTrainingMajor());
        employee.setImage(dto.getImage());
        employee.setStartWorkAt(dto.getStartWorkAt());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());

        // Gán phòng ban và vị trí nếu cần thiết
        if (employee.getDepartment() == null ||
                !employee.getDepartment().getDepartmentId().equals(dto.getDepartmentId())) {
            Department department = new Department();
            department.setDepartmentId(dto.getDepartmentId());
            employee.setDepartment(department);
        }

        if (employee.getPosition() == null ||
                !employee.getPosition().getPositionId().equals(dto.getPositionId())) {
            Position position = new Position();
            position.setPositionId(dto.getPositionId());
            employee.setPosition(position);
        }
    }

    public static EmployeeDetailDTO mapToEmployeeDetailDTO(Employee employee) {
        return EmployeeDetailDTO.builder()
                .employeeId(employee.getEmployeeId())
                .employeeCode(employee.getEmployeeCode())
                .employeeName(employee.getEmployeeName())
                .gender(employee.getGender())
                .dob(employee.getDob())
                .placeOfBirth(employee.getPlaceOfBirth())
                .originPlace(employee.getOriginPlace())
                .nationality(employee.getNationality())
                .citizenId(employee.getCitizenId())
                .citizenIssueDate(employee.getCitizenIssueDate())
                .citizenExpiryDate(employee.getCitizenExpiryDate())
                .address(employee.getAddress())
                .currentAddress(employee.getCurrentAddress())
                .ethnicity(employee.getEthnicity())
                .religion(employee.getReligion())
                .educationLevel(employee.getEducationLevel())
                .specializedLevel(employee.getSpecializedLevel())
                .trainingType(employee.getTrainingType())
                .trainingMajor(employee.getTrainingMajor())
                .foreignLanguages(employee.getForeignLanguages())
                .startWorkAt(employee.getStartWorkAt())
                .phoneNumber(employee.getPhoneNumber())
                .email(employee.getEmail())

                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)

                .positionId(employee.getPosition() != null ? employee.getPosition().getPositionId() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)

                .lineId(employee.getLine() != null ? employee.getLine().getLineId() : null)
                .lineName(employee.getLine() != null ? employee.getLine().getLineName() : null)

                .build();
    }



}
