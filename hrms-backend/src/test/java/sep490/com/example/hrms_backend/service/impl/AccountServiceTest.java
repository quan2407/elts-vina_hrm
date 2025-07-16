package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.PasswordResetRequestRepository;
import sep490.com.example.hrms_backend.repository.RoleRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordResetRequestRepository passwordResetRequestRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;

    @BeforeEach
    void setup() {
        account = new Account();
        account.setUsername("testuser");
        account.setEmail("test@example.com");
        account.setPasswordHash("oldHash");
        account.setIsActive(true);
        account.setLoginAttempts(5);
    }

    @Test
    void resetPassword_validEmail_shouldSendNewPassword() {
        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));
        when(passwordEncoder.encode(any())).thenReturn("newEncoded");

        accountService.resetPasswordByEmail("test@example.com");

        verify(accountRepository).save(account);
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertTrue(account.getMustChangePassword());
        assertEquals(5, account.getLoginAttempts());
    }

    @Test
    void resetPassword_invalidEmail_shouldThrow() {
        when(accountRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> accountService.resetPasswordByEmail("notfound@example.com")
        );

        assertTrue(ex.getMessage().contains("Không tìm thấy tài khoản"));
    }

    @Test
    void changePassword_valid_shouldUpdatePassword() {
        ChangePasswordRequest req = new ChangePasswordRequest("old123", "Newpass123", "Newpass123");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("old123", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("Newpass123")).thenReturn("newHash");

        accountService.changePassword(req);

        verify(accountRepository).save(account);
        assertEquals("newHash", account.getPasswordHash());
    }

    @Test
    void changePassword_wrongOldPassword_shouldThrow() {
        ChangePasswordRequest req = new ChangePasswordRequest("wrongOld", "Newpass123", "Newpass123");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrongOld", "oldHash")).thenReturn(false);

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> accountService.changePassword(req)
        );

        assertEquals("Mật khẩu cũ không chính xác", ex.getMessage());
    }

    @Test
    void changePassword_confirmNotMatch_shouldThrow() {
        ChangePasswordRequest req = new ChangePasswordRequest("old123", "Newpass123", "Mismatch123");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("old123", "oldHash")).thenReturn(true);

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> accountService.changePassword(req)
        );

        assertEquals("Xác nhận mật khẩu mới không khớp", ex.getMessage());
    }
    @Test
    void changePassword_userNotFound_shouldThrow() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unknownUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(accountRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        ChangePasswordRequest req = new ChangePasswordRequest("old", "Newpass123", "Newpass123");

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> accountService.changePassword(req)
        );

        assertTrue(ex.getMessage().contains("Không tìm thấy tài khoản"));
    }

}
