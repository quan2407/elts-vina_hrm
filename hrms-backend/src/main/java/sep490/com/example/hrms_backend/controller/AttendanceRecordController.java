
package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
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

}
