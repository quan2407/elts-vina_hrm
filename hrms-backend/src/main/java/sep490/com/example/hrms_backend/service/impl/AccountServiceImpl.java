package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.dto.PasswordResetRequestDTO;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final PasswordResetRequestRepository passwordResetRequestRepository;

    @Override
    public Page<PasswordResetRequestDTO> getRequestsByFilter(
            String status,
            int page,
            int size,
            String search,
            String departmentName,
            String positionName,
            String lineName
    ) {
        List<PasswordResetRequest> all = passwordResetRequestRepository.findAll();

        String st = (status == null) ? "all" : status.toLowerCase();
        List<PasswordResetRequest> byStatus = switch (st) {
            case "approved" -> all.stream()
                    .filter(PasswordResetRequest::isApproved)
                    .toList();
            case "rejected" -> all.stream()
                    .filter(r -> !r.isApproved() && r.getApprovedAt() != null)
                    .toList();
            case "pending" -> all.stream()
                    .filter(r -> r.getApprovedAt() == null)
                    .toList();
            default -> all;
        };

        String q = (search == null || search.isBlank()) ? null : search.trim().toLowerCase();
        List<PasswordResetRequest> filtered = byStatus.stream()
                .filter(r -> {
                    if (q != null) {
                        boolean byCode = r.getEmployeeCode() != null && r.getEmployeeCode().toLowerCase().contains(q);
                        boolean byName = r.getEmployeeName() != null && r.getEmployeeName().toLowerCase().contains(q);
                        if (!(byCode || byName)) return false;
                    }
                    if (departmentName != null && !departmentName.equals(r.getDepartmentName())) return false;
                    if (positionName != null && !positionName.equals(r.getPositionName())) return false;
                    if (lineName != null && !lineName.equals(r.getLineName())) return false;
                    return true;
                })
                // requestedAt desc, null xuống cuối
                .sorted((a, b) -> {
                    var va = a.getRequestedAt();
                    var vb = b.getRequestedAt();
                    if (va == null && vb == null) return 0;
                    if (va == null) return 1;
                    if (vb == null) return -1;
                    return vb.compareTo(va);
                })
                .toList();
        List<PasswordResetRequestDTO> dtoList = filtered.stream()
                .map(req -> PasswordResetRequestDTO.builder()
                        .id(req.getId())
                        .employeeId(req.getEmployeeId())
                        .employeeCode(req.getEmployeeCode())
                        .employeeName(req.getEmployeeName())
                        .departmentName(req.getDepartmentName())
                        .positionName(req.getPositionName())
                        .lineName(req.getLineName())
                        .email(req.getEmail())
                        .requestedAt(req.getRequestedAt())
                        .approved(req.isApproved())
                        .build())
                .toList();

        int start = Math.min(page * size, dtoList.size());
        int end = Math.min(start + size, dtoList.size());
        List<PasswordResetRequestDTO> pageContent = dtoList.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(page, size), dtoList.size());
    }



    @Override
    public Page<AccountResponseDTO> getAllAccounts(int page, int size,
                                                   String search,
                                                   Long departmentId,
                                                   Long positionId,
                                                   Long lineId,
                                                   String role) {
        Pageable pageable = PageRequest.of(page, size);

        List<Account> all = accountRepository.findAll();
        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            all = all.stream().filter(a -> {
                Employee e = a.getEmployee();
                boolean m3 = e != null && e.getEmployeeCode() != null && e.getEmployeeCode().toLowerCase().contains(q);
                boolean m4 = e != null && e.getEmployeeName() != null && e.getEmployeeName().toLowerCase().contains(q);
                return m3 || m4;
            }).toList();
        }

        if (departmentId != null) {
            all = all.stream().filter(a ->
                    a.getEmployee() != null &&
                            a.getEmployee().getDepartment() != null &&
                            departmentId.equals(a.getEmployee().getDepartment().getDepartmentId())
            ).toList();
        }

        if (positionId != null) {
            all = all.stream().filter(a ->
                    a.getEmployee() != null &&
                            a.getEmployee().getPosition() != null &&
                            positionId.equals(a.getEmployee().getPosition().getPositionId())
            ).toList();
        }
        if (lineId != null) {
            all = all.stream().filter(a ->
                    a.getEmployee() != null &&
                            a.getEmployee().getLine() != null &&
                            lineId.equals(a.getEmployee().getLine().getLineId())
            ).toList();
        }
        if (role != null && !role.isBlank()) {
            String normalized = role.trim().toUpperCase();
            if (!normalized.startsWith("ROLE_")) normalized = "ROLE_" + normalized; // cho phép truyền "EMPLOYEE"
            final String want = normalized;
            all = all.stream().filter(a ->
                    a.getRole() != null && want.equals(a.getRole().getRoleName())
            ).toList();
        }
        List<AccountResponseDTO> dtos = all.stream()
                .map(acc -> AccountMapper.mapToAccountResponseDTO(acc, new AccountResponseDTO()))
                .toList();

        // phân trang thủ công (giống EmployeeServiceImpl)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        List<AccountResponseDTO> content = start >= dtos.size() ? List.of() : dtos.subList(start, end);

        return new PageImpl<>(content, pageable, dtos.size());
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

        Employee emp = account.getEmployee(); // có thể null, cần check

        PasswordResetRequest request = PasswordResetRequest.builder()
                .email(email)
                .approved(false)
                .requestedAt(LocalDateTime.now())

                // ▼▼ SNAPSHOT nhân viên (không FK) ▼▼
                .employeeId(emp != null ? emp.getEmployeeId() : null)
                .employeeCode(emp != null ? emp.getEmployeeCode() : null)
                .employeeName(emp != null ? emp.getEmployeeName() : null)
                .departmentName(
                        (emp != null && emp.getDepartment() != null)
                                ? emp.getDepartment().getDepartmentName() : null
                )
                .positionName(
                        (emp != null && emp.getPosition() != null)
                                ? emp.getPosition().getPositionName() : null
                )
                .lineName(
                        (emp != null && emp.getLine() != null)
                                ? emp.getLine().getLineName() : null
                )
                // ▲▲ SNAPSHOT ▲▲

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
