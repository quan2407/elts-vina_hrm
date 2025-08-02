package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.service.WorkScheduleDetailService;
import sep490.com.example.hrms_backend.utils.WorkScheduleExcelExport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedule-details")
@RequiredArgsConstructor
public class WorkScheduleDetailController {

    private final WorkScheduleDetailService workScheduleDetailService;
    @GetMapping("/view-by-month")
    public ResponseEntity<List<DepartmentWorkScheduleViewDTO>> viewByMonth(
            @RequestParam int month,
            @RequestParam int year
    ) {
        List<DepartmentWorkScheduleViewDTO> result = workScheduleDetailService.getMonthlyWorkSchedule(month, year);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<WorkScheduleDetailResponseDTO> create(
            @Valid @RequestBody WorkScheduleDetailCreateDTO dto
    ) {
        WorkScheduleDetailResponseDTO response = workScheduleDetailService.create(dto);
        return ResponseEntity.ok(response);
    }
    @PutMapping
    public ResponseEntity<WorkScheduleDetailResponseDTO> update(
            @Valid @RequestBody WorkScheduleDetailUpdateDTO dto
    ) {
        WorkScheduleDetailResponseDTO response = workScheduleDetailService.update(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workScheduleDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/export-work-schedule")
    public ResponseEntity<ByteArrayResource> exportWorkSchedule(@RequestBody MonthYearDTO dto) throws IOException {
        int month = dto.getMonth();
        int year = dto.getYear();

        List<DepartmentWorkScheduleViewDTO> data = workScheduleDetailService.getMonthlyWorkSchedule(month, year);

        ByteArrayInputStream excelFile = WorkScheduleExcelExport.exportToExcel(data, month, year);
        ByteArrayResource resource = new ByteArrayResource(excelFile.readAllBytes());

        String filename = String.format("kehoach_lichsanxuat_thang_%02d_%d.xlsx", month, year);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
