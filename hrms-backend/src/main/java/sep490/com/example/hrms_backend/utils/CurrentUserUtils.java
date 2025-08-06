package sep490.com.example.hrms_backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;

@Component
@RequiredArgsConstructor
public class CurrentUserUtils {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;


    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null ||
                authentication.getName().isBlank()) {
            throw new HRMSAPIException("Username is missing or empty!");
        }

        return authentication.getName();
    }

    public Long getCurrentEmployeeId() {
        String username = getCurrentUsername();
        return accountRepository.findByUsername(username)
                .map(account -> {
                    if (account.getEmployee() == null) {
                        throw new HRMSAPIException("Tài khoản không gắn với nhân viên nào!");
                    }
                    return account.getEmployee().getEmployeeId();
                })
                .orElseThrow(() -> new HRMSAPIException("Tài khoản không tồn tại!"));
    }
    public String getCurrentEmployeeName() {
        Long employeeId = getCurrentEmployeeId();

        return employeeRepository.findById(employeeId)
                .map(employee -> employee.getEmployeeName())
                .orElseThrow(() -> new HRMSAPIException("Không tìm thấy thông tin nhân viên hiện tại!"));
    }
}

