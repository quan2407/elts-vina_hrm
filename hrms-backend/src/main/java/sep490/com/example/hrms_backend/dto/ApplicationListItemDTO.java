package sep490.com.example.hrms_backend.dto;

import lombok.*;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationListItemDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private String statusLabel;
    private String applicationTypeName;
}
