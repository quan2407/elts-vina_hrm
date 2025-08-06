package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.AccountRequest;

import java.util.List;
import java.util.Optional;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {

    Optional<AccountRequest> findByEmployee_EmployeeId(Long employeeId);

    List<AccountRequest> findByApprovedIsNull();
    List<AccountRequest> findByApproved(Boolean approved);

}
