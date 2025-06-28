package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;
import sep490.com.example.hrms_backend.service.WorkScheduleService;

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

}
