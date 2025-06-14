package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.dto.AccountResponseDTO;

import java.util.stream.Collectors;

public class AccountMapper {

    public static AccountResponseDTO mapToAccountResponseDTO(Account account, AccountResponseDTO dto) {
        dto.setAccountId(account.getAccountId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setIsActive(account.getIsActive());
        dto.setLastLoginAt(account.getLastLoginAt());

        if (account.getRoles() != null) {
            dto.setRoles(
                    account.getRoles().stream()
                            .map(role -> role.getRoleName())
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

}
