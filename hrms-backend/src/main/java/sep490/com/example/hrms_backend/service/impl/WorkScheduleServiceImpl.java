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
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final LineRepository lineRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

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
                            .build();
                    WorkSchedule saved = workScheduleRepository.save(entity);
                    generateAttendanceRecords(saved);
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
                                .build();
                        WorkSchedule saved = workScheduleRepository.save(entity);
                        generateAttendanceRecords(saved);
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

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        List<AttendanceRecord> records = new ArrayList<>();

        for (Employee employee : employees) {
            for (int day = 1; day <= daysInMonth; day++) {
                records.add(AttendanceRecord.builder()
                        .employee(employee)
                        .workSchedule(workSchedule)
                        .date(LocalDate.of(year, month, day))
                        .month(month)
                        .year(year)
                        .dayShift("")
                        .otShift("")
                        .weekendShift("")
                        .holidayShift("")
                        .build());
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
}
