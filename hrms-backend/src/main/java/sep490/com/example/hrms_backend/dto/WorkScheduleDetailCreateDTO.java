package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import sep490.com.example.hrms_backend.validation.ValidWorkTime;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidWorkTime
public class WorkScheduleDetailCreateDTO {

    @NotNull(message = "Vui lòng chọn ngày làm việc")
    private LocalDate dateWork;

    @NotNull(message = "Vui lòng chọn giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Vui lòng chọn giờ kết thúc")
    private LocalTime endTime;

    @NotNull(message = "Thiếu lịch làm việc")
    private Long workScheduleId;
}
