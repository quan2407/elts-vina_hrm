package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsernameOrEmail(String username, String email);

    Optional<Account> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Account> findByEmployee_EmployeeId(Long employeeId);

}
