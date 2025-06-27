package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByCitizenId(String citizenId);

    boolean existsByCitizenIdAndEmployeeIdNot(String citizenId, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndEmployeeIdNot(String email, Long id);

    Optional<Employee> findByAccount_Username(String username);
    List<Employee> findByIsDeletedFalse();
    Optional<Employee> findByEmployeeIdAndIsDeletedFalse(Long employeeId);



}
