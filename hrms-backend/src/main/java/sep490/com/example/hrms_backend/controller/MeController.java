package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.PermissionMapper;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.service.PermissionRegistrationService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final CurrentUserUtils currentUserUtils;
    private final AccountRepository accountRepository;
    private final PermissionRegistrationService permissionRegistrationService;

    @GetMapping("/permissions")
    public ResponseEntity<Set<PermissionDTO>> getCurrentUserPermissions() {
        String username = currentUserUtils.getCurrentUsername();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new HRMSAPIException("Không tìm thấy tài khoản người dùng hiện tại"));

        Role role = account.getRole();
        if (role == null || role.getPermissions() == null) {
            throw new HRMSAPIException("Người dùng chưa được gán vai trò hoặc quyền.");
        }

        Set<PermissionDTO> permissions = role.getPermissions().stream()
                .map(PermissionMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(permissions);
    }
}
