package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.Application;
import sep490.com.example.hrms_backend.entity.ApplicationApprovalStep;
import sep490.com.example.hrms_backend.entity.ApplicationType;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.ApplicationService;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;
import sep490.com.example.hrms_backend.validation.ApplicationValidator;

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
    private final CurrentUserUtils currentUserUtils;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceRecordService attendanceRecordService;
    private final ApplicationValidator applicationValidator;

    @Override
    public void createApplication(ApplicationCreateDTO dto, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        ApplicationType type = applicationTypeRepository.findById(dto.getApplicationTypeId())
                .orElseThrow(() -> new RuntimeException("Application type not found"));

        applicationValidator.validate(dto, type.getName());

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
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Xác định người tạo đơn
        Long creatorId = currentUserUtils.getCurrentEmployeeId();
        Employee creator = employeeRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Người tạo không tồn tại"));

        String position = creator.getPosition() != null
                ? creator.getPosition().getPositionName().toLowerCase()
                : "";

        if (position.contains("hr")) {
            application.setStatus(ApplicationStatus.HR_APPROVED);

            ApplicationApprovalStep step2 = ApplicationApprovalStep.builder()
                    .application(application)
                    .step(2)
                    .status(ApprovalStepStatus.APPROVED)
                    .approver(creator)
                    .approvedAt(LocalDateTime.now())
                    .note("HR tạo và duyệt đơn")
                    .build();

            applicationRepository.save(application); // ✅ Sau khi set status
            approvalStepRepository.save(step2);
            if (application.getCheckIn() != null && application.getCheckOut() != null) {
                attendanceRecordRepository.findByEmployee_EmployeeIdAndDate(
                        application.getEmployee().getEmployeeId(),
                        application.getStartDate()
                ).ifPresent(record -> {
                    AttendanceCheckInOutDTO updateDto = new AttendanceCheckInOutDTO();
                    updateDto.setCheckIn(String.valueOf(application.getCheckIn()));
                    updateDto.setCheckOut(String.valueOf(application.getCheckOut()));
                    attendanceRecordService.updateCheckInOut(record.getId(), updateDto);
                });
            }
        } else if (position.contains("quản lý sản xuất") || position.contains("production manager")) {
            application.setStatus(ApplicationStatus.MANAGER_APPROVED);

            ApplicationApprovalStep step1 = ApplicationApprovalStep.builder()
                    .application(application)
                    .step(1)
                    .status(ApprovalStepStatus.APPROVED)
                    .approver(creator)
                    .approvedAt(LocalDateTime.now())
                    .note("QLSX tạo và duyệt đơn")
                    .build();

            ApplicationApprovalStep step2 = ApplicationApprovalStep.builder()
                    .application(application)
                    .step(2)
                    .status(ApprovalStepStatus.PENDING)
                    .build();

            applicationRepository.save(application); // ✅ Sau khi set status
            approvalStepRepository.save(step1);
            approvalStepRepository.save(step2);

        } else {
            application.setStatus(ApplicationStatus.PENDING_MANAGER_APPROVAL);

            applicationRepository.save(application); // ✅ Sau khi set status
            ApplicationApprovalStep step1 = createInitialApprovalStep(application);
            approvalStepRepository.save(step1);
        }

        application.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(application); // cập nhật lại nếu cần
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

        Employee emp = application.getEmployee();

        List<ApprovalStepDTO> stepDTOs = application.getApprovalSteps().stream()
                .map(step -> ApprovalStepDTO.builder()
                        .step(step.getStep())
                        .approverName(step.getApprover() != null ? step.getApprover().getEmployeeName() : null)
                        .status(step.getStatus())
                        .note(step.getNote())
                        .approvedAt(step.getApprovedAt())
                        .build())
                .collect(Collectors.toList());
        Long currentEmployeeId = currentUserUtils.getCurrentEmployeeId();
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
                .leaveCode(application.getLeaveCode() != null ? application.getLeaveCode().name() : null)
                .isHalfDay(application.getIsHalfDay())
                .halfDayType(application.getHalfDayType() != null ? application.getHalfDayType().name() : null)
                .checkIn(application.getCheckIn())
                .checkOut(application.getCheckOut())
                .employeeId(String.valueOf(emp.getEmployeeId()))
                .employeeCode(emp.getEmployeeCode())
                .employeeName(emp.getEmployeeName())
                .positionName(emp.getPosition() != null ? emp.getPosition().getPositionName() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : null)
                .lineName(emp.getLine() != null ? emp.getLine().getLineName() : null)
                .isCreator(emp.getEmployeeId().equals(currentEmployeeId))
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

        applicationValidator.validate(dto, type.getName());

        if (dto.getAttachmentPath() == null) {
            dto.setAttachmentPath(application.getAttachmentPath());
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
        application.setCheckIn(dto.getCheckIn());
        application.setCheckOut(dto.getCheckOut());

        applicationRepository.save(application);
    }


    @Override
    @Transactional
    public void approveStep1(Long applicationId, Long approverId, ApplicationApprovalRequestDTO request) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        ApplicationApprovalStep step1 = app.getApprovalSteps().stream()
                .filter(s -> s.getStep() == 1 && s.getApprover() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bước phê duyệt phù hợp với bạn"));

        if (step1.getStatus() != ApprovalStepStatus.PENDING) {
            throw new RuntimeException("Bước đã được xử lý");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người duyệt"));
        step1.setApprover(approver);

        // Ghi nhận kết quả xử lý
        step1.setStatus(request.isApproved() ? ApprovalStepStatus.APPROVED : ApprovalStepStatus.REJECTED);
        step1.setNote(request.getNote());
        step1.setApprovedAt(LocalDateTime.now());

        if (request.isApproved()) {
            app.setStatus(ApplicationStatus.MANAGER_APPROVED);

            ApplicationApprovalStep step2 = ApplicationApprovalStep.builder()
                    .application(app)
                    .step(2)
                    .status(ApprovalStepStatus.PENDING)
                    .approver(null)
                    .build();
            app.getApprovalSteps().add(step2);
        } else {
            app.setStatus(ApplicationStatus.MANAGER_REJECTED);
            app.setRejectReason(request.getNote());
        }

        app.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app);
    }

    @Override
    public Page<ApplicationApprovalListItemDTO> getStep1Applications(ApplicationStatus status, PageRequest of) {
        Page<ApplicationApprovalStep> steps;

        if (status != null) {
            ApprovalStepStatus stepStatus = switch (status) {
                case PENDING_MANAGER_APPROVAL -> ApprovalStepStatus.PENDING;
                case MANAGER_APPROVED -> ApprovalStepStatus.APPROVED;
                case MANAGER_REJECTED -> ApprovalStepStatus.REJECTED;
                default -> throw new RuntimeException("Trạng thái không hợp lệ");
            };

            steps = approvalStepRepository.findByStepAndStatus(1, stepStatus, of);
        } else {
            steps = approvalStepRepository.findByStep(1, of);
        }

        List<ApplicationApprovalListItemDTO> dtos = steps.stream()
                .map(step -> toApprovalListItemDTO(step.getApplication()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, of, steps.getTotalElements());
    }
    private ApplicationApprovalListItemDTO toApprovalListItemDTO(Application app) {
        Employee emp = app.getEmployee();
        return ApplicationApprovalListItemDTO.builder()
                .id(app.getId())
                .title(app.getTitle())
                .content(app.getContent())
                .startDate(app.getStartDate())
                .endDate(app.getEndDate())
                .applicationTypeName(app.getApplicationType().getName())
                .status(app.getStatus())
                .statusLabel(app.getStatus().getLabel())
                .createdAt(app.getCreatedAt())
                .employeeCode(emp.getEmployeeCode())
                .employeeName(emp.getEmployeeName())
                .positionName(emp.getPosition() != null ? emp.getPosition().getPositionName() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : null)
                .lineName(emp.getLine() != null ? emp.getLine().getLineName() : null)
                .build();
    }
    @Override
    @Transactional
    public void approveStep2(Long applicationId, Long approverId, ApplicationApprovalRequestDTO request) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        ApplicationApprovalStep step2 = app.getApprovalSteps().stream()
                .filter(s -> s.getStep() == 2 && s.getApprover() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bước phê duyệt phù hợp với bạn"));

        if (step2.getStatus() != ApprovalStepStatus.PENDING) {
            throw new RuntimeException("Bước đã được xử lý");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người duyệt"));
        step2.setApprover(approver);

        step2.setStatus(request.isApproved() ? ApprovalStepStatus.APPROVED : ApprovalStepStatus.REJECTED);
        step2.setNote(request.getNote());
        step2.setApprovedAt(LocalDateTime.now());

        if (request.isApproved()) {
            app.setStatus(ApplicationStatus.HR_APPROVED);
            if (app.getCheckIn() != null && app.getCheckOut() != null) {
                attendanceRecordRepository.findByEmployee_EmployeeIdAndDate(app.getEmployee().getEmployeeId(), app.getStartDate())
                        .ifPresent(record -> {
                            AttendanceCheckInOutDTO updateDto = new AttendanceCheckInOutDTO();
                            updateDto.setCheckIn(String.valueOf(app.getCheckIn()));
                            updateDto.setCheckOut(String.valueOf(app.getCheckOut()));
                            attendanceRecordService.updateCheckInOut(record.getId(), updateDto);
                        });
            }


        } else {
            app.setStatus(ApplicationStatus.HR_REJECTED);
            app.setRejectReason(request.getNote());
        }

        app.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app);
    }

    @Override
    public Page<ApplicationApprovalListItemDTO> getStep2Applications(ApplicationStatus status, PageRequest pageRequest) {
        Page<ApplicationApprovalStep> steps;

        if (status != null) {
            ApprovalStepStatus stepStatus = switch (status) {
                case HR_APPROVED -> ApprovalStepStatus.APPROVED;
                case HR_REJECTED -> ApprovalStepStatus.REJECTED;
                case MANAGER_APPROVED -> ApprovalStepStatus.PENDING;
                default -> throw new RuntimeException("Trạng thái không hợp lệ");
            };

            steps = approvalStepRepository.findByStepAndStatus(2, stepStatus, pageRequest);
        } else {
            steps = approvalStepRepository.findByStep(2, pageRequest);
        }

        List<ApplicationApprovalListItemDTO> dtos = steps.stream()
                .map(step -> toApprovalListItemDTO(step.getApplication()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageRequest, steps.getTotalElements());
    }



}
