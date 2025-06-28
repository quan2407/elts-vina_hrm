package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;
import sep490.com.example.hrms_backend.service.WorkScheduleService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC')")
    public ResponseEntity<List<WorkScheduleResponseDTO>> createWorkSchedules(@Valid @RequestBody WorkScheduleCreateDTO dto) {
        List<WorkScheduleResponseDTO> createdList = workScheduleService.createWorkSchedulesForAll(dto);
        return ResponseEntity.ok(createdList);
    }
    @GetMapping("/available")
    public ResponseEntity<List<com.example.hrms_backend.dto.WorkScheduleMonthDTO>> getAvailableMonths() {
        return ResponseEntity.ok(workScheduleService.getAvailableMonths());
    }
    @GetMapping("/resolve-id")
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC')")
    public ResponseEntity<Long> resolveWorkScheduleId(
            @RequestParam Long departmentId,
            @RequestParam(required = false) Long lineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateWork
    ) {
        Long id = workScheduleService.resolveWorkScheduleId(departmentId, lineId, dateWork);
        return ResponseEntity.ok(id);
    }

}
