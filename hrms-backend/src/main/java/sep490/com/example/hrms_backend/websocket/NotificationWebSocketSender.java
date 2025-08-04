package sep490.com.example.hrms_backend.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.entity.Account;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToAccounts(Set<Account> recipients, NotificationDto notificationDto) {
        for (Account acc : recipients) {
            messagingTemplate.convertAndSendToUser(
                    acc.getUsername(),
                    "/queue/notifications",
                    notificationDto
            );        }
    }
}
