package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.PasswordResetRequest;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.AccountMapper;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.PasswordResetRequestRepository;
import sep490.com.example.hrms_backend.repository.RoleRepository;
import sep490.com.example.hrms_backend.service.AccountService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final PasswordResetRequestRepository passwordResetRequestRepository;

    @Override
    public List<PasswordResetRequest> getPendingResetRequests() {
        return passwordResetRequestRepository.findAll()
                .stream()
                .filter(r -> !r.isApproved())
                .sorted(Comparator.comparing(PasswordResetRequest::getRequestedAt).reversed())
                .collect(Collectors.toList());
    }

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
                .loginAttempts(5)
                .mustChangePassword(true)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        sendPasswordEmail(email, username, rawPassword, "CREATE");
    }
    @Override
    @Transactional
    public void resetPasswordByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.BAD_REQUEST, "Không tìm thấy tài khoản với email: " + email));

        String rawPassword = generateRandomPassword(8);
        String hashedPassword = passwordEncoder.encode(rawPassword);

        account.setPasswordHash(hashedPassword);
        account.setMustChangePassword(true);
        account.setLoginAttempts(5);
        accountRepository.save(account);

        sendPasswordEmail(account.getEmail(), account.getUsername(), rawPassword, "RESET");
    }
    @Override
    @Transactional
    public void requestResetPassword(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.BAD_REQUEST, "Không tìm thấy tài khoản với email: " + email));

        if (passwordResetRequestRepository.findByEmailAndApprovedFalse(email).isPresent()) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Yêu cầu reset mật khẩu đã tồn tại và đang chờ duyệt");
        }

        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(email)
                .approved(false)
                .requestedAt(LocalDateTime.now())
                .build();

        passwordResetRequestRepository.save(request);
    }
    @Override
    @Transactional
    public void approveResetPassword(String email) {
        PasswordResetRequest request = passwordResetRequestRepository.findByEmailAndApprovedFalse(email)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.BAD_REQUEST, "Không có yêu cầu reset nào đang chờ duyệt cho email này"));

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.BAD_REQUEST, "Không tìm thấy tài khoản với email: " + email));

        String rawPassword = generateRandomPassword(8);
        String hashedPassword = passwordEncoder.encode(rawPassword);

        account.setPasswordHash(hashedPassword);
        account.setMustChangePassword(true);
        account.setLoginAttempts(5);
        accountRepository.save(account);

        request.setApproved(true);
        request.setApprovedAt(LocalDateTime.now());
        passwordResetRequestRepository.save(request);

        sendPasswordEmail(email, account.getUsername(), rawPassword, "RESET");
    }




    private String generateRandomPassword(int length) {
        if (length < 8) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Mật khẩu phải có ít nhất 8 ký tự.");
        }

        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";

        String password = RandomStringUtils.random(1, lowerCase) +
                RandomStringUtils.random(1, upperCase) +
                RandomStringUtils.random(1, digits);


        String allCharacters = lowerCase + upperCase + digits;
        password += RandomStringUtils.random(length - password.length(), allCharacters);


        List<Character> passwordList = new ArrayList<>();
        for (char c : password.toCharArray()) {
            passwordList.add(c);
        }


        Collections.shuffle(passwordList);

        StringBuilder shuffledPassword = new StringBuilder();
        for (char c : passwordList) {
            shuffledPassword.append(c);
        }

        return shuffledPassword.toString();
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

    private void sendPasswordEmail(String to, String username, String password, String type) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);

            if ("CREATE".equals(type)) {
                message.setSubject("Tài khoản HRMS của bạn đã được tạo");
                message.setText(String.format(
                        "Xin chào %s,\n\n" +
                                "Tài khoản HRMS của bạn đã được khởi tạo thành công.\n\n" +
                                "🔹 Tên đăng nhập: %s\n" +
                                "🔹 Mật khẩu tạm thời: %s\n\n" +
                                "Vui lòng đăng nhập và đổi mật khẩu ngay.\n\n" +
                                "Trân trọng,\nPhòng Hành chính - Nhân sự",
                        username, username, password
                ));
            } else if ("RESET".equals(type)) {
                message.setSubject("Yêu cầu reset mật khẩu HRMS");
                message.setText(String.format(
                        "Xin chào %s,\n\n" +
                                "Mật khẩu mới cho tài khoản HRMS của bạn đã được tạo.\n\n" +
                                "🔹 Tên đăng nhập: %s\n" +
                                "🔹 Mật khẩu mới: %s\n\n" +
                                "Vui lòng đăng nhập và đổi mật khẩu ngay để đảm bảo bảo mật.\n\n" +
                                "Trân trọng,\nPhòng Hành chính - Nhân sự",
                        username, username, password
                ));
            }

            mailSender.send(message);
        } catch (Exception e) {
            throw new HRMSAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi gửi email tài khoản: " + e.getMessage());
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(dto.getOldPassword(), account.getPasswordHash())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không chính xác");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Xác nhận mật khẩu mới không khớp");
        }

        account.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        accountRepository.save(account);
    }

    public void toggleAccountStatus(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        account.setIsActive(!Boolean.TRUE.equals(account.getIsActive()));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

}
