package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.PasswordResetRequest;

public interface AccountService {

    Page<AccountResponseDTO> getAllAccounts(int page, int size);
    void createAutoAccountForEmployee(Employee employee);
    void resetPasswordByEmail(String email);
    void changePassword(ChangePasswordRequest dto);
    void requestResetPassword(String email);
    void approveResetPassword(String email);


    Page<PasswordResetRequest> getPendingResetRequests(int page, int size);

    void toggleAccountStatus(Long id);
}
