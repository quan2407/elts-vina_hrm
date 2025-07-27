package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.service.WorkScheduleService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;
    private final CurrentUserUtils currentUserUtils;
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC')")
    public ResponseEntity<List<WorkScheduleResponseDTO>> createWorkSchedules(@Valid @RequestBody WorkScheduleCreateDTO dto) {
        List<WorkScheduleResponseDTO> createdList = workScheduleService.createWorkSchedulesForAll(dto);
        return ResponseEntity.ok(createdList);
    }
    @GetMapping("/available")
    public ResponseEntity<List<WorkScheduleMonthDTO>> getAvailableMonths() {
        return ResponseEntity.ok(workScheduleService.getAvailableMonths());
    }
    @GetMapping("/resolve-id")
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC','PRODUCTION_MANAGER')")
    public ResponseEntity<Long> resolveWorkScheduleId(
            @RequestParam Long departmentId,
            @RequestParam(required = false) Long lineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateWork
    ) {
        Long id = workScheduleService.resolveWorkScheduleId(departmentId, lineId, dateWork);
        return ResponseEntity.ok(id);
    }
    @PutMapping("/submit")
    @PreAuthorize("hasRole('PMC')")
    public ResponseEntity<?> submitWorkSchedules(@RequestParam int month, @RequestParam int year) {
        workScheduleService.submitAllWorkSchedules(month, year);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/accept")
    @PreAuthorize("hasRole('PRODUCTION_MANAGER')")
    public ResponseEntity<?> acceptWorkSchedules(
            @RequestParam int month,
            @RequestParam int year) {
        workScheduleService.acceptAllSubmittedSchedules(month, year);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/employee-view")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<EmployeeWorkScheduleDTO>> getEmployeeWorkSchedule(
            @RequestParam int month,
            @RequestParam int year
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();
        List<EmployeeWorkScheduleDTO> result = workScheduleService.getWorkScheduleForEmployee(employeeId, month, year);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/reject")
    @PreAuthorize("hasRole('PRODUCTION_MANAGER')")
    public ResponseEntity<?> rejectWorkSchedules(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam String reason
    ) {
        workScheduleService.rejectSubmittedSchedule(month, year, reason);
        return ResponseEntity.ok("Từ chối bảng phân ca thành công.");
    }
    @PutMapping("/custom-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC')")
    public ResponseEntity<?> createCustomRangeWorkSchedule(@Valid @RequestBody WorkScheduleRangeDTO dto) {
        workScheduleService.createCustomWorkSchedules(dto);
        return ResponseEntity.ok("Đã dải lịch thành công theo yêu cầu.");
    }


}
