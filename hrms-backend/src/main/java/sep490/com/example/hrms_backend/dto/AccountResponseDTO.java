package sep490.com.example.hrms_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long accountId;
    private String username;
    private String email;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private Set<String> roles; // Trả về tên role (ADMIN, EMPLOYEE...)
}
