package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sep490.com.example.hrms_backend.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsernameOrEmail(String username, String email);

    Optional<Account> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<Account> findByEmployee_EmployeeId(Long employeeId);

    @Query("""
                SELECT a FROM Account a
                JOIN FETCH a.role r
                JOIN FETCH r.permissions
                WHERE a.username = :username
            """)
    Optional<Account> findByUsernameWithPermissions(String username);
    @EntityGraph(attributePaths = {
            "role",
            "employee",
            "employee.position",
            "employee.department",
            "employee.line"
    })
    Page<Account> findAll(Pageable pageable);

}
