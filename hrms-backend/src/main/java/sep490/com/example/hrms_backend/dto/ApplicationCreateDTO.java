package sep490.com.example.hrms_backend.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import sep490.com.example.hrms_backend.enums.HalfDayType;
import sep490.com.example.hrms_backend.enums.LeaveCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCreateDTO {
    private Long employeeId;
    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private LeaveCode leaveCode;
    private Boolean isHalfDay;
    private HalfDayType halfDayType;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private Long applicationTypeId;
    private String attachmentPath;
}
