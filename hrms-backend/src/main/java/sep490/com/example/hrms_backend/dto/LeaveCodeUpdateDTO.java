package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveCodeUpdateDTO {
    private String leaveCode;
    private String targetField;
}
