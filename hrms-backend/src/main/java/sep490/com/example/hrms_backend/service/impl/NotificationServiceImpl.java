package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Notification;
import sep490.com.example.hrms_backend.enums.NotificationType;
import sep490.com.example.hrms_backend.mapper.NotificationMapper;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.NotificationRepository;
import sep490.com.example.hrms_backend.service.NotificationService;
import sep490.com.example.hrms_backend.websocket.NotificationWebSocketSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationWebSocketSender notificationWebSocketSender;
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public Notification addNotification(NotificationType type, Account sender, Account recipients) {

        String content;
        String title;
        switch (type) {
            case LEAVE_REQUEST:
                title = "Đơn xin nghỉ phép mới";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa gửi đơn xin nghỉ phép.";
                break;
            case APPROVAL:
                title = "Yêu cầu duyệt đơn mới";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa gửi đơn mới.";
                break;

            case INTERVIEW_SCHEDULE:
                title = "Đơn phỏng vấn";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa tạo lịch phỏng vấn.";
                break;

            case SHIFT_CHANGED:
                title = "Cập nhật lịch làm việc";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa cập nhật lịch làm việc";
                break;

            case LINE_CHANGED:
                title = "Chuyển line làm việc";
                content = "Nhân viên " + recipients.getEmployee().getEmployeeName() + " vừa được điều chuyển line làm việc";
                break;
            default:
                title = "Thông báo mới";
                content = "Bạn có một thông báo mới từ hệ thống.";
                break;
        }

        Notification notification = Notification.builder()
                .type(type)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .receiver(recipients)
                .build();

        Notification saved = notificationRepository.save(notification);

        return saved;    }

    @Override
    public List<NotificationDto> getNotification(Long employeeId) {

        Account account = accountRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản cho nhân viên"));

        List<Notification> notificationList = account.getNotifications();

        // Sắp xếp theo createdAt giảm dần (mới nhất trước)
        return notificationList.stream()
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .map(NotificationMapper::toDto)
                .toList();
    }


    @Transactional
    public void markAsRead(Long notificationId, Long employeeId) {
        Employee e = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        Account account = e.getAccount();

        if (notification.getReceiver() == account) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }


}
