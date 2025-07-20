package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.ApplicationCreateDTO;
import sep490.com.example.hrms_backend.entity.Application;
import sep490.com.example.hrms_backend.entity.ApplicationApprovalStep;
import sep490.com.example.hrms_backend.entity.ApplicationType;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.repository.ApplicationApprovalStepRepository;
import sep490.com.example.hrms_backend.repository.ApplicationRepository;
import sep490.com.example.hrms_backend.repository.ApplicationTypeRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.service.ApplicationService;

import java.time.LocalDateTime;

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
                .status("PENDING")
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
                .status("PENDING")
                .build();
    }

}
