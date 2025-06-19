package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.service.DepartmentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final LineRepository lineRepository;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(dept -> new DepartmentDTO(dept.getDepartmentId(), dept.getDepartmentName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> getPositionsByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));
        return department.getPositions()
                .stream()
                .map(pos -> new PositionDTO(pos.getPositionId(), pos.getPositionName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LineDTO> getLinesByDepartment(Long departmentId) {
        List<Line> lines = lineRepository.findByDepartmentDepartmentId(departmentId);
        return lines.stream()
                .map(line -> new LineDTO(line.getLineId(), line.getLineName()))
                .collect(Collectors.toList());
    }
}
