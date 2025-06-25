package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.service.EmployeeService;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<EmployeeResponseDTO> employeeList = employeeService.getAllEmployees();
        return ResponseEntity.ok(employeeList);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO createdEmployee = employeeService.createEmployee(dto);
        return ResponseEntity.ok(createdEmployee);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDTO dto) {
        EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(updatedEmployee);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDetailDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDetailDTO employeeDetail = employeeService.getEmployeeDetailById(id);
        return ResponseEntity.ok(employeeDetail);
    }
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE', 'LINE_LEADER', 'PMC', 'CANTEEN', 'PRODUCTION_MANAGER')")
    public ResponseEntity<EmployeeDetailDTO> getOwnProfile() {
        EmployeeDetailDTO employeeDetail = employeeService.getOwnProfile();
        return ResponseEntity.ok(employeeDetail);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE', 'LINE_LEADER', 'PMC', 'CANTEEN', 'PRODUCTION_MANAGER')")
    public ResponseEntity<EmployeeDetailDTO> updateOwnProfile(@Valid @RequestBody EmployeeOwnProfileUpdateDTO dto) {
        EmployeeDetailDTO updated = employeeService.updateOwnProfile(dto);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.softDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<InputStreamResource> exportEmployeesToExcel() {
        ByteArrayInputStream in = employeeService.exportEmployeesToExcel();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=employees.xlsx")
                .body(new InputStreamResource(in));
    }


}
