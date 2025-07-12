package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.LeaveCodeUpdateDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;

import java.util.List;

public interface AttendanceRecordService {
    List<AttendanceMonthlyViewDTO> getMonthlyAttendance(int month, int year);

    List<MonthYearDTO> getAvailableMonths();

    void updateCheckInOut(Long id, AttendanceCheckInOutDTO dto);

    void updateLeaveCode(Long id, LeaveCodeUpdateDTO dto);

    List<AttendanceMonthlyViewDTO> getEmpMonthlyAttendanceById(Long employeeId, int month, int year);
}
