package sep490.com.example.hrms_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceRecordService attendanceRecordService;

    // Chạy lúc 2h sáng mỗi ngày
    @Scheduled(cron = "0 0 2 * * *")
    public void updateYesterdayAttendance() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        attendanceRecordService.updateDailyAttendanceForDate(yesterday);
    }
}