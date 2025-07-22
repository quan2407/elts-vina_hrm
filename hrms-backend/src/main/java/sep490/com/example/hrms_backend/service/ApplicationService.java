package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;

public interface ApplicationService {
    void createApplication(ApplicationCreateDTO dto, Long employeeId);

    Page<ApplicationListItemDTO> getApplicationsForEmployee(Long employeeId, ApplicationStatus status, Pageable pageable);

    ApplicationDetailDTO getApplicationDetail(Long id);
    void updateApplication(Long id, ApplicationCreateDTO dto, Long employeeId);


    void approveStep1(Long id, Long approverId, ApplicationApprovalRequestDTO request);

    Page<ApplicationApprovalListItemDTO> getStep1Applications(ApplicationStatus status, PageRequest of);
}
