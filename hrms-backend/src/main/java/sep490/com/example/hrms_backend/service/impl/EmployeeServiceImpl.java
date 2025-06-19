package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.EmployeeDetailDTO;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.exception.DuplicateEntryException;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.EmployeeMapper;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements sep490.com.example.hrms_backend.service.EmployeeService {

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
        checkDuplicateFieldsForCreate(dto);
        checkPositionLineRequirements(dto.getDepartmentId(), dto.getPositionId(), dto.getLineId());

        if (!positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Chức vụ không thuộc phòng ban đã chọn");
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        checkDuplicateFieldsForUpdate(dto, id, employee);
        checkPositionLineRequirements(dto.getDepartmentId(), dto.getPositionId(), dto.getLineId());

        if (!positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Chức vụ không thuộc phòng ban đã chọn");
        }

        EmployeeMapper.updateEmployeeFromUpdateDTO(dto, employee);
        employee.setDepartment(fetchDepartment(dto.getDepartmentId()));
        employee.setPosition(fetchPosition(dto.getPositionId()));
        employee.setLine(fetchLine(dto.getLineId()));

        employee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeResponseDTO(employee);
    }

    @Override
    public EmployeeDetailDTO getEmployeeDetailById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return EmployeeMapper.mapToEmployeeDetailDTO(employee);
    }

    private void checkDuplicateFieldsForCreate(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            throw new DuplicateEntryException("Mã nhân viên đã tồn tại trong hệ thống");
        }
        if (employeeRepository.existsByCitizenId(dto.getCitizenId())) {
            throw new DuplicateEntryException("Số CMND/CCCD đã tồn tại trong hệ thống");
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntryException("Email đã tồn tại trong hệ thống");
        }
    }

    private void checkDuplicateFieldsForUpdate(EmployeeUpdateDTO dto, Long id, Employee employee) {
        if (!safeEquals(dto.getCitizenId(), employee.getCitizenId())) {
            if (employeeRepository.existsByCitizenIdAndEmployeeIdNot(dto.getCitizenId(), id)) {
                throw new DuplicateEntryException("Số CMND/CCCD đã tồn tại trong hệ thống");
            }
        }
        if (!safeEquals(dto.getEmail(), employee.getEmail())) {
            if (employeeRepository.existsByEmailAndEmployeeIdNot(dto.getEmail(), id)) {
                throw new DuplicateEntryException("Email đã tồn tại trong hệ thống");
            }
        }
    }

    private void checkPositionLineRequirements(Long departmentId, Long positionId, Long lineId) {
        List<Position> positions = positionRepository.findByDepartments_DepartmentId(departmentId);
        if (!positions.isEmpty() && positionId == null) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Vui lòng chọn chức vụ cho nhân viên");
        }

        List<Line> lines = lineRepository.findByDepartment_DepartmentId(departmentId);
        if (!lines.isEmpty() && lineId == null) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Vui lòng chọn chuyền sản xuất cho nhân viên");
        }
    }

    private boolean safeEquals(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    private Department fetchDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    private Position fetchPosition(Long id) {
        if (id == null) return null;
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", id));
    }

    private Line fetchLine(Long id) {
        if (id == null) return null;
        return lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line", "id", id));
    }
}
