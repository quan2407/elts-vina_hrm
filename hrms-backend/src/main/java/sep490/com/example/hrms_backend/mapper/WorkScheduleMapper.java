package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.WorkSchedule;

public class WorkScheduleMapper {

    public static WorkScheduleResponseDTO toDTO(WorkSchedule entity) {
        return WorkScheduleResponseDTO.builder()
                .id(entity.getId())
                .month(entity.getMonth())
                .year(entity.getYear())
                .lineId(entity.getLine() != null ? entity.getLine().getLineId() : null)
                .lineName(entity.getLine() != null ? entity.getLine().getLineName() : null)
                .departmentId(entity.getDepartment() != null ? entity.getDepartment().getDepartmentId().toString() : null)
                .departmentName(entity.getDepartment() != null ? entity.getDepartment().getDepartmentName() : null)
                .isAccepted(entity.isAccepted())
                .isSubmitted(entity.isSubmitted())
                .rejectReason(entity.getRejectReason())
                .build();
    }

    public static WorkSchedule toEntity(WorkScheduleCreateDTO dto, Line line, Department department) {
        return WorkSchedule.builder()
                .month(dto.getMonth())
                .year(dto.getYear())
                .line(line)
                .department(department)
                .isAccepted(false)
                .isSubmitted(false)
                .isDeleted(false)
                .rejectReason(null)
                .build();
    }
}
