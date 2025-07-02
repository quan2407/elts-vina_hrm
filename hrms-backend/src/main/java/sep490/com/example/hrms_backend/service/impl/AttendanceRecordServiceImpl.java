package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.AttendanceCellDTO;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.enums.LeaveCode;
import sep490.com.example.hrms_backend.repository.AttendanceRecordRepository;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public List<AttendanceMonthlyViewDTO> getMonthlyAttendance(int month, int year) {
        List<AttendanceRecord> records = attendanceRecordRepository.findByMonthAndYear(month, year);
        System.out.println("Found records: " + records.size());
        Map<Long, AttendanceMonthlyViewDTO> resultMap = new LinkedHashMap<>();

        for (AttendanceRecord record : records) {
            Employee emp = record.getEmployee();
            Long empId = emp.getEmployeeId();

            AttendanceMonthlyViewDTO dto = resultMap.computeIfAbsent(empId, id -> AttendanceMonthlyViewDTO.builder()
                    .employeeCode(emp.getEmployeeCode())
                    .employeeName(emp.getEmployeeName())
                    .departmentName(emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : null)
                    .positionName(emp.getPosition() != null ? emp.getPosition().getPositionName() : null)
                    .lineName(emp.getLine() != null ? emp.getLine().getLineName() : null)
                    .attendanceByDate(new LinkedHashMap<>())
                    .totalDayShiftHours(0f)
                    .totalOvertimeHours(0f)
                    .totalWeekendHours(0f)
                    .totalHolidayHours(0f)
                    .totalHours(0f)
                    .build()
            );

            String dateKey = String.valueOf(record.getDate().getDayOfMonth());

            // Tạo AttendanceCellDTO
            AttendanceCellDTO cell = AttendanceCellDTO.builder()
                    .shift(record.getDayShift())
                    .overtime(record.getOtShift())
                    .weekend(record.getWeekendShift())
                    .holiday(record.getHolidayShift())
                    .build();

            dto.getAttendanceByDate().put(dateKey, cell);

            // Tính riêng từng loại công
            float dayShift = parseHour(record.getDayShift());
            float ot = parseHour(record.getOtShift());
            float weekend = parseHour(record.getWeekendShift());
            float holiday = parseHour(record.getHolidayShift());

            dto.setTotalDayShiftHours(dto.getTotalDayShiftHours() + dayShift);
            dto.setTotalOvertimeHours(dto.getTotalOvertimeHours() + ot);
            dto.setTotalWeekendHours(dto.getTotalWeekendHours() + weekend);
            dto.setTotalHolidayHours(dto.getTotalHolidayHours() + holiday);

            dto.setTotalHours(dto.getTotalHours() + dayShift + ot + weekend + holiday);
        }

        return new ArrayList<>(resultMap.values());
    }

    private float parseHour(String value) {
        if (value == null || value.isBlank()) return 0f;

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            try {
                LeaveCode code = LeaveCode.valueOf(value);
                return switch (code) {
                    case NL, VR, KL1, P -> 8f;
                    case KL1_2 -> 2f;
                    case KL1_4, P_4, NDB_4 -> 4f;
                    case KL1_2_4 -> 2.4f;
                    case NDB -> 5f;
                    case NDB_1_5 -> 1.5f;
                    case VPHĐ, KL, NTS -> 0f;
                };
            } catch (IllegalArgumentException ex) {
                return 0f;
            }
        }
    }
}
