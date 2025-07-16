package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.LeaveCodeUpdateDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;

import java.util.List;

public interface AttendanceRecordService {
    Page<AttendanceMonthlyViewDTO> getMonthlyAttendance(int month, int year, int page, int size);


    List<MonthYearDTO> getAvailableMonths();

    void updateCheckInOut(Long id, AttendanceCheckInOutDTO dto);

    void updateLeaveCode(Long id, LeaveCodeUpdateDTO dto);

    List<AttendanceMonthlyViewDTO> getEmpMonthlyAttendanceById(Long employeeId, int month, int year);
}
