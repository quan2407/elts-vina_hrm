package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class HolidayDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private boolean recurring;
}
