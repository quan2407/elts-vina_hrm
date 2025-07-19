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
        while (!currentDate.isAfter(dto.getEndDate())) {
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            WorkSchedule schedule = workScheduleRepository
                    .findByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(
                            department.getDepartmentId(), line.getLineId(), month, year)
                    .orElseGet(() -> workScheduleRepository.save(
                            WorkSchedule.builder()
                                    .department(department)
                                    .line(line)
                                    .month(month)
                                    .year(year)
                                    .isDeleted(false)
                                    .isAccepted(false)
                                    .isSubmitted(false)
                                    .build()
                    ));

            saveOrUpdateScheduleDetail(schedule, currentDate, dto);
            currentDate = currentDate.plusDays(1);
        }
    }

    private void createScheduleWithoutLine(Department department, WorkScheduleRangeDTO dto) {
        LocalDate currentDate = dto.getStartDate();
        while (!currentDate.isAfter(dto.getEndDate())) {
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            WorkSchedule schedule = workScheduleRepository
                    .findByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(
                            department.getDepartmentId(), month, year)
                    .orElseGet(() -> workScheduleRepository.save(
                            WorkSchedule.builder()
                                    .department(department)
                                    .line(null)
                                    .month(month)
                                    .year(year)
                                    .isDeleted(false)
                                    .isAccepted(false)
                                    .isSubmitted(false)
                                    .build()
                    ));

            saveOrUpdateScheduleDetail(schedule, currentDate, dto);
            currentDate = currentDate.plusDays(1);
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

        for (int day = 1; day <= daysInMonth; day++) {
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

    private void generateAttendanceRecords(WorkSchedule workSchedule) {
        int year = workSchedule.getYear();
        int month = workSchedule.getMonth();
        Long departmentId = workSchedule.getDepartment().getDepartmentId();
        Long lineId = workSchedule.getLine() != null ? workSchedule.getLine().getLineId() : null;

        List<Employee> employees = (lineId != null)
                ? employeeRepository.findByDepartment_DepartmentIdAndLine_LineIdAndIsDeletedFalse(departmentId, lineId)
                : employeeRepository.findByDepartment_DepartmentIdAndIsDeletedFalse(departmentId);

        List<WorkScheduleDetail> scheduleDetails = workSchedule.getWorkScheduleDetails();

        if (scheduleDetails == null || scheduleDetails.isEmpty()) {
            System.out.println("Detail la " + workSchedule.getWorkScheduleDetails().size());
            return;
        };
        System.out.println("Da co du lieu work detail");
        List<AttendanceRecord> records = new ArrayList<>();

        for (Employee employee : employees) {
            System.out.println("Ten nhan vien la: " + employee.getEmployeeName());
            for (WorkScheduleDetail detail : scheduleDetails) {
                LocalDate workDate = detail.getDateWork();

                AttendanceRecord record = AttendanceRecord.builder()
                        .employee(employee)
                        .workSchedule(workSchedule)
                        .date(workDate)
                        .month(workDate.getMonthValue())
                        .year(workDate.getYear())
                        .dayShift("")
                        .otShift(detail.getIsOvertime() != null && detail.getIsOvertime() ? "" : null)
                        .weekendShift("")
                        .holidayShift("")
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
            schedule.setSubmitted(true);
        }

        workScheduleRepository.saveAll(schedules);
    }

    @Override
    public void acceptAllSubmittedSchedules(int month, int year) {
        List<WorkSchedule> schedules = workScheduleRepository
                .findByMonthAndYearAndIsSubmittedTrueAndIsAcceptedFalse(month, year);
        int count = 1;

        for (WorkSchedule schedule : schedules) {
            System.out.println("✅ Accepting schedule ID: " + schedule.getId());
            schedule.setAccepted(true);
            schedule.setRejectReason(null);
            System.out.println("Schedule is accept: " + schedule.isAccepted());
            workScheduleRepository.save(schedule);
            System.out.println("Bat dau luu lan " + count);
            generateAttendanceRecords(schedule);
            count ++;
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
            schedule.setRejectReason(reason);
        }

        workScheduleRepository.saveAll(schedules);
    }

}