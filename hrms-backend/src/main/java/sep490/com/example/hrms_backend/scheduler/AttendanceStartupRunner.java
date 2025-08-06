package sep490.com.example.hrms_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AttendanceStartupRunner {

    private final AttendanceRecordService attendanceRecordService;

    @EventListener(ApplicationReadyEvent.class)
    public void generateAttendanceOnStartup() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        System.out.println("⚙️  Tự động tạo công cho ngày hôm qua khi khởi động: " + yesterday);
        attendanceRecordService.updateDailyAttendanceForDate(yesterday);
    }
}
