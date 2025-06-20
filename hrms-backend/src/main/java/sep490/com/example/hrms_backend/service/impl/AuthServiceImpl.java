package sep490.com.example.hrms_backend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.LoginDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.security.JwtTokenProvider;
import sep490.com.example.hrms_backend.service.AuthService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginDto loginDto) {
        // Tìm account theo username hoặc email
        Account account = accountRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(),loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.UNAUTHORIZED, "Tài khoản không tồn tại"));

        // Check trạng thái
        if (Boolean.FALSE.equals(account.getIsActive())) {
            throw new HRMSAPIException(HttpStatus.UNAUTHORIZED, "Tài khoản đã bị khóa");
        }

        if (account.getLoginAttempts() <= 0) {
            account.setIsActive(false);
            accountRepository.save(account);
            throw new HRMSAPIException(HttpStatus.UNAUTHORIZED, "Tài khoản đã bị khóa do vượt quá số lần đăng nhập sai");
        }

        try {
            // Xác thực
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(),
                            loginDto.getPassword()
                    )
            );

            // Xác thực thành công
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Reset loginAttempts
            account.setLoginAttempts(5);
            accountRepository.save(account);

            // Tạo token
            return jwtTokenProvider.generateToken(authentication);

        } catch (BadCredentialsException e) {
            // Sai mật khẩu
            int remainingAttempts = account.getLoginAttempts() - 1;
            account.setLoginAttempts(remainingAttempts);

            if (remainingAttempts <= 0) {
                account.setIsActive(false);
            }

            accountRepository.save(account);

            throw new HRMSAPIException(HttpStatus.UNAUTHORIZED,
                    "Sai mật khẩu. Số lần còn lại: " + Math.max(remainingAttempts, 0));
        }
    }
}
