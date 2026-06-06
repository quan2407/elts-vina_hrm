package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.AccountRequest;

import java.util.Optional;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {

    Optional<AccountRequest> findByEmployee_EmployeeId(Long employeeId);

    @EntityGraph(attributePaths = {
            "employee",
            "employee.position",
            "employee.department",
            "employee.line"
    })
    Page<AccountRequest> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
            "employee",
            "employee.position",
            "employee.department",
            "employee.line"
    })
    Page<AccountRequest> findByApproved(Boolean approved, Pageable pageable);

    @EntityGraph(attributePaths = {
            "employee",
            "employee.position",
            "employee.department",
            "employee.line"
    })
    Page<AccountRequest> findByApprovedIsNull(Pageable pageable);
}
