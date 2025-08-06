package sep490.com.example.hrms_backend.dto;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {
    private Long id;
    private String employeeName;
    private LocalDateTime requestedAt;
    private Boolean approved;
}

