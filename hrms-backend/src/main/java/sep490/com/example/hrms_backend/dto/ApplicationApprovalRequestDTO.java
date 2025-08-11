package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationApprovalRequestDTO {
    private boolean approved;
    private String note;
}
