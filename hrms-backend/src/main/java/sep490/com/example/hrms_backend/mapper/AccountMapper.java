package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.entity.Account;

public class AccountMapper {

    public static AccountResponseDTO mapToAccountResponseDTO(Account account, AccountResponseDTO dto) {
        dto.setAccountId(account.getAccountId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setIsActive(account.getIsActive());
        dto.setLastLoginAt(account.getLastLoginAt());

        if (account.getRole() != null) {
            dto.setRole(account.getRole().getRoleName());
        }

        return dto;
    }
}

