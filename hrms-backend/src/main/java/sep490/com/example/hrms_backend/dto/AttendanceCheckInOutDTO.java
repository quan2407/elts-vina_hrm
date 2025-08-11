package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.validation.ValidCheckInOut;

@ValidCheckInOut
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttendanceCheckInOutDTO {
    private String checkIn;
    private String checkOut;
}
