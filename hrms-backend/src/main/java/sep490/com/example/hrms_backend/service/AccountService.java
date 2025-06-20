package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.entity.Employee;

import java.util.List;

public interface AccountService {

    /**
     * Lấy danh sách toàn bộ tài khoản (dành cho Admin xem)
     */
    List<AccountResponseDTO> getAllAccounts();
    void createAutoAccountForEmployee(Employee employee);

}
