package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.WorkSchedule;
import sep490.com.example.hrms_backend.mapper.WorkScheduleMapper;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.repository.WorkScheduleRepository;
import sep490.com.example.hrms_backend.service.WorkScheduleService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final LineRepository lineRepository;
    private final DepartmentRepository departmentRepository;


    @Override
    public List<WorkScheduleResponseDTO> createWorkSchedulesForAll(WorkScheduleCreateDTO dto) {
        int month = dto.getMonth();
        int year = dto.getYear();

        List<Department> departments = departmentRepository.findAll();
        List<Line> allLines = lineRepository.findAllWithDepartment(); // cần viết custom query nếu chưa có
        List<WorkScheduleResponseDTO> createdList = new ArrayList<>();

        for (Department dept : departments) {
            List<Line> lines = allLines.stream()
                    .filter(line -> line.getDepartment().getDepartmentId().equals(dept.getDepartmentId()))
                    .toList();

            // Nếu phòng không có line → tạo WorkSchedule line = null
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
                    createdList.add(WorkScheduleMapper.toDTO(workScheduleRepository.save(entity)));
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
                        createdList.add(WorkScheduleMapper.toDTO(workScheduleRepository.save(entity)));
                    }
                }
            }
        }

        return createdList;
    }


}
