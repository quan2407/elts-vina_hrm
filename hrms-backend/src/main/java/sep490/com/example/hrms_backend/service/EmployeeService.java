package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.EmployeeDetailDTO;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO dto);
    EmployeeDetailDTO getEmployeeDetailById(Long id);

}
