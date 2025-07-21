package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sep490.com.example.hrms_backend.dto.ApplicationCreateDTO;
import sep490.com.example.hrms_backend.dto.ApplicationListItemDTO;

public interface ApplicationService {
    void createApplication(ApplicationCreateDTO dto, Long employeeId);

    Page<ApplicationListItemDTO> getApplicationsForEmployee(Long employeeId, Pageable pageable);
}
