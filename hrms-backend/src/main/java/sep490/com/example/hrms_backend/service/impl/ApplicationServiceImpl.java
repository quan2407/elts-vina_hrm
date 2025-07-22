package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.ApplicationCreateDTO;
import sep490.com.example.hrms_backend.dto.ApplicationDetailDTO;
import sep490.com.example.hrms_backend.dto.ApplicationListItemDTO;
import sep490.com.example.hrms_backend.dto.ApprovalStepDTO;
import sep490.com.example.hrms_backend.entity.Application;
import sep490.com.example.hrms_backend.entity.ApplicationApprovalStep;
import sep490.com.example.hrms_backend.entity.ApplicationType;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;
import sep490.com.example.hrms_backend.repository.ApplicationApprovalStepRepository;
import sep490.com.example.hrms_backend.repository.ApplicationRepository;
import sep490.com.example.hrms_backend.repository.ApplicationTypeRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.service.ApplicationService;

import java.time.LocalDateTime;
import java.util.List;
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
    public Page<ApplicationListItemDTO> getApplicationsForEmployee(Long employeeId, ApplicationStatus status, Pageable pageable) {
        Page<Application> applications;

        if (status != null) {
            applications = applicationRepository.findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable);
        } else {
            applications = applicationRepository.findByEmployee_EmployeeId(employeeId, pageable);
        }

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
    @Override
    public ApplicationDetailDTO getApplicationDetail(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        List<ApprovalStepDTO> stepDTOs = application.getApprovalSteps().stream()
                .map(step -> ApprovalStepDTO.builder()
                        .step(step.getStep())
                        .approverName(step.getApprover() != null ? step.getApprover().getEmployeeName() : null)
                        .status(step.getStatus())
                        .note(step.getNote())
                        .approvedAt(step.getApprovedAt())
                        .build())
                .collect(Collectors.toList());

        return ApplicationDetailDTO.builder()
                .id(application.getId())
                .title(application.getTitle())
                .content(application.getContent())
                .startDate(application.getStartDate())
                .endDate(application.getEndDate())
                .applicationTypeId(application.getApplicationType().getId())
                .applicationTypeName(application.getApplicationType().getName())
                .status(application.getStatus())
                .statusLabel(application.getStatus().getLabel())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .attachmentPath(application.getAttachmentPath())
                .rejectReason(application.getRejectReason())
                .approvalSteps(stepDTOs)
                .build();
    }
    @Override
    public void updateApplication(Long id, ApplicationCreateDTO dto, Long employeeId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Bạn không có quyền sửa đơn này");
        }

        if (application.getStatus() != ApplicationStatus.PENDING_MANAGER_APPROVAL) {
            throw new RuntimeException("Chỉ được sửa khi đơn đang chờ quản lý duyệt");
        }

        ApplicationType type = applicationTypeRepository.findById(dto.getApplicationTypeId())
                .orElseThrow(() -> new RuntimeException("Application type not found"));

        if (type.getName().equalsIgnoreCase("Nghỉ phép") && dto.getLeaveCode() == null) {
            throw new IllegalArgumentException("Leave code is required for leave application");
        }

        application.setTitle(dto.getTitle());
        application.setContent(dto.getContent());
        application.setStartDate(dto.getStartDate());
        application.setEndDate(dto.getEndDate());
        application.setLeaveCode(dto.getLeaveCode());
        application.setIsHalfDay(dto.getIsHalfDay());
        application.setHalfDayType(dto.getHalfDayType());
        application.setAttachmentPath(dto.getAttachmentPath());
        application.setApplicationType(type);
        application.setUpdatedAt(LocalDateTime.now());

        applicationRepository.save(application);
    }

}
