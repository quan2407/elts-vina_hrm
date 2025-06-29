package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleResolveRequest {
    private Long departmentId;
    private Long lineId;
    private LocalDate dateWork;

}