package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.WorkScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.WorkScheduleService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final LineRepository lineRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final WorkScheduleDetailRepository workScheduleDetailRepository;
    private final HolidayRepository holidayRepository;
    @Override
    public List<WorkScheduleResponseDTO> createWorkSchedulesForAll(WorkScheduleCreateDTO dto) {
        int month = dto.getMonth();
        int year = dto.getYear();

        List<Department> departments = departmentRepository.findAll();
        List<Line> allLines = lineRepository.findAllWithDepartment();
        List<WorkSchedule> createdSchedules = new ArrayList<>();

        for (Department dept : departments) {
            List<Line> lines = allLines.stream()
                    .filter(line -> line.getDepartment().getDepartmentId().equals(dept.getDepartmentId()))
                    .toList();

            if (lines.isEmpty()) {
                boolean exists = workScheduleRepository.existsByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(
                        dept.getDepartmentId(), month, year);
                if (!exists) {
                    WorkSchedule entity = WorkSchedule.builder()
                            .department(dept)
                            .line(null)
                            .month(month)
                            .year(year)
                            .isDeleted(false)
                            .isAccepted(false)
                            .isSubmitted(false)
                            .build();
                    WorkSchedule saved = workScheduleRepository.save(entity);
                    createdSchedules.add(saved);
                }
            } else {
                for (Line line : lines) {
                    boolean exists = workScheduleRepository.existsByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(
                            dept.getDepartmentId(), line.getLineId(), month, year);
                    if (!exists) {
                        WorkSchedule entity = WorkSchedule.builder()
                                .department(dept)
                                .line(line)
                                .month(month)
                                .year(year)
                                .isDeleted(false)
                                .isAccepted(false)
                                .isSubmitted(false)
                                .build();
                        WorkSchedule saved = workScheduleRepository.save(entity);
                        createdSchedules.add(saved);
                    }
                }
            }
        }

        // 👉 Sau khi tất cả WorkSchedule đã được tạo xong
        for (WorkSchedule schedule : createdSchedules) {
            generateDefaultWorkScheduleDetails(schedule);
            recalcMonthlyOtUsed(schedule.getId());
        }

        return createdSchedules.stream()
                .map(WorkScheduleMapper::toDTO)
                .collect(Collectors.toList());

    }
    public void createCustomWorkSchedules(WorkScheduleRangeDTO dto) {
        if (dto.getStartDate().getMonthValue() != dto.getEndDate().getMonthValue()
                || dto.getStartDate().getYear() != dto.getEndDate().getYear()) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu và kết thúc phải nằm trong cùng một tháng và năm");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng ban"));

        List<Line> lines = new ArrayList<>();

        if (dto.getLineId() != null) {
            // Nếu có chuyền cụ thể
            Line line = lineRepository.findById(dto.getLineId())
                    .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy tổ"));
            lines.add(line);
        } else {
            // Nếu không có chuyền thì kiểm tra xem phòng ban có chuyền nào không
            lines = lineRepository.findByDepartment_DepartmentId(department.getDepartmentId());

            if (lines.isEmpty()) {
                // Phòng ban không có chuyền -> tạo lịch ở cấp phòng ban (line = null)
                createScheduleWithoutLine(department, dto);
                return;
            }
        }

        // Có ít nhất 1 chuyền
        for (Line line : lines) {
            createScheduleForLine(department, line, dto);
        }
    }
    private void createScheduleForLine(Department department, Line line, WorkScheduleRangeDTO dto) {
        LocalDate currentDate = dto.getStartDate();
        WorkSchedule scheduleOfThisLoop = null;

        while (!currentDate.isAfter(dto.getEndDate())) {
            final int month = currentDate.getMonthValue();
            final int year  = currentDate.getYear();
            final Long deptId = department.getDepartmentId();
            final Long lineId = line.getLineId();

            WorkSchedule schedule = workScheduleRepository
                    .findByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(deptId, lineId, month, year)
                    .orElseGet(() -> workScheduleRepository.save(
                            WorkSchedule.builder()
                                    .department(department)
                                    .line(line)
                                    .month(month)
                                    .year(year)
                                    .isDeleted(false)
                                    .isAccepted(false)
                                    .isSubmitted(false)
                                    .needRevision(false)
                                    .build()
                    ));

            scheduleOfThisLoop = schedule;

            saveOrUpdateScheduleDetail(schedule, currentDate, dto);
            currentDate = currentDate.plusDays(1);
        }

        if (scheduleOfThisLoop != null) {
            recalcMonthlyOtUsed(scheduleOfThisLoop.getId());
        }
    }



    private void createScheduleWithoutLine(Department department, WorkScheduleRangeDTO dto) {
        LocalDate currentDate = dto.getStartDate();
        WorkSchedule scheduleOfThisLoop = null;

        while (!currentDate.isAfter(dto.getEndDate())) {
            final int month = currentDate.getMonthValue();
            final int year  = currentDate.getYear();
            final Long deptId = department.getDepartmentId();

            WorkSchedule schedule = workScheduleRepository
                    .findByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(deptId, month, year)
                    .orElseGet(() -> workScheduleRepository.save(
                            WorkSchedule.builder()
                                    .department(department)
                                    .line(null)
                                    .month(month)
                                    .year(year)
                                    .isDeleted(false)
                                    .isAccepted(false)
                                    .isSubmitted(false)
                                    .needRevision(false)
                                    .build()
                    ));

            scheduleOfThisLoop = schedule;

            saveOrUpdateScheduleDetail(schedule, currentDate, dto);
            currentDate = currentDate.plusDays(1);
        }

        if (scheduleOfThisLoop != null) {
            recalcMonthlyOtUsed(scheduleOfThisLoop.getId()); // << giữ nguyên
        }
    }



    private void saveOrUpdateScheduleDetail(WorkSchedule schedule, LocalDate currentDate, WorkScheduleRangeDTO dto) {
        // Nếu đã có detail cho ngày đó, xóa đi để ghi đè
        workScheduleDetailRepository.findByWorkSchedule_IdAndDateWork(schedule.getId(), currentDate)
                .ifPresent(workScheduleDetailRepository::delete);

        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(currentDate);
        boolean isWeekend = currentDate.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isLate = dto.getEndTime().isAfter(LocalTime.of(17, 0));
        boolean isOvertime = isHoliday || isWeekend || isLate;
        int extraOt = computeOvertimeMinutes(currentDate, dto.getStartTime(), dto.getEndTime());
        ensureMonthlyCap(schedule.getId(), extraOt);
        WorkScheduleDetail detail = WorkScheduleDetail.builder()
                .dateWork(currentDate)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isOvertime(isOvertime)
                .workSchedule(schedule)
                .build();

        workScheduleDetailRepository.save(detail);
    }



    private void generateDefaultWorkScheduleDetails(WorkSchedule workSchedule) {
        int month = workSchedule.getMonth();
        int year = workSchedule.getYear();

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        List<WorkScheduleDetail> details = new ArrayList<>();


        LocalDate today = LocalDate.now();
        int startDay = 1;

        if (today.getYear() == year && today.getMonthValue() == month) {
            startDay = today.getDayOfMonth();
        }

        for (int day = startDay; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);

            // Skip Sunday
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;

            // Skip holidays
            boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date);
            if (isHoliday) continue;

            WorkScheduleDetail detail = WorkScheduleDetail.builder()
                    .dateWork(date)
                    .startTime(startTime)
                    .endTime(endTime)
                    .isOvertime(false)
                    .workSchedule(workSchedule)
                    .build();

            details.add(detail);
        }

        workScheduleDetailRepository.saveAll(details);
    }


    public void generateAttendanceRecords(WorkSchedule workSchedule) {
        int year = workSchedule.getYear();
        int month = workSchedule.getMonth();
        Long departmentId = workSchedule.getDepartment().getDepartmentId();
        Long lineId = workSchedule.getLine() != null ? workSchedule.getLine().getLineId() : null;

        List<Employee> employees = (lineId != null)
                ? employeeRepository.findByDepartment_DepartmentIdAndLine_LineIdAndIsDeletedFalse(departmentId, lineId)
                : employeeRepository.findByDepartment_DepartmentIdAndIsDeletedFalse(departmentId);
        List<WorkScheduleDetail> scheduleDetails = workSchedule.getWorkScheduleDetails();

        if (scheduleDetails == null || scheduleDetails.isEmpty()) return;

        LocalDate today = LocalDate.now();
        List<AttendanceRecord> records = new ArrayList<>();
        for (Employee employee : employees) {
            for (WorkScheduleDetail detail : scheduleDetails) {
                LocalDate workDate = detail.getDateWork();

                // 👉 Chỉ xử lý ngày trước hôm nay
                if (!workDate.isBefore(today)) continue;

                // 🔒 Kiểm tra trùng (tránh sinh lại)
                boolean exists = attendanceRecordRepository
                        .existsByEmployee_EmployeeIdAndDate(employee.getEmployeeId(), workDate);
                if (exists) continue;

                LocalTime start = detail.getStartTime();
                LocalTime end = detail.getEndTime();

                boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(workDate);
                boolean isWeekend = workDate.getDayOfWeek() == DayOfWeek.SUNDAY;
                LocalTime standardEnd = LocalTime.of(17, 0);

                // Tính công ngày
                LocalTime dayShiftIn = start;
                LocalTime dayShiftOut = end.isBefore(standardEnd) ? end : standardEnd;

                int startSec = dayShiftIn.toSecondOfDay();
                int endSec = dayShiftOut.toSecondOfDay();
                int breakStart = LocalTime.of(11, 30).toSecondOfDay();
                int breakEnd = LocalTime.of(12, 30).toSecondOfDay();
                int overlapStart = Math.max(startSec, breakStart);
                int overlapEnd = Math.min(endSec, breakEnd);
                int overlap = Math.max(0, overlapEnd - overlapStart);

                float shiftHours = (float) (endSec - startSec - overlap) / 3600f;
                shiftHours = Math.max(0, shiftHours);

                // Tính tăng ca
                float overtimeHours = 0f;
                if (end.isAfter(standardEnd)) {
                    overtimeHours = (float) (end.toSecondOfDay() - standardEnd.toSecondOfDay()) / 3600f;
                }

                String dayShiftStr = null, otShiftStr = null, weekendStr = null, holidayStr = null;

                if (isHoliday && isWeekend) {
                    String value = String.format("%.2f", shiftHours + overtimeHours);
                    holidayStr = value;
                    weekendStr = value;
                } else if (isHoliday) {
                    holidayStr = String.format("%.2f", shiftHours + overtimeHours);
                } else if (isWeekend) {
                    weekendStr = String.format("%.2f", shiftHours + overtimeHours);
                } else {
                    if (shiftHours > 0) dayShiftStr = String.format("%.2f", shiftHours);
                    if (overtimeHours > 0) otShiftStr = String.format("%.2f", overtimeHours);
                }

                AttendanceRecord record = AttendanceRecord.builder()
                        .employee(employee)
                        .workSchedule(workSchedule)
                        .date(workDate)
                        .month(workDate.getMonthValue())
                        .year(workDate.getYear())
                        .checkInTime(start)
                        .checkOutTime(end)
                        .dayShift(dayShiftStr)
                        .otShift(otShiftStr)
                        .weekendShift(weekendStr)
                        .holidayShift(holidayStr)
                        .leaveDaysRemaining(1.0f)
                        .build();

                records.add(record);
            }
        }

        attendanceRecordRepository.saveAll(records);
    }




    @Override
    public List<WorkScheduleMonthDTO> getAvailableMonths() {
        return workScheduleRepository.findAllAvailableMonths();
    }

    @Override
    public Long resolveWorkScheduleId(Long departmentId, Long lineId, LocalDate dateWork) {
        int month = dateWork.getMonthValue();
        int year = dateWork.getYear();

        Optional<WorkSchedule> existing;

        if (lineId != null) {
            existing = workScheduleRepository.findByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(departmentId, lineId, month, year);
        } else {
            existing = workScheduleRepository.findByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(departmentId, month, year);
        }

        return existing
                .map(WorkSchedule::getId)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.BAD_REQUEST, "WorkSchedule chưa được tạo"));
    }

    @Override
    public void submitAllWorkSchedules(int month, int year) {
        List<WorkSchedule> schedules = workScheduleRepository
                .findByMonthAndYearAndIsSubmittedFalseAndIsDeletedFalse(month, year);

        for (WorkSchedule schedule : schedules) {
            recalcMonthlyOtUsed(schedule.getId());
            if (schedule.getMonthlyOtUsedMinutes() > schedule.getMonthlyOtCapMinutes()) {
                double used = schedule.getMonthlyOtUsedMinutes() / 60.0;
                throw new HRMSAPIException(HttpStatus.BAD_REQUEST,
                        String.format("Lịch %s/%s vượt 40h OT: %.2f giờ.",
                                schedule.getDepartment().getDepartmentName(),
                                schedule.getLine() != null ? schedule.getLine().getLineName() : "Phòng ban",
                                used));
            }
            schedule.setSubmitted(true);
            schedule.setAccepted(false);
            schedule.setNeedRevision(false);
        }
        workScheduleRepository.saveAll(schedules);
    }

    @Override
    public void acceptAllSubmittedSchedules(int month, int year) {
        List<WorkSchedule> schedules = workScheduleRepository
                .findByMonthAndYearAndIsSubmittedTrueAndIsAcceptedFalse(month, year);
        for (WorkSchedule schedule : schedules) {
            schedule.setSubmitted(false);
            schedule.setAccepted(true);
            schedule.setNeedRevision(false);
            schedule.setRejectReason(null);
            workScheduleRepository.save(schedule);
            generateAttendanceRecords(schedule);
        }

    }
    public List<EmployeeWorkScheduleDTO> getWorkScheduleForEmployee(Long employeeId, int month, int year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));

        Long lineId = employee.getLine() != null ? employee.getLine().getLineId() : null;
        Long departmentId = employee.getDepartment().getDepartmentId();

        Optional<WorkSchedule> scheduleOpt = (lineId != null)
                ? workScheduleRepository.findByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(departmentId, lineId, month, year)
                : workScheduleRepository.findByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(departmentId, month, year);

        WorkSchedule schedule = scheduleOpt.orElseThrow(() ->
                new HRMSAPIException(HttpStatus.NOT_FOUND, "Không có lịch làm việc cho tháng này"));

        List<WorkScheduleDetail> details = schedule.getWorkScheduleDetails();

        return details.stream()
                .map(detail -> {
                    return EmployeeWorkScheduleDTO.builder()
                            .date(detail.getDateWork())
                            .startTime(detail.getStartTime())
                            .endTime(detail.getEndTime())
                            .isOvertime(Boolean.TRUE.equals(detail.getIsOvertime()))
                            .lineName(schedule.getLine() != null ? schedule.getLine().getLineName() : null)
                            .departmentName(schedule.getDepartment().getDepartmentName())
                            .build();
                }).toList();
    }

    @Override
    public void rejectSubmittedSchedule(int month, int year, String reason) {
        List<WorkSchedule> schedules = workScheduleRepository
                .findByMonthAndYearAndIsSubmittedTrueAndIsAcceptedFalse(month, year);

        for (WorkSchedule schedule : schedules) {
            schedule.setSubmitted(false);
            schedule.setAccepted(false);
            schedule.setNeedRevision(false);
            schedule.setRejectReason(reason);
        }

        workScheduleRepository.saveAll(schedules);
    }
    @Override
    public void requestRevision(int month, int year, String reason) {
        List<WorkSchedule> approvedSchedules = workScheduleRepository
                .findByMonthAndYearAndIsSubmittedTrueAndIsAcceptedTrue(month, year);

        for (WorkSchedule schedule : approvedSchedules) {
            schedule.setAccepted(false);
            schedule.setSubmitted(false);
            schedule.setNeedRevision(true);
            schedule.setRejectReason(reason);
        }

        workScheduleRepository.saveAll(approvedSchedules);
    }
    private int minutesBetween(LocalTime from, LocalTime to) {
        int a = from.toSecondOfDay();
        int b = to.toSecondOfDay();
        if (b < a) b += 24 * 3600;
        return (b - a) / 60;
    }

    private int computeOvertimeMinutes(WorkScheduleDetail d) {
        LocalDate date = d.getDateWork();
        LocalTime start = d.getStartTime();
        LocalTime end = d.getEndTime();

        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date);
        boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SUNDAY;

        if (isHoliday || isWeekend) {
            return minutesBetween(start, end);
        }
        LocalTime standardEnd = LocalTime.of(17, 0);
        if (end.isAfter(standardEnd)) {
            return minutesBetween(standardEnd, end);
        }
        return 0;
    }

    private void recalcMonthlyOtUsed(Long scheduleId) {
        WorkSchedule s = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "WorkSchedule không tồn tại"));

        List<WorkScheduleDetail> details =
                workScheduleDetailRepository.findByWorkSchedule_Id(scheduleId);

        int used = 0;
        for (WorkScheduleDetail d : details) {
            used += computeOvertimeMinutes(d);
        }
        s.setMonthlyOtUsedMinutes(used);
        workScheduleRepository.save(s);
    }

    // Tính OT theo tham số (không cần entity)
    private int computeOvertimeMinutes(LocalDate date, LocalTime start, LocalTime end) {
        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date);
        boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SUNDAY;

        if (isHoliday || isWeekend) {
            return minutesBetween(start, end);
        }
        LocalTime standardEnd = LocalTime.of(17, 0);
        if (end.isAfter(standardEnd)) {
            return minutesBetween(standardEnd, end);
        }
        return 0;
    }

    // Kiểm tra trần OT tháng cho 1 WorkSchedule
    private void ensureMonthlyCap(Long scheduleId, int extraMinutes) {
        WorkSchedule s = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "WorkSchedule không tồn tại"));

        // Tổng OT hiện tại
        List<WorkScheduleDetail> details = workScheduleDetailRepository.findByWorkSchedule_Id(scheduleId);
        int current = 0;
        for (WorkScheduleDetail d : details) {
            current += computeOvertimeMinutes(d.getDateWork(), d.getStartTime(), d.getEndTime());
        }

        int cap = Optional.ofNullable(s.getMonthlyOtCapMinutes()).orElse(2400); // 40h = 2400 phút
        int prospective = current + extraMinutes;

        if (prospective > cap) {
            int remain = Math.max(0, cap - current);
            double remainHours = Math.round((remain / 60.0) * 10) / 10.0;
            throw new HRMSAPIException(
                    HttpStatus.BAD_REQUEST,
                    "Vượt trần OT tháng (40h). Còn lại: " + remainHours + "h"
            );
        }
    }

}