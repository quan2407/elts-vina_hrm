package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}/positions")
    public ResponseEntity<List<PositionDTO>> getPositionsByDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getPositionsByDepartment(id));
    }

    @GetMapping("/{id}/lines")
    public ResponseEntity<List<LineDTO>> getLinesByDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getLinesByDepartment(id));
    }
}
