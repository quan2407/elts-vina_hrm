package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.service.SalaryService;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * ✅ Tạo bảng lương cho một tháng/năm cụ thể
     * POST /api/salaries?month=6&year=2025
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> generateSalary(
            @RequestParam int month,
            @RequestParam int year
    ) {
        salaryService.generateMonthlySalaries(month, year);
        return ResponseEntity.ok("Tạo bảng lương thành công cho " + month + "/" + year);
    }

    /**
     * ✅ Lấy danh sách bảng lương theo tháng/năm
     * GET /api/salaries?month=6&year=2025
     */
    @GetMapping
    public ResponseEntity<List<SalaryDTO>> getSalaries(
            @RequestParam int month,
            @RequestParam int year
    ) {
        List<SalaryDTO> salaries = salaryService.getSalariesByMonth(month, year);
        return ResponseEntity.ok(salaries);
    }
}
