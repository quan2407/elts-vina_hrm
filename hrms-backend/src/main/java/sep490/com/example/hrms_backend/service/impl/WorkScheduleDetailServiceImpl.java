package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.WorkSchedule;
import sep490.com.example.hrms_backend.entity.WorkScheduleDetail;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.WorkScheduleDetailService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class WorkScheduleDetailServiceImpl implements WorkScheduleDetailService {

    private final WorkScheduleDetailRepository workScheduleDetailRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final LineRepository lineRepository;
    private final DepartmentRepository departmentRepository;
    private final HolidayRepository holidayRepository;

    @Override
    @Transactional
    public WorkScheduleDetailResponseDTO create(WorkScheduleDetailCreateDTO dto) {
        WorkSchedule schedule = workScheduleRepository.findById(dto.getWorkScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch làm việc"));


        if (dto.getDateWork().getMonthValue() != schedule.getMonth()
                || dto.getDateWork().getYear() != schedule.getYear()) {
            throw new IllegalArgumentException("Ngày làm việc không nằm trong tháng/năm của lịch làm việc");
        }

        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(dto.getDateWork());
        boolean isWeekend = dto.getDateWork().getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isLate = dto.getEndTime().isAfter(LocalTime.of(17, 0));
        boolean isOvertime = isHoliday || isWeekend || isLate;


        WorkScheduleDetail entity = WorkScheduleDetail.builder()
                .dateWork(dto.getDateWork())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isOvertime(isOvertime)
                .workSchedule(schedule)
                .build();


        int extra = computeOvertimeMinutes(dto.getDateWork(), dto.getStartTime(), dto.getEndTime());
        ensureMonthlyCap(schedule.getId(), extra, null);

        WorkScheduleDetail saved = workScheduleDetailRepository.save(entity);
        recalcForSchedule(schedule.getId());



        return WorkScheduleDetailResponseDTO.builder()
                .id(saved.getId())
                .dateWork(saved.getDateWork())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .isOvertime(saved.getIsOvertime())
                .lineId(schedule.getLine() != null ? schedule.getLine().getLineId() : null)
                .lineName(schedule.getLine() != null ? schedule.getLine().getLineName() : "Chưa phân tổ")
                .departmentId(schedule.getDepartment().getDepartmentId())
                .departmentName(schedule.getDepartment().getDepartmentName())
                .workScheduleId(schedule.getId())
                .build();
    }

    // Target: get all schedule for all department and all line in a month a year
    // Idea
    // 1st: get all work schedule detail data for a month a year
    // 2nd: from work schedule detail get all department in work schedule
    // 3rd: from department continue to get line list
    // 4th: each line get day detail
    // 5th: for the rest day if not set, just create day null to display
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentWorkScheduleViewDTO> getMonthlyWorkSchedule(int month, int year) {
        boolean exists = workScheduleRepository.existsByMonthAndYearAndIsDeletedFalse(month, year);
        if (!exists) {
            throw new HRMSAPIException(HttpStatus.NOT_FOUND, "Chưa có lịch làm việc nào được tạo cho tháng " + month + "/" + year);
        }

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        List<Department> allDepartments = departmentRepository.findAll();
        List<Line> allLines = lineRepository.findAllWithDepartment();
        List<WorkScheduleDetail> allDetails = workScheduleDetailRepository
                .findByWorkSchedule_MonthAndWorkSchedule_Year(month, year);

        Map<Long, DepartmentWorkScheduleViewDTO> departmentMap = new LinkedHashMap<>();
        Map<Long, Map<Integer, WorkScheduleDetail>> detailMapByLine = new HashMap<>();
        Map<Long, Map<Integer, WorkScheduleDetail>> detailMapByDepartment = new HashMap<>();

        for (WorkScheduleDetail detail : allDetails) {
            int day = detail.getDateWork().getDayOfMonth();
            if (detail.getWorkSchedule().getLine() != null) {
                Long lineId = detail.getWorkSchedule().getLine().getLineId();
                detailMapByLine.computeIfAbsent(lineId, k -> new HashMap<>()).put(day, detail);
            } else {
                Long deptId = detail.getWorkSchedule().getDepartment().getDepartmentId();
                detailMapByDepartment.computeIfAbsent(deptId, k -> new HashMap<>()).put(day, detail);
            }
        }

        Function<LocalDate, String> getWeekday = date -> switch (date.getDayOfWeek()) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };

        for (Line line : allLines) {
            Department dept = line.getDepartment();

            departmentMap.putIfAbsent(dept.getDepartmentId(), DepartmentWorkScheduleViewDTO.builder()
                    .departmentId(dept.getDepartmentId())
                    .departmentName(dept.getDepartmentName())
                    .lines(new ArrayList<>())
                    .build());

            List<WorkScheduleDayDetailDTO> workDetails = new ArrayList<>();
            Map<Integer, WorkScheduleDetail> dayMap = detailMapByLine.getOrDefault(line.getLineId(), new HashMap<>());

            WorkSchedule sampleSchedule;

            if (dayMap.size() > 0) {
                sampleSchedule = dayMap.values().stream()
                        .map(WorkScheduleDetail::getWorkSchedule)
                        .findFirst()
                        .orElse(null);
            } else {
                sampleSchedule = workScheduleRepository
                        .findByMonthAndYearAndLine_LineIdAndIsDeletedFalse(month, year, line.getLineId())
                        .orElse(null);
            }

            for (int i = 1; i <= daysInMonth; i++) {
                LocalDate date = LocalDate.of(year, month, i);
                String weekday = getWeekday.apply(date);
                WorkScheduleDetail detail = dayMap.get(i);

                workDetails.add(WorkScheduleDayDetailDTO.builder()
                        .id(detail != null ? detail.getId() : null)
                        .date(date)
                        .weekday(weekday)
                        .startTime(detail != null ? detail.getStartTime() : null)
                        .endTime(detail != null ? detail.getEndTime() : null)
                        .isOvertime(detail != null && detail.getIsOvertime())
                        .workScheduleId(detail != null ? detail.getWorkSchedule().getId() : null)
                        .build());
            }

            Integer cap = null, used = null, remain = null;
            Double remainHours = null;
            if (sampleSchedule != null) {
                cap = sampleSchedule.getMonthlyOtCapMinutes();
                used = sampleSchedule.getMonthlyOtUsedMinutes();
                remain = sampleSchedule.getMonthlyOtRemainingMinutes();
                remainHours = sampleSchedule.getMonthlyOtRemainingHours();
            }

            departmentMap.get(dept.getDepartmentId()).getLines().add(LineWorkScheduleViewDTO.builder()
                    .lineId(line.getLineId())
                    .lineName(line.getLineName())
                    .workDetails(workDetails)
                    .isSubmitted(sampleSchedule != null && sampleSchedule.isSubmitted())
                    .isAccepted(sampleSchedule != null && sampleSchedule.isAccepted())
                    .rejectReason(sampleSchedule != null ? sampleSchedule.getRejectReason() : null)

                    // ==== NEW: OT tháng ====
                    .monthlyOtCapMinutes(cap)
                    .monthlyOtUsedMinutes(used)
                    .monthlyOtRemainingMinutes(remain)
                    .monthlyOtRemainingHours(remainHours)
                    .build());

        }

        for (Department dept : allDepartments) {
            boolean alreadyAdded = departmentMap.containsKey(dept.getDepartmentId());
            boolean hasLine = allLines.stream().anyMatch(line ->
                    line.getDepartment().getDepartmentId().equals(dept.getDepartmentId())
            );

            if (alreadyAdded || hasLine) continue;

            List<WorkScheduleDayDetailDTO> workDetails = new ArrayList<>();
            Map<Integer, WorkScheduleDetail> dayMap = detailMapByDepartment.getOrDefault(dept.getDepartmentId(), new HashMap<>());

            WorkSchedule sampleSchedule;

            if (dayMap.size() > 0) {
                sampleSchedule = dayMap.values().stream()
                        .map(WorkScheduleDetail::getWorkSchedule)
                        .findFirst()
                        .orElse(null);
            } else {
                sampleSchedule = workScheduleRepository
                        .findByMonthAndYearAndDepartment_DepartmentIdAndLineIsNullAndIsDeletedFalse(month, year, dept.getDepartmentId())
                        .orElse(null);
            }

            for (int i = 1; i <= daysInMonth; i++) {
                LocalDate date = LocalDate.of(year, month, i);
                String weekday = getWeekday.apply(date);
                WorkScheduleDetail detail = dayMap.get(i);

                workDetails.add(WorkScheduleDayDetailDTO.builder()
                        .id(detail != null ? detail.getId() : null)
                        .date(date)
                        .weekday(weekday)
                        .startTime(detail != null ? detail.getStartTime() : null)
                        .endTime(detail != null ? detail.getEndTime() : null)
                        .isOvertime(detail != null && detail.getIsOvertime())
                        .workScheduleId(detail != null ? detail.getWorkSchedule().getId() : null)
                        .build());
            }

            Integer cap = null, used = null, remain = null;
            Double remainHours = null;
            if (sampleSchedule != null) {
                cap = sampleSchedule.getMonthlyOtCapMinutes();
                used = sampleSchedule.getMonthlyOtUsedMinutes();
                remain = sampleSchedule.getMonthlyOtRemainingMinutes();
                remainHours = sampleSchedule.getMonthlyOtRemainingHours();
            }

            departmentMap.put(dept.getDepartmentId(), DepartmentWorkScheduleViewDTO.builder()
                    .departmentId(dept.getDepartmentId())
                    .departmentName(dept.getDepartmentName())
                    .lines(List.of(LineWorkScheduleViewDTO.builder()
                            .lineId(null)
                            .lineName(null)
                            .workDetails(workDetails)
                            .isSubmitted(sampleSchedule != null && sampleSchedule.isSubmitted())
                            .isAccepted(sampleSchedule != null && sampleSchedule.isAccepted())
                            .rejectReason(sampleSchedule != null ? sampleSchedule.getRejectReason() : null)

                            // ==== NEW: OT tháng ====
                            .monthlyOtCapMinutes(cap)
                            .monthlyOtUsedMinutes(used)
                            .monthlyOtRemainingMinutes(remain)
                            .monthlyOtRemainingHours(remainHours)
                            .build()))
                    .build());

        }

        return new ArrayList<>(departmentMap.values());
    }

    @Override
    @Transactional
    public WorkScheduleDetailResponseDTO update(WorkScheduleDetailUpdateDTO dto) {
        WorkScheduleDetail detail = workScheduleDetailRepository.findById(dto.getWorkScheduleDetailId())
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết lịch làm việc"));

        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(detail.getDateWork());
        boolean isWeekend = detail.getDateWork().getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isLate = dto.getEndTime().isAfter(LocalTime.of(17, 0));
        boolean isOvertime = isHoliday || isWeekend || isLate;

        int extra = computeOvertimeMinutes(detail.getDateWork(), dto.getStartTime(), dto.getEndTime());
        ensureMonthlyCap(detail.getWorkSchedule().getId(), extra, detail.getId());
        detail.setStartTime(dto.getStartTime());
        detail.setEndTime(dto.getEndTime());
        detail.setIsOvertime(isOvertime);

        WorkScheduleDetail saved = workScheduleDetailRepository.save(detail);
        WorkSchedule schedule = saved.getWorkSchedule();
        recalcForSchedule(saved.getWorkSchedule().getId());

        return WorkScheduleDetailResponseDTO.builder()
                .id(saved.getId())
                .dateWork(saved.getDateWork())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .isOvertime(saved.getIsOvertime())
                .lineId(schedule.getLine() != null ? schedule.getLine().getLineId() : null)
                .lineName(schedule.getLine() != null ? schedule.getLine().getLineName() : "Chưa phân tổ")
                .departmentId(schedule.getDepartment().getDepartmentId())
                .departmentName(schedule.getDepartment().getDepartmentName())
                .workScheduleId(schedule.getId())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        WorkScheduleDetail detail = workScheduleDetailRepository.findById(id)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết lịch làm việc"));

        WorkSchedule ws = detail.getWorkSchedule();
        workScheduleDetailRepository.delete(detail);
        recalcForSchedule(ws.getId());


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

        if (isHoliday || isWeekend) return minutesBetween(start, end);
        LocalTime standardEnd = LocalTime.of(17, 0);
        if (end.isAfter(standardEnd)) return minutesBetween(standardEnd, end);
        return 0;
    }

    private void recalcForSchedule(Long scheduleId) {
        WorkSchedule s = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "WorkSchedule không tồn tại"));
        List<WorkScheduleDetail> details = workScheduleDetailRepository.findByWorkSchedule_Id(scheduleId);
        int used = details.stream().mapToInt(this::computeOvertimeMinutes).sum();
        s.setMonthlyOtUsedMinutes(used);
        workScheduleRepository.save(s);
    }
    private int computeOvertimeMinutes(LocalDate date, LocalTime start, LocalTime end) {
        boolean isHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date);
        boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SUNDAY;

        if (isHoliday || isWeekend) return minutesBetween(start, end);
        LocalTime standardEnd = LocalTime.of(17, 0);
        if (end.isAfter(standardEnd)) return minutesBetween(standardEnd, end);
        return 0;
    }
    private void ensureMonthlyCap(Long scheduleId, int extraMinutes, Long excludeDetailIdIfAny) {
        WorkSchedule s = workScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "WorkSchedule không tồn tại"));

        // tổng OT hiện tại (trừ bản ghi đang update nếu có)
        List<WorkScheduleDetail> details = workScheduleDetailRepository.findByWorkSchedule_Id(scheduleId);
        int current = details.stream()
                .filter(d -> excludeDetailIdIfAny == null || !Objects.equals(d.getId(), excludeDetailIdIfAny))
                .mapToInt(this::computeOvertimeMinutes)
                .sum();

        int cap = Optional.ofNullable(s.getMonthlyOtCapMinutes()).orElse(2400); // 40h
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
