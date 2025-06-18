package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.mapper.EmployeeMapper;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;
import sep490.com.example.hrms_backend.service.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LineRepository lineRepository;
    private final PositionRepository positionRepository;

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper::mapToEmployeeResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        Employee employee = EmployeeMapper.mapToEmployee(dto);
        employee.setDepartment(fetchDepartment(dto.getDepartmentId()));
        employee.setPosition(fetchPosition(dto.getPositionId()));
        employee.setLine(fetchLine(dto.getLineId()));
        employee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeResponseDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
        EmployeeMapper.updateEmployeeFromUpdateDTO(dto, employee);
        employee.setDepartment(fetchDepartment(dto.getDepartmentId()));
        employee.setPosition(fetchPosition(dto.getPositionId()));
        employee.setLine(fetchLine(dto.getLineId()));
        employee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeResponseDTO(employee);
    }


    private Department fetchDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
    }

    private Position fetchPosition(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found with ID: " + id));
    }

    private Line fetchLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Line not found with ID: " + id));
    }


}
