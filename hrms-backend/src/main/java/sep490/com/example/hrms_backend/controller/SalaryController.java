package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.service.SalaryService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;
import sep490.com.example.hrms_backend.utils.SalaryExcelExport;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final CurrentUserUtils currentUserUtils;

    @PostMapping
    public ResponseEntity<String> generateSalary(
            @RequestParam int month,
            @RequestParam int year
    ) {
        salaryService.generateMonthlySalaries(month, year);
        return ResponseEntity.ok("Tạo bảng lương thành công cho " + month + "/" + year);
    }

    @GetMapping
    public ResponseEntity<Page<SalaryDTO>> getSalaries(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search // NEW
    ) {
        Page<SalaryDTO> salaries = salaryService.getSalariesByMonth(month, year, page, size, search);
        return ResponseEntity.ok(salaries);
    }

    @PutMapping("/regenerate")
    public ResponseEntity<String> regenerateSalary(
            @RequestParam int month,
            @RequestParam int year
    ) {
        salaryService.regenerateMonthlySalaries(month, year);
        return ResponseEntity.ok("Cập nhật bảng lương thành công cho " + month + "/" + year);
    }
    @GetMapping("/available-months")
    public ResponseEntity<List<String>> getAvailableSalaryMonths() {
        List<LocalDate> salaryDates = salaryService.getAvailableSalaryMonths();
        List<String> formatted = salaryDates.stream()
                .map(d -> String.format("%02d-%d", d.getMonthValue(), d.getYear()))
                .toList();

        return ResponseEntity.ok(formatted);
    }

    @GetMapping("/employee-months")
    public ResponseEntity<List<SalaryDTO>> getEmpSalaryMonths(
            @RequestParam int month,
            @RequestParam int year
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();
        List<SalaryDTO> salaries = salaryService.getEmpSalariesByMonth(employeeId, month, year);
        return ResponseEntity.ok(salaries);
    }
    @PutMapping("/lock")
    public ResponseEntity<String> lockSalaries(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam boolean locked
    ) {
        salaryService.lockSalariesByMonth(month, year, locked);
        String msg = locked ? "đã chốt lương" : "đã bỏ chốt lương";
        return ResponseEntity.ok("Tháng " + month + "/" + year + " " + msg);
    }
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportSalaryToExcel(
            @RequestParam int month,
            @RequestParam int year
    ) {
        List<SalaryDTO> salaries = salaryService.getSalariesByMonth(month, year);
        ByteArrayInputStream excelFile = SalaryExcelExport.exportSalariesToExcel(salaries, month, year);

        ByteArrayResource resource = new ByteArrayResource(excelFile.readAllBytes());

        String filename = String.format("bao_cao_luong_%02d_%d.xlsx", month, year);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
