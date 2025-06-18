package sep490.com.example.hrms_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/username")
public class UsernameController {

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUsername(Authentication authentication) {
        // Lấy username từ Authentication
        String username = authentication.getName();

        // Trả về response
        return ResponseEntity.ok(username);
    }
}
