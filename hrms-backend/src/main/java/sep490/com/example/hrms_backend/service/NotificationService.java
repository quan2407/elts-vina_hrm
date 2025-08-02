package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Notification;
import sep490.com.example.hrms_backend.enums.NotificationType;

import java.util.List;
import java.util.Set;

public interface NotificationService {

    Notification addNotification(NotificationType type, Account sender, Set<Account> recipients);

    List<NotificationDto> getNotification(Long employeeId);

    List<NotificationDto> getTodayNotification(Long empId);

    void markAsRead(Long id, Long employeeId);
}
