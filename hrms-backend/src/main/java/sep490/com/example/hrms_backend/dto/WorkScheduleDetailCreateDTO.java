package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleDetailCreateDTO {

    @NotNull(message = "Vui lòng chọn ngày làm việc")
    @FutureOrPresent(message = "Ngày làm không được ở quá khứ")
    private LocalDate dateWork;

    @NotNull(message = "Vui lòng chọn giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Vui lòng chọn giờ kết thúc")
    private LocalTime endTime;

    @NotNull(message = "Thiếu lịch làm việc")
    private Long workScheduleId;
}
