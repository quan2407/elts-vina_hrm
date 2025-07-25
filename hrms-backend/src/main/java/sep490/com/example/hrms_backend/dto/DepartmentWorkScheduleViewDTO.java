package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DepartmentWorkScheduleViewDTO {
    private Long departmentId;
    private String departmentName;
    private List<LineWorkScheduleViewDTO> lines;
}
