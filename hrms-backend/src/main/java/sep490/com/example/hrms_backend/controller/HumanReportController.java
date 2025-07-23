package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.service.HumanReportService;

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
    @PreAuthorize("hasAnyRole('HR', 'PRODUCTION_MANAGER')")
    public Map<String, List<EmployeeResponseDTO>> getListEmp(){

        Map<String, List<EmployeeResponseDTO>> humanReport = humanReportService.getFullEmp();
        return humanReport;
    }

    @GetMapping("/absent")
    @PreAuthorize("hasAnyRole('HR', 'PRODUCTION_MANAGER')")
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsent(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        Map<String, List<AttendanceMonthlyViewDTO>> humanAbsent = humanReportService.getListEmpAbsent(date);
        return humanAbsent;
    }

    @GetMapping("/absentkl")
    @PreAuthorize("hasAnyRole('HR', 'PRODUCTION_MANAGER')")
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsentKL(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        Map<String, List<AttendanceMonthlyViewDTO>> humanAbsent = humanReportService.getListEmpAbsentKL(date);
        return humanAbsent;
    }

}
