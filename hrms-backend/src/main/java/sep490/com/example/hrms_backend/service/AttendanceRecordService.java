package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.LeaveCodeUpdateDTO;
import sep490.com.example.hrms_backend.dto.MonthYearDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceRecordService {



    List<MonthYearDTO> getAvailableMonths();

    void updateCheckInOut(Long id, AttendanceCheckInOutDTO dto);

    void updateLeaveCode(Long id, LeaveCodeUpdateDTO dto);

    List<AttendanceMonthlyViewDTO> getEmpMonthlyAttendanceById(Long employeeId, int month, int year);
    void updateDailyAttendanceForDate(LocalDate date);

    void importAttendanceFromExcel(MultipartFile file, LocalDate date);

    List<AttendanceMonthlyViewDTO> getAttendanceForExport(int month, int year);

    Page<AttendanceMonthlyViewDTO> getMonthlyAttendance(int month, int year, int page, int size, String search, Long departmentId, Long positionId, Long lineId);

    Map<String, Object> checkScheduleCoverage(int month, int year);
}
