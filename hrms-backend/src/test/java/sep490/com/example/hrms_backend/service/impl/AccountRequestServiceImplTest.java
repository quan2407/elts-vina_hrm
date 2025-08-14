package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.AccountService;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;
import sep490.com.example.hrms_backend.validation.ApplicationValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountRequestServiceImplTest {
    // 1. Mock tất cả các dependency
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ApplicationTypeRepository applicationTypeRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ApplicationApprovalStepRepository approvalStepRepository;
    @Mock
    private CurrentUserUtils currentUserUtils;
    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;
    @Mock
    private AttendanceRecordService attendanceRecordService;
    @Mock
    private ApplicationValidator applicationValidator;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private ApplicationServiceImpl applicationService;

    // 3. Khai báo dữ liệu mẫu
    private Employee employee;
    private Employee creatorEmployee;
    private Employee creatorManager;
    private Employee creatorHr;
    private ApplicationType applicationType;
    private ApplicationCreateDTO createDTO;
    private Position positionEmployee, positionManager, positionHr;
    private Application application;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        positionEmployee = new Position();
        positionEmployee.setPositionName("Nhân viên");

        positionManager = new Position();
        positionManager.setPositionName("Quản lý sản xuất");

        positionHr = new Position();
        positionHr.setPositionName("Chuyên viên nhân sự (hr)");

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeName("Nhân viên A");
        employee.setPosition(positionEmployee);

        creatorEmployee = new Employee();
        creatorEmployee.setEmployeeId(1L);
        creatorEmployee.setEmployeeName("Nhân viên A");
        creatorEmployee.setPosition(positionEmployee);

        creatorManager = new Employee();
        creatorManager.setEmployeeId(2L);
        creatorManager.setEmployeeName("Quản lý B");
        creatorManager.setPosition(positionManager);

        creatorHr = new Employee();
        creatorHr.setEmployeeId(3L);
        creatorHr.setEmployeeName("Nhân sự C");
        creatorHr.setPosition(positionHr);

        applicationType = new ApplicationType();
        applicationType.setId(10L);
        applicationType.setName("Đơn xin nghỉ phép");

        application = new Application();
        application.setId(100L);
        application.setTitle("Đơn xin nghỉ phép");
        application.setApplicationType(applicationType);
        application.setStatus(ApplicationStatus.PENDING_MANAGER_APPROVAL);
        application.setCreatedAt(java.time.LocalDateTime.now());
        application.setEmployee(employee);
        application.setApprovalSteps(new ArrayList<>());


        createDTO = new ApplicationCreateDTO();
        createDTO.setApplicationTypeId(10L);
        createDTO.setTitle("Xin nghỉ phép");
        createDTO.setContent("Nghỉ vì lý do cá nhân");
        createDTO.setStartDate(LocalDate.now().plusDays(5));
        createDTO.setEndDate(LocalDate.now().plusDays(5));
    }

    //================================================================================
    // Test cho phương thức createApplication
    //================================================================================

    @Test
    @DisplayName("Test createApplication - Tạo bởi nhân viên thường, trạng thái PENDING_MANAGER_APPROVAL")
    void createApplication_ByRegularEmployee_ShouldBePendingManagerApproval() {
        // Arrange
        Long employeeId = 1L;
        Long creatorId = 1L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), anyString());
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(creatorId);
        when(employeeRepository.findById(creatorId)).thenReturn(Optional.of(creatorEmployee));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        ArgumentCaptor<ApplicationApprovalStep> stepCaptor = ArgumentCaptor.forClass(ApplicationApprovalStep.class);

        // Act
        applicationService.createApplication(createDTO, employeeId);

        // Assert & Verify
        verify(applicationRepository, atLeastOnce()).save(applicationCaptor.capture());
        verify(approvalStepRepository, times(1)).save(stepCaptor.capture());

        Application savedApp = applicationCaptor.getValue();
        assertEquals(ApplicationStatus.PENDING_MANAGER_APPROVAL, savedApp.getStatus());

        ApplicationApprovalStep savedStep = stepCaptor.getValue();
        assertEquals(1, savedStep.getStep());
        assertEquals(ApprovalStepStatus.PENDING, savedStep.getStatus());
        assertNull(savedStep.getApprover());
    }

    @Test
    @DisplayName("Test createApplication - Tạo bởi Quản lý sản xuất, trạng thái MANAGER_APPROVED")
    void createApplication_ByProductionManager_ShouldBeManagerApproved() {
        // Arrange
        Long employeeId = 1L;
        Long creatorId = 2L; // Manager ID

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), anyString());
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(creatorId);
        when(employeeRepository.findById(creatorId)).thenReturn(Optional.of(creatorManager));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        ArgumentCaptor<ApplicationApprovalStep> stepCaptor = ArgumentCaptor.forClass(ApplicationApprovalStep.class);

        // Act
        applicationService.createApplication(createDTO, employeeId);

        // Assert & Verify
        verify(applicationRepository, atLeastOnce()).save(applicationCaptor.capture());
        // Phải lưu 2 bước
        verify(approvalStepRepository, times(2)).save(stepCaptor.capture());

        Application savedApp = applicationCaptor.getValue();
        assertEquals(ApplicationStatus.MANAGER_APPROVED, savedApp.getStatus());

        List<ApplicationApprovalStep> savedSteps = stepCaptor.getAllValues();
        ApplicationApprovalStep step1 = savedSteps.stream().filter(s -> s.getStep() == 1).findFirst().orElse(null);
        ApplicationApprovalStep step2 = savedSteps.stream().filter(s -> s.getStep() == 2).findFirst().orElse(null);

        assertNotNull(step1);
        assertEquals(ApprovalStepStatus.APPROVED, step1.getStatus());
        assertEquals(creatorManager, step1.getApprover());

        assertNotNull(step2);
        assertEquals(ApprovalStepStatus.PENDING, step2.getStatus());
    }

    @Test
    @DisplayName("Test createApplication - Tạo bởi HR, trạng thái HR_APPROVED và cập nhật chấm công")
    void createApplication_ByHr_ShouldBeHrApprovedAndUpdateAttendance() {
        // Arrange
        Long employeeId = 1L;
        Long creatorId = 3L; // HR ID
        createDTO.setCheckIn(LocalTime.of(8, 0));
        createDTO.setCheckOut(LocalTime.of(17, 0));

        AttendanceRecord mockRecord = new AttendanceRecord();
        mockRecord.setId(500L);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), anyString());
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(creatorId);
        when(employeeRepository.findById(creatorId)).thenReturn(Optional.of(creatorHr));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(attendanceRecordRepository.findByEmployee_EmployeeIdAndDate(employeeId, createDTO.getStartDate()))
                .thenReturn(Optional.of(mockRecord));

        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        ArgumentCaptor<ApplicationApprovalStep> stepCaptor = ArgumentCaptor.forClass(ApplicationApprovalStep.class);

        // Act
        applicationService.createApplication(createDTO, employeeId);

        // Assert & Verify
        verify(applicationRepository, atLeastOnce()).save(applicationCaptor.capture());
        verify(approvalStepRepository, times(1)).save(stepCaptor.capture());

        // Kiểm tra xem service cập nhật chấm công có được gọi không
        verify(attendanceRecordService, times(1)).updateCheckInOut(eq(mockRecord.getId()), any(AttendanceCheckInOutDTO.class));

        Application savedApp = applicationCaptor.getValue();
        assertEquals(ApplicationStatus.HR_APPROVED, savedApp.getStatus());

        ApplicationApprovalStep savedStep = stepCaptor.getValue();
        assertEquals(2, savedStep.getStep());
        assertEquals(ApprovalStepStatus.APPROVED, savedStep.getStatus());
        assertEquals(creatorHr, savedStep.getApprover());
    }

    //================================================================================
    // Test cho phương thức getApplicationsForEmployee
    //================================================================================

    @Test
    @DisplayName("Test getApplicationsForEmployee - Lấy đơn theo trạng thái cụ thể")
    void getApplicationsForEmployee_WithStatus_ShouldCallCorrectRepositoryMethod() {
        // Arrange
        Long employeeId = 1L;
        ApplicationStatus status = ApplicationStatus.PENDING_MANAGER_APPROVAL;
        Pageable pageable = PageRequest.of(0, 10);

        List<Application> applications = List.of(application);
        Page<Application> applicationPage = new PageImpl<>(applications, pageable, 1);

        when(applicationRepository.findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable))
                .thenReturn(applicationPage);

        // Act
        Page<ApplicationListItemDTO> result = applicationService.getApplicationsForEmployee(employeeId, status, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Đơn xin nghỉ phép", result.getContent().get(0).getTitle());

        // Verify
        verify(applicationRepository, times(1)).findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable);
        verify(applicationRepository, never()).findByEmployee_EmployeeId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("Test getApplicationsForEmployee - Lấy tất cả đơn khi trạng thái là null")
    void getApplicationsForEmployee_WithNullStatus_ShouldCallCorrectRepositoryMethod() {
        // Arrange
        Long employeeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Application> applications = List.of(application);
        Page<Application> applicationPage = new PageImpl<>(applications, pageable, 1);

        when(applicationRepository.findByEmployee_EmployeeId(employeeId, pageable))
                .thenReturn(applicationPage);

        // Act
        Page<ApplicationListItemDTO> result = applicationService.getApplicationsForEmployee(employeeId, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());

        // Verify
        verify(applicationRepository, never()).findByEmployee_EmployeeIdAndStatus(anyLong(), any(ApplicationStatus.class), any(Pageable.class));
        verify(applicationRepository, times(1)).findByEmployee_EmployeeId(employeeId, pageable);
    }

    //================================================================================
    // Test cho phương thức getApplicationDetail
    //================================================================================

    @Test
    @DisplayName("Test getApplicationDetail - Lấy chi tiết đơn thành công")
    void getApplicationDetail_Success() {
        // Arrange
        Long applicationId = 100L;
        Long currentUserId = 1L; // Giả sử người xem là người tạo đơn

        ApplicationApprovalStep step = new ApplicationApprovalStep();
        step.setStep(1);
        step.setStatus(ApprovalStepStatus.PENDING);
        application.getApprovalSteps().add(step);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(currentUserId);

        // Act
        ApplicationDetailDTO result = applicationService.getApplicationDetail(applicationId);

        // Assert
        assertNotNull(result);
        assertEquals(application.getTitle(), result.getTitle());
        assertEquals(employee.getEmployeeName(), result.getEmployeeName());
        assertEquals(1, result.getApprovalSteps().size());
        assertTrue(result.isCreator(), "Người xem phải là người tạo đơn");

        // Verify
        verify(applicationRepository, times(1)).findById(applicationId);
        verify(currentUserUtils, times(1)).getCurrentEmployeeId();
    }

    @Test
    @DisplayName("Test getApplicationDetail - Ném ngoại lệ khi không tìm thấy đơn")
    void getApplicationDetail_ThrowsException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(applicationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.getApplicationDetail(nonExistentId);
        });
        assertEquals("Application not found", exception.getMessage());
    }

    //================================================================================
    // Test cho phương thức updateApplication
    //================================================================================

    @Test
    @DisplayName("Test updateApplication - Cập nhật thành công")
    void updateApplication_Success() {
        // Arrange
        Long applicationId = 100L;
        Long employeeId = 1L; // Người tạo đơn
        ApplicationCreateDTO updateDto = new ApplicationCreateDTO();
        updateDto.setTitle("Tiêu đề mới");
        updateDto.setContent("Nội dung mới");
        updateDto.setApplicationTypeId(10L);
        updateDto.setStartDate(LocalDate.now().plusDays(10));

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationTypeRepository.findById(updateDto.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), anyString());

        // Act
        applicationService.updateApplication(applicationId, updateDto, employeeId);

        // Assert & Verify
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(captor.capture());

        Application savedApp = captor.getValue();
        assertEquals("Tiêu đề mới", savedApp.getTitle());
        assertEquals("Nội dung mới", savedApp.getContent());
        assertEquals(updateDto.getStartDate(), savedApp.getStartDate());
    }

    @Test
    @DisplayName("Test updateApplication - Ném ngoại lệ khi không phải người tạo đơn")
    void updateApplication_ThrowsException_WhenNotCreator() {
        // Arrange
        Long applicationId = 100L;
        Long otherEmployeeId = 99L; // Một người khác cố gắng sửa

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.updateApplication(applicationId, createDTO, otherEmployeeId);
        });
        assertEquals("Bạn không có quyền sửa đơn này", exception.getMessage());
    }

    @Test
    @DisplayName("Test updateApplication - Ném ngoại lệ khi đơn không ở trạng thái chờ duyệt")
    void updateApplication_ThrowsException_WhenStatusIsNotPending() {
        // Arrange
        application.setStatus(ApplicationStatus.MANAGER_APPROVED); // Đơn đã được duyệt
        Long applicationId = 100L;
        Long employeeId = 1L;

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.updateApplication(applicationId, createDTO, employeeId);
        });
        assertEquals("Chỉ được sửa khi đơn đang chờ quản lý duyệt", exception.getMessage());
    }

    //================================================================================
    // Test cho phương thức approveStep1
    //================================================================================

    @Test
    @DisplayName("Test approveStep1 - Phê duyệt thành công")
    void approveStep1_Approve_Success() {
        // Arrange
        Long applicationId = 100L;
        Long approverId = 2L; // Manager
        ApplicationApprovalRequestDTO request = new ApplicationApprovalRequestDTO(true, "Đã duyệt");

        ApplicationApprovalStep step1 = new ApplicationApprovalStep();
        step1.setStep(1);
        step1.setStatus(ApprovalStepStatus.PENDING);
        application.getApprovalSteps().add(step1);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(employeeRepository.findById(approverId)).thenReturn(Optional.of(creatorManager));

        // Act
        applicationService.approveStep1(applicationId, approverId, request);

        // Assert & Verify
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(captor.capture());

        Application savedApp = captor.getValue();
        assertEquals(ApplicationStatus.MANAGER_APPROVED, savedApp.getStatus());

        ApplicationApprovalStep processedStep1 = savedApp.getApprovalSteps().get(0);
        assertEquals(ApprovalStepStatus.APPROVED, processedStep1.getStatus());
        assertEquals(creatorManager, processedStep1.getApprover());
        assertEquals("Đã duyệt", processedStep1.getNote());

        // Kiểm tra xem step 2 đã được tạo chưa
        assertEquals(2, savedApp.getApprovalSteps().size());
        ApplicationApprovalStep newStep2 = savedApp.getApprovalSteps().get(1);
        assertEquals(2, newStep2.getStep());
        assertEquals(ApprovalStepStatus.PENDING, newStep2.getStatus());
    }

    @Test
    @DisplayName("Test approveStep1 - Từ chối thành công")
    void approveStep1_Reject_Success() {
        // Arrange
        Long applicationId = 100L;
        Long approverId = 2L; // Manager
        ApplicationApprovalRequestDTO request = new ApplicationApprovalRequestDTO(false, "Lý do từ chối");

        ApplicationApprovalStep step1 = new ApplicationApprovalStep();
        step1.setStep(1);
        step1.setStatus(ApprovalStepStatus.PENDING);
        application.getApprovalSteps().add(step1);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(employeeRepository.findById(approverId)).thenReturn(Optional.of(creatorManager));

        // Act
        applicationService.approveStep1(applicationId, approverId, request);

        // Assert & Verify
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(captor.capture());

        Application savedApp = captor.getValue();
        assertEquals(ApplicationStatus.MANAGER_REJECTED, savedApp.getStatus());
        assertEquals("Lý do từ chối", savedApp.getRejectReason());

        ApplicationApprovalStep processedStep1 = savedApp.getApprovalSteps().get(0);
        assertEquals(ApprovalStepStatus.REJECTED, processedStep1.getStatus());
        assertEquals(creatorManager, processedStep1.getApprover());
    }

    @Test
    @DisplayName("Test approveStep1 - Ném ngoại lệ khi bước đã được xử lý")
    void approveStep1_ThrowsException_WhenStepAlreadyProcessed() {
        // Arrange
        Long applicationId = 100L;
        Long approverId = 2L;
        ApplicationApprovalRequestDTO request = new ApplicationApprovalRequestDTO(true, "Note");

        ApplicationApprovalStep step1 = new ApplicationApprovalStep();
        step1.setStep(1);
        step1.setStatus(ApprovalStepStatus.APPROVED); // Đã được duyệt trước đó
        application.getApprovalSteps().add(step1);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.approveStep1(applicationId, approverId, request);
        });
        assertEquals("Bước đã được xử lý", exception.getMessage());
    }

    //================================================================================
    // Test cho phương thức getStep1Applications
    //================================================================================

    @Test
    @DisplayName("Test getStep1Applications - Lấy đơn theo trạng thái PENDING")
    void getStep1Applications_WithPendingStatus() {
        // Arrange
        ApplicationStatus status = ApplicationStatus.PENDING_MANAGER_APPROVAL;
        PageRequest pageRequest = PageRequest.of(0, 10);

        ApplicationApprovalStep step = new ApplicationApprovalStep();
        step.setApplication(application);
        Page<ApplicationApprovalStep> stepPage = new PageImpl<>(List.of(step), pageRequest, 1);

        when(approvalStepRepository.findByStepAndStatus(1, ApprovalStepStatus.PENDING, pageRequest))
                .thenReturn(stepPage);

        // Act
        Page<ApplicationApprovalListItemDTO> result = applicationService.getStep1Applications(status, pageRequest);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Đơn xin nghỉ phép", result.getContent().get(0).getTitle());

        // Verify
        verify(approvalStepRepository, times(1)).findByStepAndStatus(1, ApprovalStepStatus.PENDING, pageRequest);
    }

    @Test
    @DisplayName("Test getStep1Applications - Lấy tất cả đơn khi trạng thái là null")
    void getStep1Applications_WithNullStatus() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);

        ApplicationApprovalStep step = new ApplicationApprovalStep();
        step.setApplication(application);
        Page<ApplicationApprovalStep> stepPage = new PageImpl<>(List.of(step), pageRequest, 1);

        when(approvalStepRepository.findByStep(1, pageRequest)).thenReturn(stepPage);

        // Act
        Page<ApplicationApprovalListItemDTO> result = applicationService.getStep1Applications(null, pageRequest);

        // Assert
        assertEquals(1, result.getTotalElements());

        // Verify
        verify(approvalStepRepository, times(1)).findByStep(1, pageRequest);
        verify(approvalStepRepository, never()).findByStepAndStatus(anyInt(), any(), any());
    }

    @Test
    @DisplayName("Test getStep1Applications - Ném ngoại lệ khi trạng thái không hợp lệ")
    void getStep1Applications_ThrowsException_ForInvalidStatus() {
        // Arrange
        ApplicationStatus invalidStatus = ApplicationStatus.HR_APPROVED; // Trạng thái không hợp lệ cho step 1
        PageRequest pageRequest = PageRequest.of(0, 10);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.getStep1Applications(invalidStatus, pageRequest);
        });
        assertEquals("Trạng thái không hợp lệ", exception.getMessage());
    }
}
