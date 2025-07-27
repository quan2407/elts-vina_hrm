package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.ApplicationType;

public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, Long> {
}
