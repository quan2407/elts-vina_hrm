package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.DepartmentWorkScheduleViewDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailResponseDTO;
import sep490.com.example.hrms_backend.service.WorkScheduleDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/work-schedule-details")
@RequiredArgsConstructor
public class WorkScheduleDetailController {

    private final WorkScheduleDetailService workScheduleDetailService;
    @GetMapping("/view-by-month")
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC','PRODUCTION_MANAGER')")
    public ResponseEntity<List<DepartmentWorkScheduleViewDTO>> viewByMonth(
            @RequestParam int month,
            @RequestParam int year
    ) {
        List<DepartmentWorkScheduleViewDTO> result = workScheduleDetailService.getMonthlyWorkSchedule(month, year);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PMC')")
    public ResponseEntity<WorkScheduleDetailResponseDTO> create(
            @Valid @RequestBody WorkScheduleDetailCreateDTO dto
    ) {
        WorkScheduleDetailResponseDTO response = workScheduleDetailService.create(dto);
        return ResponseEntity.ok(response);
    }

}
