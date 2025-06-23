package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.JWTAuthResponse;
import sep490.com.example.hrms_backend.dto.LoginDto;
import sep490.com.example.hrms_backend.service.AccountService;
import sep490.com.example.hrms_backend.service.AuthService;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private AccountService accountService;

    // Build Login REST API
    @PostMapping("login")
    public ResponseEntity<JWTAuthResponse> login(@Valid @RequestBody LoginDto loginDto){
        String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        return ResponseEntity.ok(jwtAuthResponse);
    }
    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        accountService.resetPasswordByEmail(email);
        return ResponseEntity.ok("Mật khẩu mới đã được gửi tới email.");
    }


}
