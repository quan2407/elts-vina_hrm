package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeWorkScheduleDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isOvertime;
    private String lineName;
    private String departmentName;
}
