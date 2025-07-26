package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.service.HumanReportService;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/human-report")
@AllArgsConstructor
public class HumanReportController {
    @Autowired
    private HumanReportService humanReportService;

    @GetMapping("/full-emp")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'PRODUCTION_MANAGER')")
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmp(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        Map<String, List<AttendanceMonthlyViewDTO>> humanReport = humanReportService.getFullEmp(date);
        return humanReport;
    }

    @GetMapping("/absent")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'PRODUCTION_MANAGER')")
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsent(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        Map<String, List<AttendanceMonthlyViewDTO>> humanAbsent = humanReportService.getListEmpAbsent(date);
        return humanAbsent;
    }
    @GetMapping("/absentkl")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'PRODUCTION_MANAGER')")
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsentKL(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        Map<String, List<AttendanceMonthlyViewDTO>> humanAbsent = humanReportService.getListEmpAbsentKL(date);
        return humanAbsent;
    }
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<InputStreamResource> exportHumanReportToExcel(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ByteArrayInputStream in = humanReportService.exportHumanReportToExcel(date);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=baocaonhanluc.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

}
