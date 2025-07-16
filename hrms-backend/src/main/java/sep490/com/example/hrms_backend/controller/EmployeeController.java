package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.service.EmployeeService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(
            @Valid @ModelAttribute EmployeeRequestDTO dto,
            @RequestParam(value = "frontImageFile", required = false) MultipartFile frontImageFile,
            @RequestParam(value = "backImageFile", required = false) MultipartFile backImageFile) {

        try {
            if (frontImageFile != null && !frontImageFile.isEmpty()) {
                String frontPath = saveFile(frontImageFile, "front");
                dto.setCccdFrontImage(frontPath);
            }

            if (backImageFile != null && !backImageFile.isEmpty()) {
                String backPath = saveFile(backImageFile, "back");
                dto.setCccdBackImage(backPath);
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        EmployeeResponseDTO createdEmployee = employeeService.createEmployee(dto);
        return ResponseEntity.ok(createdEmployee);
    }
    private String saveFile(MultipartFile file, String prefix) throws IOException {
        String folder = "uploads/cccd/";
        String filename = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(folder + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return filename;
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute EmployeeUpdateDTO dto,
            @RequestParam(value = "frontImageFile", required = false) MultipartFile frontImageFile,
            @RequestParam(value = "backImageFile", required = false) MultipartFile backImageFile) {

        try {
            if (frontImageFile != null && !frontImageFile.isEmpty()) {
                String frontPath = saveFile(frontImageFile, "front");
                dto.setCccdFrontImage(frontPath);
            }

            if (backImageFile != null && !backImageFile.isEmpty()) {
                String backPath = saveFile(backImageFile, "back");
                dto.setCccdBackImage(backPath);
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

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


    @GetMapping("/next-code")
    public ResponseEntity<String> getNextEmployeeCode() {
        String nextCode = employeeService.getNextEmployeeCode();
        return ResponseEntity.ok(nextCode);
    }
    @GetMapping("/next-code/{positionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<String> getNextEmployeeCodeByPosition(@PathVariable Long positionId) {
        String nextCode = employeeService.getNextEmployeeCodeByPosition(positionId);
        return ResponseEntity.ok(nextCode);
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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=danhsachnhanvien.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/department/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeByDepartmentId(@PathVariable Long id) {
        List<EmployeeResponseDTO> employeeDetailInDepartment = employeeService.getEmployeeByDepartmentId(id);
        return ResponseEntity.ok(employeeDetailInDepartment);
    }

    @GetMapping("/line/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'PMC')")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeByLineId(@PathVariable Long id) {
        List<EmployeeResponseDTO> employeeDetailInLine = employeeService.getEmployeeByLineId(id);
        return ResponseEntity.ok(employeeDetailInLine);
    }


    @GetMapping("/not-in-line/{lineId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesNotInLine(
            @PathVariable Long lineId,
            @RequestParam(required = false) String search) {
        List<EmployeeResponseDTO> employees = employeeService.getEmployeesNotInLine(lineId, search);
        return ResponseEntity.ok(employees);
    }


    @PutMapping("/add-to-line/{lineId}")
    public ResponseEntity<?> addEmployeesToLine(
            @PathVariable Long lineId,
            @RequestBody List<Long> employeeIds) {
        employeeService.addEmployeesToLine(lineId, employeeIds);
        return ResponseEntity.ok("Thêm thành công");
    }
}
