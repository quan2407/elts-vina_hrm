package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import sep490.com.example.hrms_backend.dto.LoginDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.security.JwtTokenProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginDto loginDto;
    private Account account;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto("user", "Password123");

        account = new Account();
        account.setUsername("user");
        account.setEmail("user@email.com");
        account.setIsActive(true);
        account.setLoginAttempts(5);
    }
    @Test
    void testLogin_Success() {
        Authentication authentication = mock(Authentication.class);

        when(accountRepository.findByUsernameOrEmail("user", "user"))
                .thenReturn(Optional.of(account));

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication))
                .thenReturn("mock-token");

        String token = authService.login(loginDto);

        assertEquals("mock-token", token);
        assertEquals(5, account.getLoginAttempts());
        verify(accountRepository).save(account);
    }
    @Test
    void testLogin_AccountNotFound() {
        when(accountRepository.findByUsernameOrEmail("user", "user"))
                .thenReturn(Optional.empty());

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> authService.login(loginDto)
        );

        assertEquals("Tài khoản không tồn tại", ex.getMessage());
    }
    @Test
    void testLogin_AccountLocked() {
        account.setIsActive(false);
        when(accountRepository.findByUsernameOrEmail("user", "user"))
                .thenReturn(Optional.of(account));

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> authService.login(loginDto)
        );

        assertEquals("Tài khoản đã bị khóa", ex.getMessage());
    }
    @Test
    void testLogin_AccountOutOfAttempts() {
        account.setLoginAttempts(0);
        when(accountRepository.findByUsernameOrEmail("user", "user"))
                .thenReturn(Optional.of(account));

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> authService.login(loginDto)
        );

        assertEquals("Tài khoản đã bị khóa do vượt quá số lần đăng nhập sai", ex.getMessage());
        assertFalse(account.getIsActive());
        verify(accountRepository).save(account);
    }
    @Test
    void testLogin_WrongPassword() {
        account.setLoginAttempts(2);

        when(accountRepository.findByUsernameOrEmail("user", "user"))
                .thenReturn(Optional.of(account));

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> authService.login(loginDto)
        );

        assertTrue(ex.getMessage().contains("Sai mật khẩu"));
        assertEquals(1, account.getLoginAttempts());
        verify(accountRepository).save(account);
    }

    @Test
    void testLogin_WrongPassword_LockAfter() {
        account.setLoginAttempts(1);

        when(accountRepository.findByUsernameOrEmail("user", "user"))
                .thenReturn(Optional.of(account));

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        HRMSAPIException ex = assertThrows(
                HRMSAPIException.class,
                () -> authService.login(loginDto)
        );

        assertEquals(0, account.getLoginAttempts());
        assertFalse(account.getIsActive());
        assertTrue(ex.getMessage().contains("Sai mật khẩu"));
        verify(accountRepository).save(account);
    }


}
