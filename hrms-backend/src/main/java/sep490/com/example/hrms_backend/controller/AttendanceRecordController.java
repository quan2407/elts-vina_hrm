
package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.LeaveCodeUpdateDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.util.List;

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
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(attendanceRecordService.getMonthlyAttendance(month, year, page, size));
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
}
