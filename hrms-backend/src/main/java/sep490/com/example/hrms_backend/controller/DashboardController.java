package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.service.EmployeeService;
import sep490.com.example.hrms_backend.service.RecruitmentService;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final RecruitmentService recruitmentService;
    private final EmployeeService employeeService;

    @GetMapping("/recruitment-graph")
    public ResponseEntity<?> getRecruitmentGraph(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        try {
            return ResponseEntity.ok(
                    recruitmentService.getGraphData(
                            fromDate.atStartOfDay(),
                            toDate.atTime(LocalTime.MAX)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/employee-gender-distribution")
    public ResponseEntity<?> getEmployeeGenderDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        try {
            return ResponseEntity.ok(employeeService.getGenderDistribution(fromDate, toDate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/employee-department-distribution")
    public ResponseEntity<?> getEmployeeDepartmentDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        try {
            return ResponseEntity.ok(employeeService.getDepartmentDistribution(fromDate, toDate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
