package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDTO {

    private Long id;
    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    private LocalDate startDate;
    @NotNull(message = "Vui lòng chọn ngày kết thúc")
    private LocalDate endDate;
    private String name;
    private boolean recurring;
}
