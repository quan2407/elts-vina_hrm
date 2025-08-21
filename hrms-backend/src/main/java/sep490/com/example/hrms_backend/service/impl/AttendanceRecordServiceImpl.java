package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.LeaveCode;
import sep490.com.example.hrms_backend.enums.NotificationType;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;
import sep490.com.example.hrms_backend.service.NotificationService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final NotificationService notificationService;
    private final AccountRepository accountRepository;
    private final CurrentUserUtils currentUserUtils;
    //Tested
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
    public Page<AttendanceMonthlyViewDTO> getMonthlyAttendance(
            int month, int year, int page, int size, String search,
            Long departmentId, Long positionId, Long lineId) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Employee> employeePage = employeeRepository.findActiveByFilters(
                (search == null || search.isBlank()) ? null : search.trim(),
                departmentId, positionId, lineId, pageable);

        List<Employee> employees = employeePage.getContent();
        if (employees.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, employeePage.getTotalElements());
        }

        List<Long> empIds = employees.stream().map(Employee::getEmployeeId).toList();
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByMonthYearAndEmpIds(month, year, empIds);

        Map<Long, List<AttendanceRecord>> byEmp = records.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getEmployeeId()));

        List<AttendanceMonthlyViewDTO> dtoList = new ArrayList<>();
        for (Employee emp : employees) {
            AttendanceMonthlyViewDTO dto = AttendanceMonthlyViewDTO.builder()
                    .employeeId(emp.getEmployeeId())
                    .employeeCode(emp.getEmployeeCode())
                    .employeeName(emp.getEmployeeName())
                    .departmentName(emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : null)
                    .positionName(emp.getPosition() != null ? emp.getPosition().getPositionName() : null)
                    .lineName(emp.getLine() != null ? emp.getLine().getLineName() : null)
                    .attendanceByDate(new LinkedHashMap<>())
                    .totalDayShiftHours(0f).totalOvertimeHours(0f).totalWeekendHours(0f).totalHolidayHours(0f)
                    .totalHours(0f)
                    .build();

            for (AttendanceRecord r : byEmp.getOrDefault(emp.getEmployeeId(), List.of())) {
                String dateKey = String.valueOf(r.getDate().getDayOfMonth());

                boolean hasSchedule = r.getWorkSchedule() != null
                        && r.getWorkSchedule().getWorkScheduleDetails() != null
                        && r.getWorkSchedule().getWorkScheduleDetails().stream()
                        .anyMatch(d -> d.getDateWork().equals(r.getDate()));

                boolean isWeekend = hasSchedule && r.getWorkSchedule().getWorkScheduleDetails().stream()
                        .filter(d -> d.getDateWork().equals(r.getDate()))
                        .findFirst().map(d -> d.getDateWork().getDayOfWeek().getValue() == 7).orElse(false);

                boolean isHoliday = holidayRepository
                        .existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(r.getDate());

                dto.getAttendanceByDate().put(dateKey, AttendanceCellDTO.builder()
                        .attendanceRecordId(r.getId())
                        .shift(r.getDayShift())
                        .overtime(r.getOtShift())
                        .weekend(r.getWeekendShift())
                        .holiday(r.getHolidayShift())
                        .hasScheduleDetail(hasSchedule)
                        .checkIn(r.getCheckInTime() != null ? r.getCheckInTime().toString() : null)
                        .checkOut(r.getCheckOutTime() != null ? r.getCheckOutTime().toString() : null)
                        .holidayFlag(isHoliday)
                        .weekendFlag(isWeekend)
                        .build());

                dto.setTotalDayShiftHours(dto.getTotalDayShiftHours() + parseHour(r.getDayShift()));
                dto.setTotalOvertimeHours(dto.getTotalOvertimeHours() + parseHour(r.getOtShift()));
                dto.setTotalWeekendHours(dto.getTotalWeekendHours() + parseHour(r.getWeekendShift()));
                dto.setTotalHolidayHours(dto.getTotalHolidayHours() + parseHour(r.getHolidayShift()));
            }
            dto.setTotalHours(dto.getTotalDayShiftHours() + dto.getTotalOvertimeHours()
                    + dto.getTotalWeekendHours() + dto.getTotalHolidayHours());
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

    //Tested
    @Override
    public List<MonthYearDTO> getAvailableMonths() {
        List<Object[]> rawList = attendanceRecordRepository.findDistinctMonthYear();
        return rawList.stream()
                .map(row -> new MonthYearDTO((int) row[0], (int) row[1]))
                .collect(Collectors.toList());
    }

    //Tested
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
        if (record.getWorkSchedule() != null
                && record.getWorkSchedule().getWorkScheduleDetails() != null
                && record.getCheckInTime() != null
                && record.getCheckOutTime() != null) {
            calculateShift(record);
        } else {
            record.setDayShift(null);
            record.setOtShift(null);
            record.setWeekendShift(null);
            record.setHolidayShift(null);
        }

        attendanceRecordRepository.save(record);
        try {
            Account sender = currentUserUtils.getCurrentAccount();
            Long empId = record.getEmployee().getEmployeeId();
            Account receiver = accountRepository.findByEmployee_EmployeeId(empId)
                    .orElse(null);
            if (receiver != null) {
                notificationService.addNotification(NotificationType.ATTENDANCE_UPDATED, sender, receiver);
            }
        } catch (Exception ignore){}
    }

    //Tested
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
        try {
            Account sender = currentUserUtils.getCurrentAccount();
            Long empId = record.getEmployee().getEmployeeId();
            Account receiver = accountRepository.findByEmployee_EmployeeId(empId)
                    .orElse(null);
            if (receiver != null) {
                notificationService.addNotification(NotificationType.LEAVE_CODE_UPDATED, sender, receiver);
            }
        } catch (Exception ignore) { }
    }
    //Tested
    @Override
    public void updateDailyAttendanceForDate(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        for (LocalDate currentDate = firstDayOfMonth; !currentDate.isAfter(yesterday); currentDate = currentDate.plusDays(1)) {
            final LocalDate dateToCheck = currentDate;

            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            List<WorkSchedule> acceptedSchedules = workScheduleRepository
                    .findByMonthAndYearAndIsAcceptedTrue(month, year);

            for (WorkSchedule schedule : acceptedSchedules) {
                List<WorkScheduleDetail> details = workScheduleDetailRepository
                        .findByWorkSchedule_Id(schedule.getId());

                schedule.setWorkScheduleDetails(details);

                boolean hasDetailForDate = details != null &&
                        details.stream().anyMatch(d -> d.getDateWork().isEqual(dateToCheck));

                if (hasDetailForDate) {
                    workScheduleService.generateAttendanceRecords(schedule);
                }
            }
        }
        try {
            Account sender = currentUserUtils.getCurrentAccount();
            notifyMany(NotificationType.ATTENDANCE_DAILY_UPDATED, sender, findAllEmployees());
        } catch (Exception ignore) {}
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
    @Override
    public void importAttendanceFromExcel(MultipartFile file, LocalDate targetDate) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            Map<String, List<LocalTime>> timeMap = new HashMap<>();
            Map<String, Employee> employeeMap = new HashMap<>();
            Map<String, LocalDate> dateMap = new HashMap<>();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String employeeCode = formatter.formatCellValue(row.getCell(3)).trim();
                String timeStr = formatter.formatCellValue(row.getCell(1)).trim();

                if (employeeCode.isEmpty() || timeStr.isEmpty()) continue;

                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(timeStr, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    System.err.println("⚠ Không thể parse thời gian ở dòng " + (i + 1) + ": \"" + timeStr + "\"");
                    continue;
                }

                LocalDate date = dateTime.toLocalDate();
                LocalTime time = dateTime.toLocalTime();

                if (!date.equals(targetDate)) continue;

                String key = employeeCode + "_" + date;

                timeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(time);
                employeeMap.putIfAbsent(key, employeeRepository.findByEmployeeCode(employeeCode)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên: " + employeeCode)));
                dateMap.putIfAbsent(key, date);
            }

            for (String key : timeMap.keySet()) {
                Employee emp = employeeMap.get(key);
                LocalDate date = dateMap.get(key);
                List<LocalTime> times = timeMap.get(key);

                if (times.isEmpty()) continue;

                AttendanceRecord record = attendanceRecordRepository.findByEmployeeAndDate(emp, date).orElse(null);
                if (record == null) {
                    System.err.println("Không có bảng công cho ngày " + date + ", nhân viên " + emp.getEmployeeCode());
                    continue;
                }

                if (times.size() == 1) {
                    record.setCheckInTime(times.get(0));
                    record.setCheckOutTime(null);
                    updateLeaveCode(record.getId(), new LeaveCodeUpdateDTO("KL", "dayShift"));
                    attendanceRecordRepository.save(record);
                    System.out.println("Gán KL cho " + emp.getEmployeeCode());
                    continue;
                }

                LocalTime checkIn = Collections.min(times);
                LocalTime checkOut = Collections.max(times);
                record.setCheckInTime(checkIn);
                record.setCheckOutTime(checkOut);

                WorkSchedule schedule = getScheduleForEmployeeOnDate(emp, date);
                if (schedule != null) {
                    record.setWorkSchedule(schedule);
                }

                if (record.getWorkSchedule() != null
                        && record.getWorkSchedule().getWorkScheduleDetails() != null
                        && record.getCheckInTime() != null
                        && record.getCheckOutTime() != null) {
                    calculateShift(record);
                } else {
                    record.setDayShift(null);
                    record.setOtShift(null);
                    record.setWeekendShift(null);
                    record.setHolidayShift(null);
                }

                attendanceRecordRepository.save(record);
                System.out.println("✔ Đã cập nhật cho: " + emp.getEmployeeCode() + " ngày " + date);
            }

        } catch (Exception e) {
            throw new RuntimeException("Import thất bại: " + e.getMessage(), e);
        }
    }






    private WorkSchedule getScheduleForEmployeeOnDate(Employee employee, LocalDate date) {
        Long lineId = employee.getLine() != null ? employee.getLine().getLineId() : null;
        Long departmentId = employee.getDepartment().getDepartmentId();

        Optional<WorkSchedule> scheduleOpt = (lineId != null)
                ? workScheduleRepository.findByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(
                departmentId, lineId, date.getMonthValue(), date.getYear())
                : workScheduleRepository.findByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(
                departmentId, date.getMonthValue(), date.getYear());

        if (scheduleOpt.isEmpty()) return null;

        WorkSchedule schedule = scheduleOpt.get();
        boolean hasDetail = schedule.getWorkScheduleDetails().stream()
                .anyMatch(d -> d.getDateWork().isEqual(date));

        return hasDetail ? schedule : null;
    }

    @Override
    public List<AttendanceMonthlyViewDTO> getAttendanceForExport(int month, int year) {
        List<Employee> employees = employeeRepository.findAllActive();
        List<AttendanceRecord> records = attendanceRecordRepository.findByMonthAndYear(month, year);
        Map<Long, List<AttendanceRecord>> recordsByEmployee = records.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getEmployeeId()));

        List<AttendanceMonthlyViewDTO> dtoList = new ArrayList<>();
        for (Employee emp : employees) {
            AttendanceMonthlyViewDTO dto = AttendanceMonthlyViewDTO.builder()
                    .employeeId(emp.getEmployeeId())
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


        return dtoList;
    }
    @Override
    public Map<String, Object> checkScheduleCoverage(int month, int year) {
// Lấy tất cả lịch làm việc đã được duyệt
        List<WorkSchedule> schedules = workScheduleRepository.findByMonthAndYearAndIsAcceptedTrue(month, year);


        Set<Long> employeeIds = new HashSet<>();
        Set<LocalDate> workingDates = new HashSet<>();


        for (WorkSchedule schedule : schedules) {
            List<WorkScheduleDetail> details = workScheduleDetailRepository.findByWorkSchedule_Id(schedule.getId());
            schedule.setWorkScheduleDetails(details);
            for (WorkScheduleDetail detail : details) {
                workingDates.add(detail.getDateWork());
            }

            List<Employee> employees = employeeRepository.findActiveByDepartmentAndLine(
                    schedule.getDepartment().getDepartmentId(),
                    schedule.getLine() != null ? schedule.getLine().getLineId() : null
            );


            for (Employee emp : employees) {
                employeeIds.add(emp.getEmployeeId());
            }
        }


        int totalExpected = employeeIds.size() * workingDates.size();
        int actualAttendanceCount = attendanceRecordRepository
                .countByMonthAndYearAndEmployeeIds(month, year, employeeIds);


        Map<String, Object> result = new HashMap<>();
        result.put("totalScheduleCount", totalExpected);
        result.put("totalAttendanceCount", actualAttendanceCount);
        result.put("missingCount", totalExpected - actualAttendanceCount);
        result.put("okToGenerate", totalExpected <= actualAttendanceCount);
        return result;
    }
    private void notifyMany(NotificationType type, Account sender, List<Account> receivers) {
        if (receivers == null || receivers.isEmpty()) return;
        for (Account acc : receivers) {
            if (acc == null || acc.equals(sender)) continue;
            notificationService.addNotification(type, sender, acc);
        }
    }

    private List<Account> findAllEmployees() {
        return accountRepository
                .findByRole_RoleNameInAndIsActiveTrue(
                        List.of("ROLE_EMPLOYEE","ROLE_PMC","ROLE_LINE_LEADER","ROLE_HR","ROLE_HR_MANAGER","ROLE_PRODUCTION_MANAGER")
                )
                .stream().distinct().toList();
    }

}
