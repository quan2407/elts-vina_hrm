package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Notification;
import sep490.com.example.hrms_backend.enums.NotificationType;
import sep490.com.example.hrms_backend.mapper.NotificationMapper;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.NotificationRepository;
import sep490.com.example.hrms_backend.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    @Override
    public Notification addNotification(NotificationType type, Account sender, Set<Account> recipients) {

        String content;
        String title;
        switch (type) {
            case LEAVE_REQUEST:
                title = "Đơn xin nghỉ phép mới";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa gửi đơn xin nghỉ phép.";
                break;
            case APPROVAL:
                title = "Yêu cầu duyệt đơn mới";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa gửi đơn.";
                break;

            case INTERVIEW_SCHEDULE:
                title = "Đơn phỏng vấn";
                content = "Nhân viên " + sender.getEmployee().getEmployeeName() + " vừa tạo lịch phỏng vấn.";
                break;

            case SHIFT_CHANGED:
                title = "Cập nhật lịch làm việc";
                content = "Nhân viên "+ sender.getEmployee().getEmployeeName() + " vừa cập nhật lịch làm việc";
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
                .account(recipients)
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDto> getNotification(Long employeeId) {


        Account account = accountRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản cho nhân viên"));


        Set<Notification> notificationList = account.getNotifications();

        return notificationList.stream().map(NotificationMapper::toDto).toList();
    }
}
