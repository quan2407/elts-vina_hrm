package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.*;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO dto);
    EmployeeDetailDTO getEmployeeDetailById(Long id);

    EmployeeDetailDTO getOwnProfile();
    EmployeeDetailDTO updateOwnProfile(EmployeeOwnProfileUpdateDTO dto);
    void softDeleteEmployee(Long id);
    ByteArrayInputStream exportEmployeesToExcel();
    List<EmployeeResponseDTO> getEmployeeByDepartmentId(Long id);


    List<EmployeeResponseDTO> getEmployeesNotInLine(Long lineId, String search);
    List<EmployeeResponseDTO> getEmployeeByLineId(Long id);

    void addEmployeesToLine(Long lineId, List<Long> employeeIds);
    String getNextEmployeeCodeByPosition(Long positionId);

    String getNextEmployeeCode();
}
