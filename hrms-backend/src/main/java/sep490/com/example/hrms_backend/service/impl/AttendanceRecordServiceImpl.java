package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.WorkSchedule;
import sep490.com.example.hrms_backend.entity.WorkScheduleDetail;
import sep490.com.example.hrms_backend.enums.LeaveCode;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceRecordServiceImpl implements AttendanceRecordService {
    private final WorkScheduleRepository workScheduleRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final HolidayRepository holidayRepository;
    private final WorkScheduleServiceImpl workScheduleService;
    private final WorkScheduleDetailRepository workScheduleDetailRepository;

    @Override
    public List<AttendanceMonthlyViewDTO> getEmpMonthlyAttendanceById(Long employeeId, int month, int year) {

        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        List<AttendanceRecord> records = attendanceRecordRepository.findByEmpIdAndMonthAndYear(employeeId, month, year);
        Map<Long, List<AttendanceRecord>> recordsByEmployee = new HashMap<>();

        recordsByEmployee.put(employeeId, records);

        AttendanceMonthlyViewDTO result = AttendanceMonthlyViewDTO.builder()
                .employeeCode(employee.getEmployeeCode())
                .employeeName(employee.getEmployeeName())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .lineName(employee.getLine() != null ? employee.getLine().getLineName() : null)
                .attendanceByDate(new LinkedHashMap<>())
                .totalDayShiftHours(0f)
                .totalOvertimeHours(0f)
                .totalWeekendHours(0f)
                .totalHolidayHours(0f)
                .totalHours(0f)
                .build();

        List<AttendanceRecord> empRecords = recordsByEmployee.getOrDefault(employee.getEmployeeId(), Collections.emptyList());

        for (AttendanceRecord record : empRecords) {
            String dateKey = String.valueOf(record.getDate().getDayOfMonth());

            boolean hasSchedule = false;
            boolean isWeekend = false;
            if (record.getWorkSchedule() != null && record.getWorkSchedule().getWorkScheduleDetails() != null) {
                Optional<WorkScheduleDetail> detailOpt = record.getWorkSchedule().getWorkScheduleDetails().stream()
                        .filter(detail -> detail.getDateWork().equals(record.getDate()))
                        .findFirst();
                if (detailOpt.isPresent()) {
                    hasSchedule = true;
                    isWeekend = detailOpt.get().getDateWork().getDayOfWeek().getValue() == 7; // Chủ nhật
                }
            }

            boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(record.getDate());

            AttendanceCellDTO cell = AttendanceCellDTO.builder()
                    .attendanceRecordId(record.getId())
                    .shift(record.getDayShift())
                    .overtime(record.getOtShift())
                    .weekend(record.getWeekendShift())
                    .holiday(record.getHolidayShift())
                    .hasScheduleDetail(hasSchedule)
                    .checkIn(record.getCheckInTime() != null ? record.getCheckInTime().toString() : null)
                    .checkOut(record.getCheckOutTime() != null ? record.getCheckOutTime().toString() : null)
                    .holidayFlag(isHoliday)
                    .weekendFlag(isWeekend)
                    .build();

            result.getAttendanceByDate().put(dateKey, cell);

            result.setTotalDayShiftHours(result.getTotalDayShiftHours() + parseHour(record.getDayShift()));
            result.setTotalOvertimeHours(result.getTotalOvertimeHours() + parseHour(record.getOtShift()));
            result.setTotalWeekendHours(result.getTotalWeekendHours() + parseHour(record.getWeekendShift()));
            result.setTotalHolidayHours(result.getTotalHolidayHours() + parseHour(record.getHolidayShift()));
        }

        result.setTotalHours(result.getTotalDayShiftHours()
                + result.getTotalOvertimeHours()
                + result.getTotalWeekendHours()
                + result.getTotalHolidayHours());

        return List.of(result);
    }

    @Override
    public Page<AttendanceMonthlyViewDTO> getMonthlyAttendance(int month, int year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAllActive(pageable);

        List<AttendanceRecord> records = attendanceRecordRepository.findByMonthAndYear(month, year);
        Map<Long, List<AttendanceRecord>> recordsByEmployee = records.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getEmployeeId()));

        List<AttendanceMonthlyViewDTO> dtoList = new ArrayList<>();

        for (Employee emp : employeePage.getContent()) {
            AttendanceMonthlyViewDTO dto = AttendanceMonthlyViewDTO.builder()
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
                    .build();

            List<AttendanceRecord> empRecords = recordsByEmployee.getOrDefault(emp.getEmployeeId(), Collections.emptyList());

            for (AttendanceRecord record : empRecords) {
                String dateKey = String.valueOf(record.getDate().getDayOfMonth());

                boolean hasSchedule = false;
                boolean isWeekend = false;
                if (record.getWorkSchedule() != null && record.getWorkSchedule().getWorkScheduleDetails() != null) {
                    Optional<WorkScheduleDetail> detailOpt = record.getWorkSchedule().getWorkScheduleDetails().stream()
                            .filter(detail -> detail.getDateWork().equals(record.getDate()))
                            .findFirst();
                    if (detailOpt.isPresent()) {
                        hasSchedule = true;
                        isWeekend = detailOpt.get().getDateWork().getDayOfWeek().getValue() == 7;
                    }
                }

                boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(record.getDate());

                AttendanceCellDTO cell = AttendanceCellDTO.builder()
                        .attendanceRecordId(record.getId())
                        .shift(record.getDayShift())
                        .overtime(record.getOtShift())
                        .weekend(record.getWeekendShift())
                        .holiday(record.getHolidayShift())
                        .hasScheduleDetail(hasSchedule)
                        .checkIn(record.getCheckInTime() != null ? record.getCheckInTime().toString() : null)
                        .checkOut(record.getCheckOutTime() != null ? record.getCheckOutTime().toString() : null)
                        .holidayFlag(isHoliday)
                        .weekendFlag(isWeekend)
                        .build();

                dto.getAttendanceByDate().put(dateKey, cell);

                dto.setTotalDayShiftHours(dto.getTotalDayShiftHours() + parseHour(record.getDayShift()));
                dto.setTotalOvertimeHours(dto.getTotalOvertimeHours() + parseHour(record.getOtShift()));
                dto.setTotalWeekendHours(dto.getTotalWeekendHours() + parseHour(record.getWeekendShift()));
                dto.setTotalHolidayHours(dto.getTotalHolidayHours() + parseHour(record.getHolidayShift()));
            }

            dto.setTotalHours(dto.getTotalDayShiftHours()
                    + dto.getTotalOvertimeHours()
                    + dto.getTotalWeekendHours()
                    + dto.getTotalHolidayHours());

            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, pageable, employeePage.getTotalElements());
    }


    private float parseHour(String value) {
        if (value == null || value.isBlank()) return 0f;

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            try {
                LeaveCode code = LeaveCode.valueOf(value);
                return switch (code) {
                    case P,NTS,CKH,KH,NT -> 8f;
                    case P_2 -> 4f;
                    case KL -> 0f;
                };
            } catch (IllegalArgumentException ex) {
                return 0f;
            }
        }
    }

    @Override
    public List<MonthYearDTO> getAvailableMonths() {
        List<Object[]> rawList = attendanceRecordRepository.findDistinctMonthYear();
        return rawList.stream()
                .map(row -> new MonthYearDTO((int) row[0], (int) row[1]))
                .collect(Collectors.toList());
    }

    @Override
    public void updateCheckInOut(Long id, AttendanceCheckInOutDTO dto) {
        AttendanceRecord record = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        if (dto.getCheckIn() != null && !dto.getCheckIn().isBlank()) {
            record.setCheckInTime(LocalTime.parse(dto.getCheckIn(), formatter));
        } else {
            record.setCheckInTime(null);
        }

        if (dto.getCheckOut() != null && !dto.getCheckOut().isBlank()) {
            record.setCheckOutTime(LocalTime.parse(dto.getCheckOut(), formatter));
        } else {
            record.setCheckOutTime(null);
        }

        // 👉 Tính công nếu có lịch làm và giờ vào/ra
        if (record.getWorkSchedule() != null
                && record.getWorkSchedule().getWorkScheduleDetails() != null
                && record.getCheckInTime() != null
                && record.getCheckOutTime() != null) {
            calculateShift(record);
        } else {
            // Xoá công nếu không đủ thông tin
            record.setDayShift(null);
            record.setOtShift(null);
            record.setWeekendShift(null);
            record.setHolidayShift(null);
        }

        attendanceRecordRepository.save(record);
    }


    public void updateLeaveCode(Long id, LeaveCodeUpdateDTO dto) {
        AttendanceRecord record = attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        String leaveCodeStr = dto.getLeaveCode();
        String field = dto.getTargetField();
        LeaveCode leaveCode;
        try {
            leaveCode = LeaveCode.valueOf(leaveCodeStr); 
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid leave code: " + leaveCodeStr);
        }
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("Target field is required");
        }

        switch (field) {
            case "dayShift" -> record.setDayShift(leaveCode.name());
            case "otShift" -> record.setOtShift(leaveCode.name());
            case "weekendShift" -> record.setWeekendShift(leaveCode.name());
            case "holidayShift" -> record.setHolidayShift(leaveCode.name());
            default -> throw new IllegalArgumentException("Invalid field: " + field);
        }

        attendanceRecordRepository.save(record);
    }
    @Override
    public void updateDailyAttendanceForDate(LocalDate date) {
        int month = date.getMonthValue();
        int year = date.getYear();

        List<WorkSchedule> acceptedSchedules = workScheduleRepository
                .findByMonthAndYearAndIsAcceptedTrue(month, year);

        for (WorkSchedule schedule : acceptedSchedules) {
            List<WorkScheduleDetail> details = workScheduleDetailRepository
                    .findByWorkSchedule_Id(schedule.getId());

            schedule.setWorkScheduleDetails(details); // gán thủ công vào entity

            boolean hasDetailForDate = details != null &&
                    details.stream().anyMatch(d -> d.getDateWork().isEqual(date));

            if (hasDetailForDate) {
                workScheduleService.generateAttendanceRecords(schedule);
            }
        }
    }

    private void calculateShift(AttendanceRecord record) {
        Optional<WorkScheduleDetail> optionalDetail = record.getWorkSchedule().getWorkScheduleDetails().stream()
                .filter(detail -> detail.getDateWork().equals(record.getDate()))
                .findFirst();

        if (optionalDetail.isEmpty()) return;

        WorkScheduleDetail detail = optionalDetail.get();
        LocalTime scheduledStart = detail.getStartTime();
        LocalTime scheduledEnd = detail.getEndTime();
        boolean isWeekend = detail.getDateWork().getDayOfWeek().getValue() == 7;
        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(record.getDate());

        LocalTime checkIn = record.getCheckInTime();
        LocalTime checkOut = record.getCheckOutTime();

        record.setDayShift(null);
        record.setOtShift(null);
        record.setWeekendShift(null);
        record.setHolidayShift(null);

        if (checkIn != null && checkOut != null && scheduledStart != null && scheduledEnd != null) {
            LocalTime dayShiftIn = checkIn.isBefore(scheduledStart) ? scheduledStart : checkIn;
            LocalTime dayShiftOut = checkOut.isBefore(scheduledEnd) ? checkOut : scheduledEnd;
            LocalTime standardEnd = LocalTime.of(17, 0);

            LocalTime endPoint = checkOut.isBefore(standardEnd) ? checkOut : standardEnd;
            int startSec = dayShiftIn.toSecondOfDay();
            int endSec = endPoint.toSecondOfDay();

            int breakStart = LocalTime.of(11, 30).toSecondOfDay();
            int breakEnd = LocalTime.of(12, 30).toSecondOfDay();
            int overlapStart = Math.max(startSec, breakStart);
            int overlapEnd = Math.min(endSec, breakEnd);
            int overlap = Math.max(0, overlapEnd - overlapStart);

            float shiftHours = (float) (endSec - startSec - overlap) / 3600f;
            shiftHours = Math.max(0, shiftHours);

            float overtimeHours = 0f;
            if (checkOut.isAfter(standardEnd)) {
                overtimeHours = (float) (dayShiftOut.toSecondOfDay() - standardEnd.toSecondOfDay()) / 3600;
            }

            if (isHoliday && isWeekend) {
                String value = String.format("%.2f", shiftHours + overtimeHours);
                record.setWeekendShift(value);
                record.setHolidayShift(value);
            } else if (isHoliday) {
                record.setHolidayShift(String.format("%.2f", shiftHours + overtimeHours));
            } else if (isWeekend) {
                record.setWeekendShift(String.format("%.2f", shiftHours + overtimeHours));
            } else {
                if (shiftHours > 0) record.setDayShift(String.format("%.2f", shiftHours));
                if (overtimeHours > 0) record.setOtShift(String.format("%.2f", overtimeHours));
            }
        }
    }


}
