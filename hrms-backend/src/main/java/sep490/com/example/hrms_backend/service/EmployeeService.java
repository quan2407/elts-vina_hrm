package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> getAllEmployees();
}
