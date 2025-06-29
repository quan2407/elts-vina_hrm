package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleDetailResponseDTO {
    private Long id;
    private LocalDate dateWork;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isOvertime;
    private Long lineId;
    private String lineName;
    private Long departmentId;
    private String departmentName;
    private Long workScheduleId;
}
