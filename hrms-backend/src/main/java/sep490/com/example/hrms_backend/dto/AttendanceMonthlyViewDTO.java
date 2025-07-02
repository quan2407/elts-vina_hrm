package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceMonthlyViewDTO {
    private String employeeCode;
    private String employeeName;
    private String departmentName;
    private String positionName;
    private String lineName;
    private Map<String, AttendanceCellDTO> attendanceByDate; // 1 day key equal a attendance cell
    private Float totalHours;
    private Float totalDayShiftHours;
    private Float totalOvertimeHours;
    private Float totalWeekendHours;
    private Float totalHolidayHours;
}