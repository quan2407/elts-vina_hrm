package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;

import java.util.List;

public interface AttendanceRecordService {
    List<AttendanceMonthlyViewDTO> getMonthlyAttendance(int month, int year);

    List<MonthYearDTO> getAvailableMonths();
}
