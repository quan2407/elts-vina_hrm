package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class WorkScheduleDayDetailDTO {
    private LocalDate date;
    private String weekday;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isOvertime;
    private Long workScheduleId;
}
