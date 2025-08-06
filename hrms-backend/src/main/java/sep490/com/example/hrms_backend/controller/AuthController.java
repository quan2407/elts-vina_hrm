package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.dto.JWTAuthResponse;
import sep490.com.example.hrms_backend.dto.LoginDto;
import sep490.com.example.hrms_backend.entity.PasswordResetRequest;
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
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest dto) {
        accountService.changePassword(dto);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
    @PostMapping("request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        accountService.requestResetPassword(email);
        return ResponseEntity.ok("Yêu cầu reset mật khẩu đã được gửi và chờ admin phê duyệt.");
    }
    @GetMapping("/admin/pending-reset-requests")
    public ResponseEntity<Page<PasswordResetRequest>> getPendingResetRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(accountService.getPendingResetRequests(page, size));
    }


    @PostMapping("/admin/approve-reset-password")
    public ResponseEntity<String> approveResetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        accountService.approveResetPassword(email);
        return ResponseEntity.ok("Reset mật khẩu thành công. Mật khẩu mới đã được gửi qua email.");
    }

}
