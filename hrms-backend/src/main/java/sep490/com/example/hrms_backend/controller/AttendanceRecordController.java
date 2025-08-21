
package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.LeaveCodeUpdateDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;
import sep490.com.example.hrms_backend.utils.AttendanceExcelExport;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordService attendanceRecordService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/view-by-month")
    public ResponseEntity<Page<AttendanceMonthlyViewDTO>> viewAttendanceByMonth(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false) Long lineId
    ) {
        return ResponseEntity.ok(
                attendanceRecordService.getMonthlyAttendance(
                        month, year, page, size, search, departmentId, positionId, lineId
                )
        );
    }




    @GetMapping("/employee")
    public ResponseEntity<List<AttendanceMonthlyViewDTO>> viewEmpAttendanceByMonthById(
            @RequestParam int month,
            @RequestParam int year
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();
        return ResponseEntity.ok(attendanceRecordService.getEmpMonthlyAttendanceById(employeeId, month, year));
    }

    @GetMapping("/available-months")
    public ResponseEntity<List<MonthYearDTO>> getAvailableMonths() {
        return ResponseEntity.ok(attendanceRecordService.getAvailableMonths());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCheckInOut(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceCheckInOutDTO dto
    ) {
        attendanceRecordService.updateCheckInOut(id, dto);
        return ResponseEntity.ok("Cập nhật giờ vào – giờ ra thành công");
    }

    @PutMapping("/{id}/leave-code")
    public ResponseEntity<String> updateLeaveCode(
            @PathVariable Long id,
            @RequestBody LeaveCodeUpdateDTO dto
    ) {
        attendanceRecordService.updateLeaveCode(id, dto);
        return ResponseEntity.ok("Cập nhật mã nghỉ thành công");
    }
    @PostMapping("/import")
    public ResponseEntity<String> importAttendance(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        attendanceRecordService.importAttendanceFromExcel(file, date);
        return ResponseEntity.ok("Import thành công");
    }
    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportAttendanceToExcel(@RequestBody MonthYearDTO monthYearDTO) {
        int month = monthYearDTO.getMonth();
        int year = monthYearDTO.getYear();

        List<AttendanceMonthlyViewDTO> attendanceData =
                attendanceRecordService.getAttendanceForExport(month, year);

        ByteArrayInputStream excelFile = AttendanceExcelExport.exportAttendanceToExcel(
                attendanceData, month, year
        );

        ByteArrayResource resource = new ByteArrayResource(excelFile.readAllBytes());

        String fileName = String.format("baocao_chamcong_thang_%02d_%d.xlsx", month, year);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/check-schedule-coverage")
    public ResponseEntity<Map<String, Object>> checkScheduleCoverage(
            @RequestParam int month,
            @RequestParam int year
    ) {
        Map<String, Object> result = attendanceRecordService.checkScheduleCoverage(month, year);
        return ResponseEntity.ok(result);
    }

}
