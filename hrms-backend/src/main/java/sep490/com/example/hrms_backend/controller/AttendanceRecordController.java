
package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.LeaveCodeUpdateDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;

import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordService attendanceRecordService;

    @GetMapping("/view-by-month")
    public ResponseEntity<List<AttendanceMonthlyViewDTO>> viewAttendanceByMonth(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(attendanceRecordService.getMonthlyAttendance(month, year));
    }
    @GetMapping("/available-months")
    public ResponseEntity<List<MonthYearDTO>> getAvailableMonths() {
        List<MonthYearDTO> result = attendanceRecordService.getAvailableMonths();
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<?> updateCheckInOut(
            @PathVariable Long id,
            @RequestBody AttendanceCheckInOutDTO dto
    ) {
        attendanceRecordService.updateCheckInOut(id, dto);
        return ResponseEntity.ok("Updated successfully");
    }
    @PutMapping("/{id}/leave-code")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Void> updateLeaveCode(
            @PathVariable Long id,
            @RequestBody LeaveCodeUpdateDTO dto) {
        attendanceRecordService.updateLeaveCode(id, dto);
        return ResponseEntity.ok().build();
    }

}
