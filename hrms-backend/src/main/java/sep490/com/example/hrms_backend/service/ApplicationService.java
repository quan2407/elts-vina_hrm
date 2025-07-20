package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.ApplicationCreateDTO;

public interface ApplicationService {
    void createApplication(ApplicationCreateDTO dto, Long employeeId);
}
