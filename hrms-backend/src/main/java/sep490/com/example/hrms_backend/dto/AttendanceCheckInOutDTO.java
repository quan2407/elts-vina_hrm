package sep490.com.example.hrms_backend.dto;

import lombok.Data;
import sep490.com.example.hrms_backend.validation.ValidCheckInOut;

@ValidCheckInOut
@Data
public class AttendanceCheckInOutDTO {
    private String checkIn;
    private String checkOut;
}
