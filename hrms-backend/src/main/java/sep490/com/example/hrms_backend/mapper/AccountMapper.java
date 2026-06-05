package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.AccountResponseDTO;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Employee;

public class AccountMapper {


    public static AccountResponseDTO mapToAccountResponseDTO(Account account, AccountResponseDTO target) {
        if (target == null) target = new AccountResponseDTO();

        target.setAccountId(account.getAccountId());
        target.setUsername(account.getUsername());
        target.setEmail(account.getEmail());
        target.setIsActive(account.getIsActive());
        target.setLastLoginAt(account.getLastLoginAt());
        target.setRole(account.getRole() != null ? account.getRole().getRoleName() : null);

        Employee e = account.getEmployee();
        if (e != null) {
            target.setEmployeeId(e.getEmployeeId());
            target.setEmployeeCode(e.getEmployeeCode());
            target.setEmployeeName(e.getEmployeeName());
            target.setPositionName(e.getPosition() != null ? e.getPosition().getPositionName() : null);
            target.setDepartmentName(e.getDepartment() != null ? e.getDepartment().getDepartmentName() : null);
            target.setLineName(e.getLine() != null ? e.getLine().getLineName() : null);
        }

        return target;
    }
}

