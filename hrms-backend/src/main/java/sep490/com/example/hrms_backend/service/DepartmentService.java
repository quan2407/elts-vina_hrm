package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.PositionDTO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> getAllDepartments();
    List<PositionDTO> getPositionsByDepartment(Long departmentId);
    List<LineDTO> getLinesByDepartment(Long departmentId);
}
