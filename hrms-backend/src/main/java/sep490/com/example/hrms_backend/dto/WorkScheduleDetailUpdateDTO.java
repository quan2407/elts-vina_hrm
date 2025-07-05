package sep490.com.example.hrms_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sep490.com.example.hrms_backend.validation.ValidWorkTime;

import java.time.LocalTime;

@Getter
@Setter
@ValidWorkTime
public class WorkScheduleDetailUpdateDTO {

    @NotNull
    private Long workScheduleDetailId;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
