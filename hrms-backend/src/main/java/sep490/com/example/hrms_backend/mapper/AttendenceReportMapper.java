package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AttendenceReportMapper {
    public static AttendanceMonthlyViewDTO mapToAttendanceMonthlyViewDTO(AttendanceRecord attendanceRecord){
        return AttendanceMonthlyViewDTO.builder()
                .employeeCode(attendanceRecord.getEmployee().getEmployeeCode())
                .employeeName(attendanceRecord.getEmployee().getEmployeeName())
                .departmentName(attendanceRecord.getEmployee().getDepartment().getDepartmentName())
                .positionName(attendanceRecord.getEmployee().getPosition().getPositionName())
                .attendanceByDate(new LinkedHashMap<>())
                .totalDayShiftHours(0f)
                .totalOvertimeHours(0f)
                .totalWeekendHours(0f)
                .totalHolidayHours(0f)
                .totalHours(0f).build();
    }

    public static List<AttendanceMonthlyViewDTO> mapToAttendanceMonthlyViewDTOList(List<AttendanceRecord> list){

        List<AttendanceMonthlyViewDTO> newList = new ArrayList<>();
        for(AttendanceRecord a : list)
        {
            AttendanceMonthlyViewDTO aDTO = mapToAttendanceMonthlyViewDTO(a);
            newList.add(aDTO);

        }

        return newList;
    }
}
