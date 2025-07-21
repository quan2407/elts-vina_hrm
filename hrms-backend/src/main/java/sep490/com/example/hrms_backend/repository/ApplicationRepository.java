package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Application;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByEmployee_EmployeeIdAndStatus(Long employeeId, ApplicationStatus status, Pageable pageable);
    Page<Application> findByEmployee_EmployeeId(Long employeeId, Pageable pageable);


}
