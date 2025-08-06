package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private NotificationType type;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;

}
