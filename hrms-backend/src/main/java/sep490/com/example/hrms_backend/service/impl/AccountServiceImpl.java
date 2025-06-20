package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.AccountMapper;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.RoleRepository;
import sep490.com.example.hrms_backend.service.AccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(account -> AccountMapper.mapToAccountResponseDTO(account, new AccountResponseDTO()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createAutoAccountForEmployee(Employee employee) {
        String email = employee.getEmail();
        if (email == null || !email.contains("@")) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Email nhân viên không hợp lệ để tạo tài khoản");
        }

        String username = email.split("@")[0];
        if (accountRepository.existsByUsername(username)) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Tên đăng nhập đã tồn tại trong hệ thống: " + username);
        }

        String rawPassword = generateRandomPassword(8);
        String hashedPassword = passwordEncoder.encode(rawPassword);

        String roleName = mapPositionToRole(employee.getPosition().getPositionName());
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Không tìm thấy role: " + roleName));

        Account account = Account.builder()
                .username(username)
                .passwordHash(hashedPassword)
                .email(email)
                .employee(employee)
                .role(role)
                .isActive(true)
                .mustChangePassword(true)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        sendPasswordEmail(email, username, rawPassword);
    }

    private String generateRandomPassword(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    private String mapPositionToRole(String positionName) {
        positionName = positionName.toLowerCase();

        if (positionName.contains("hr")) {
            return "ROLE_HR";
        } else if (positionName.contains("canteen")) {
            return "ROLE_CANTEEN";
        } else if (positionName.contains("pmc")) {
            return "ROLE_PMC";
        } else if (positionName.contains("tổ trưởng") || positionName.contains("trưởng ca")) {
            return "ROLE_LINE_LEADER";
        } else if (positionName.contains("quản lý") || positionName.contains("phó phòng") || positionName.contains("tổng quản lý")) {
            return "ROLE_PRODUCTION_MANAGER";
        } else {
            return "ROLE_EMPLOYEE";
        }
    }

    private void sendPasswordEmail(String to, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Tài khoản HRMS của bạn đã được tạo");
            message.setText(String.format(
                    "Xin chào,\n\nTài khoản HRMS của bạn đã được tạo thành công.\n" +
                            "Tên đăng nhập: %s\n" +
                            "Mật khẩu tạm thời: %s\n\n" +
                            "Vui lòng đăng nhập và đổi mật khẩu ngay.\n\nTrân trọng.",
                    username, password
            ));
            mailSender.send(message);
        } catch (Exception e) {
            throw new HRMSAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi gửi email tài khoản: " + e.getMessage());
        }
    }
}
