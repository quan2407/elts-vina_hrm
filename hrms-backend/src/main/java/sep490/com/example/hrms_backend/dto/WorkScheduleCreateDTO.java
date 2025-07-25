package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleCreateDTO {

    @NotNull(message = "Tháng không được để trống")
    private Integer month;

    @Min(value = 2000, message = "Năm không hợp lệ (tối thiểu là 2000)")
    private int year;
}
