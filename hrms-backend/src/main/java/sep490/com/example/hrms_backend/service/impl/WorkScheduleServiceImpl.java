package sep490.com.example.hrms_backend.service.impl;

import com.example.hrms_backend.dto.WorkScheduleMonthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.WorkScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.WorkScheduleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final LineRepository lineRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final WorkScheduleDetailRepository workScheduleDetailRepository;

    @Override
    public List<WorkScheduleResponseDTO> createWorkSchedulesForAll(WorkScheduleCreateDTO dto) {
        int month = dto.getMonth();
        int year = dto.getYear();

        List<Department> departments = departmentRepository.findAll();
        List<Line> allLines = lineRepository.findAllWithDepartment();
        List<WorkScheduleResponseDTO> createdList = new ArrayList<>();

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
                    createdList.add(WorkScheduleMapper.toDTO(saved));
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
                        createdList.add(WorkScheduleMapper.toDTO(saved));
                    }
                }
            }
        }

        return createdList;
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
            System.out.println("Schedule is accept: " + schedule.isAccepted());
            workScheduleRepository.save(schedule);
            System.out.println("Bat dau luu lan " + count);
            generateAttendanceRecords(schedule);
            count ++;
        }

    }

}