package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.NotificationDto;
import sep490.com.example.hrms_backend.service.NotificationService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.util.List;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping
    public ResponseEntity<?> getNotificvationList() {

        Long employeeId = currentUserUtils.getCurrentEmployeeId();

        List<NotificationDto> notificationList = notificationService.getNotification(employeeId);

        return ResponseEntity.ok(notificationList);

    }

}
