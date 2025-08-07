package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.service.NotificationService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping
    public ResponseEntity<?> getNotificationList() {

        Long employeeId = currentUserUtils.getCurrentEmployeeId();

        List<NotificationDto> notificationList = notificationService.getNotification(employeeId);

        return ResponseEntity.ok(notificationList);

    }

//    public void notifyEmployee(NotificationDto dto) {
//        // Gửi socket tới user có username là dto.getReceiverUsername()
//        messagingTemplate.convertAndSendToUser(
//                dto.getReceiverUsername(),
//                "/queue/notifications",
//                dto
//        );
//    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id
    ) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();
        notificationService.markAsRead(id, employeeId);
        return ResponseEntity.ok().build();
    }

}
