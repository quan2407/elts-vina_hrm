package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.dto.PasswordResetRequestDTO;
import sep490.com.example.hrms_backend.entity.Employee;

public interface AccountService {

    Page<AccountResponseDTO> getAllAccounts(int page, int size,
                                            String search,
                                            Long departmentId,
                                            Long positionId,
                                            Long lineId,
                                            String role);
    void createAutoAccountForEmployee(Employee employee);
    void resetPasswordByEmail(String email);
    void changePassword(ChangePasswordRequest dto);
    void requestResetPassword(String email);
    void approveResetPassword(String email);


    void toggleAccountStatus(Long id);

    Page<PasswordResetRequestDTO> getRequestsByFilter(String status, int page, int size, String search, String department, String position, String line);
}