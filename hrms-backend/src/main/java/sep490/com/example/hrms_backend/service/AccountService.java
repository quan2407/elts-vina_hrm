package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.dto.ChangePasswordRequest;
import sep490.com.example.hrms_backend.entity.Employee;

import java.util.List;

public interface AccountService {

    List<AccountResponseDTO> getAllAccounts();
    void createAutoAccountForEmployee(Employee employee);
    void resetPasswordByEmail(String email);
    void changePassword(ChangePasswordRequest dto);

}
