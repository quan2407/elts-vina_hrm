package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.ApplicationCreateDTO;
import sep490.com.example.hrms_backend.dto.ApplicationListItemDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.ApplicationService;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationTypeRepository applicationTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplicationApprovalStepRepository approvalStepRepository;

    @Override
    public void createApplication(ApplicationCreateDTO dto, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        ApplicationType type = applicationTypeRepository.findById(dto.getApplicationTypeId())
                .orElseThrow(() -> new RuntimeException("Application type not found"));

        if (type.getName().equalsIgnoreCase("Nghỉ phép") && dto.getLeaveCode() == null) {
            throw new IllegalArgumentException("Leave code is required for leave application");
        }

        Application application = Application.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .leaveCode(dto.getLeaveCode())
                .isHalfDay(dto.getIsHalfDay())
                .halfDayType(dto.getHalfDayType())
                .applicationType(type)
                .employee(employee)
                .attachmentPath(dto.getAttachmentPath())
                .status(ApplicationStatus.PENDING_MANAGER_APPROVAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
        ApplicationApprovalStep step1 = createInitialApprovalStep(application);
        approvalStepRepository.save(step1);
    }

    private ApplicationApprovalStep createInitialApprovalStep(Application application) {
        return ApplicationApprovalStep.builder()
                .application(application)
                .approver(null)
                .step(1)
                .status(ApprovalStepStatus.PENDING)
                .build();
    }

    @Override
    public Page<ApplicationListItemDTO> getApplicationsForEmployee(Long employeeId, Pageable pageable) {
        Page<Application> applications = applicationRepository.findByEmployee_EmployeeId(employeeId, pageable);

        return new PageImpl<>(
                applications.getContent().stream().map(this::toListItemDTO).collect(Collectors.toList()),
                pageable,
                applications.getTotalElements()
        );
    }


    private ApplicationListItemDTO toListItemDTO(Application app) {
        return ApplicationListItemDTO.builder()
                .id(app.getId())
                .title(app.getTitle())
                .content(app.getContent())
                .startDate(app.getStartDate())
                .endDate(app.getEndDate())
                .status(app.getStatus())
                .statusLabel(app.getStatus().getLabel())
                .createdAt(app.getCreatedAt())
                .applicationTypeName(app.getApplicationType().getName())
                .build();
    }
}
