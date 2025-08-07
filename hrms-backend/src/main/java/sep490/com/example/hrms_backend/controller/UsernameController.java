package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/api/username")
@AllArgsConstructor
public class UsernameController {
    private CurrentUserUtils currentUserUtils;
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUsername(Authentication authentication) {
        // Lấy username từ Authentication
        String username = authentication.getName();

        // Trả về response
        return ResponseEntity.ok(username);
    }
    @GetMapping("/me/name")
    public ResponseEntity<String> getCurrentEmployeeName() {
        String employeeName = currentUserUtils.getCurrentEmployeeName();
        return ResponseEntity.ok(employeeName);
    }
}
