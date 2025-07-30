package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Notification;

import java.util.Set;
import java.util.stream.Collectors;

public class NotificationMapper {

    // Convert từ entity → DTO
    public static NotificationDto toDto(Notification entity) {

        return new NotificationDto(
                entity.getType(),
                entity.getContent(),
                entity.getIsRead(),
                entity.getCreatedAt()
        );
    }

}
