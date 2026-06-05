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
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.ApplicationStatus;
import sep490.com.example.hrms_backend.enums.ApprovalStepStatus;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.AttendanceRecordService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;
import sep490.com.example.hrms_backend.validation.ApplicationValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {
    // 1. Mock tất cả các dependency
    @Mock
    private Application application;

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
        // SỬA LỖI: Đảm bảo status luôn có giá trị mặc định
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

    @Test
    @DisplayName("createApplication - HR tạo đơn không có checkin/checkout -> duyệt step2, không cập nhật chấm công")
    void createApplication_HR_NoCheckInOut_NoAttendanceUpdate() {
        Long employeeId = 1L;
        Long creatorId = 3L; // HR

        // Không set checkin/checkout
        createDTO.setCheckIn(null);
        createDTO.setCheckOut(null);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), eq(applicationType.getName()));
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(creatorId);
        when(employeeRepository.findById(creatorId)).thenReturn(Optional.of(creatorHr));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
        ArgumentCaptor<ApplicationApprovalStep> stepCaptor = ArgumentCaptor.forClass(ApplicationApprovalStep.class);

        applicationService.createApplication(createDTO, employeeId);

        // Save 2 lần (lưu + cập nhật updatedAt)
        verify(applicationRepository, times(2)).save(appCaptor.capture());
        verify(approvalStepRepository, times(1)).save(stepCaptor.capture());

        // Không đụng vào chấm công
        verify(attendanceRecordRepository, never()).findByEmployee_EmployeeIdAndDate(anyLong(), any());
        verify(attendanceRecordService, never()).updateCheckInOut(anyLong(), any());

        Application savedFirst = appCaptor.getAllValues().get(0);
        assertEquals(ApplicationStatus.HR_APPROVED, savedFirst.getStatus());

        ApplicationApprovalStep step2 = stepCaptor.getValue();
        assertEquals(2, step2.getStep());
        assertEquals(ApprovalStepStatus.APPROVED, step2.getStatus());
        assertEquals(creatorHr, step2.getApprover());
        assertEquals("HR tạo và duyệt đơn", step2.getNote());
    }

    @Test
    @DisplayName("createApplication - HR có checkin/checkout nhưng không có bản ghi chấm công -> không cập nhật")
    void createApplication_HR_CheckInOut_NoAttendanceRecord_NoUpdate() {
        Long employeeId = 1L;
        Long creatorId = 3L; // HR

        createDTO.setCheckIn(LocalTime.of(8, 0));
        createDTO.setCheckOut(LocalTime.of(17, 0));

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), eq(applicationType.getName()));
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(creatorId);
        when(employeeRepository.findById(creatorId)).thenReturn(Optional.of(creatorHr));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(attendanceRecordRepository.findByEmployee_EmployeeIdAndDate(employeeId, createDTO.getStartDate()))
                .thenReturn(Optional.empty());

        applicationService.createApplication(createDTO, employeeId);

        // Có gọi tìm bản ghi chấm công nhưng không cập nhật
        verify(attendanceRecordRepository, times(1))
                .findByEmployee_EmployeeIdAndDate(employeeId, createDTO.getStartDate());
        verify(attendanceRecordService, never()).updateCheckInOut(anyLong(), any());
    }

    @Test
    @DisplayName("createApplication - Validator ném lỗi -> không lưu application/step")
    void createApplication_ValidatorThrows_ShouldNotSave() {
        Long employeeId = 1L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doThrow(new RuntimeException("invalid")).when(applicationValidator).validate(any(), eq(applicationType.getName()));

        assertThrows(RuntimeException.class, () -> applicationService.createApplication(createDTO, employeeId));

        verify(applicationRepository, never()).save(any());
        verify(approvalStepRepository, never()).save(any());
        verify(attendanceRecordService, never()).updateCheckInOut(anyLong(), any());
    }

    @Test
    @DisplayName("createApplication - Employee không tồn tại -> throw và không lưu gì")
    void createApplication_EmployeeNotFound_Throws() {
        Long employeeId = 999L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> applicationService.createApplication(createDTO, employeeId));

        verifyNoInteractions(applicationTypeRepository, applicationRepository, approvalStepRepository,
                attendanceRecordRepository, attendanceRecordService, currentUserUtils, applicationValidator);
    }

    @Test
    @DisplayName("createApplication - ApplicationType không tồn tại -> throw và không lưu gì")
    void createApplication_ApplicationTypeNotFound_Throws() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> applicationService.createApplication(createDTO, employeeId));

        verify(applicationRepository, never()).save(any());
        verify(approvalStepRepository, never()).save(any());
    }

    @Test
    @DisplayName("createApplication - Creator không tồn tại -> throw và không lưu gì")
    void createApplication_CreatorNotFound_Throws() {
        Long employeeId = 1L;
        Long creatorId = 12345L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(applicationTypeRepository.findById(createDTO.getApplicationTypeId())).thenReturn(Optional.of(applicationType));
        doNothing().when(applicationValidator).validate(any(), eq(applicationType.getName()));
        when(currentUserUtils.getCurrentEmployeeId()).thenReturn(creatorId);
        when(employeeRepository.findById(creatorId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> applicationService.createApplication(createDTO, employeeId));

        verify(applicationRepository, never()).save(any());
        verify(approvalStepRepository, never()).save(any());
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

    @Test
    @DisplayName("getApplicationsForEmployee - Có status nhưng không có kết quả -> trả về trang rỗng")
    void getApplicationsForEmployee_WithStatus_NoResults_ReturnsEmptyPage() {
        Long employeeId = 1L;
        ApplicationStatus status = ApplicationStatus.PENDING_MANAGER_APPROVAL;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Application> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(applicationRepository.findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable))
                .thenReturn(emptyPage);

        Page<ApplicationListItemDTO> result = applicationService.getApplicationsForEmployee(employeeId, status, pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(applicationRepository, times(1)).findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable);
        verify(applicationRepository, never()).findByEmployee_EmployeeId(anyLong(), any());
    }

    @Test
    @DisplayName("getApplicationsForEmployee - Bảo toàn thông tin phân trang (page, size, total)")
    void getApplicationsForEmployee_PaginationMetadata_Preserved() {
        Long employeeId = 1L;
        Pageable pageable = PageRequest.of(2, 5); // page=2, size=5
        // total lớn hơn số phần tử trả về ở trang hiện tại
        Page<Application> repoPage = new PageImpl<>(List.of(application), pageable, 23);

        when(applicationRepository.findByEmployee_EmployeeId(employeeId, pageable)).thenReturn(repoPage);

        Page<ApplicationListItemDTO> result = applicationService.getApplicationsForEmployee(employeeId, null, pageable);

        assertEquals(23, result.getTotalElements());
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getSize());
        verify(applicationRepository, times(1)).findByEmployee_EmployeeId(employeeId, pageable);
    }

    @Test
    @DisplayName("getApplicationsForEmployee - Map đúng và giữ nguyên thứ tự nhiều phần tử")
    void getApplicationsForEmployee_WithMultipleResults_MapsAllAndKeepsOrder() {
        Long employeeId = 1L;
        ApplicationStatus status = ApplicationStatus.PENDING_MANAGER_APPROVAL;
        Pageable pageable = PageRequest.of(0, 10);

        Application app1 = application; // đã setup với title "Đơn xin nghỉ phép"
        Application app2 = new Application();
        app2.setId(101L);
        app2.setTitle("Đơn xin đi muộn");
        app2.setApplicationType(applicationType);
        app2.setStatus(ApplicationStatus.PENDING_MANAGER_APPROVAL);
        app2.setEmployee(employee);
        app2.setCreatedAt(java.time.LocalDateTime.now());

        Page<Application> repoPage = new PageImpl<>(List.of(app1, app2), pageable, 2);
        when(applicationRepository.findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable))
                .thenReturn(repoPage);

        Page<ApplicationListItemDTO> result = applicationService.getApplicationsForEmployee(employeeId, status, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Đơn xin nghỉ phép", result.getContent().get(0).getTitle());
        assertEquals("Đơn xin đi muộn", result.getContent().get(1).getTitle());
        verify(applicationRepository, times(1)).findByEmployee_EmployeeIdAndStatus(employeeId, status, pageable);
    }


    //================================================================================
    // Test cho phương thức approveStep2
    //================================================================================

    @Test
    @DisplayName("Test approveStep2 - Phê duyệt thành công và cập nhật chấm công")
    void approveStep2_Approve_Success() {
        // Arrange
        Long applicationId = 100L;
        Long approverId = 3L; // HR
        ApplicationApprovalRequestDTO request = new ApplicationApprovalRequestDTO(true, "HR đã duyệt");

        // Giả lập đơn đã qua bước 1
        ApplicationApprovalStep step1 = new ApplicationApprovalStep();
        step1.setStep(1);
        step1.setStatus(ApprovalStepStatus.APPROVED);

        // Giả lập bước 2 đang chờ
        ApplicationApprovalStep step2 = new ApplicationApprovalStep();
        step2.setStep(2);
        step2.setStatus(ApprovalStepStatus.PENDING);

        application.getApprovalSteps().addAll(List.of(step1, step2));
        application.setCheckIn(LocalTime.of(8, 5)); // Có check-in/out để test logic cập nhật
        application.setCheckOut(LocalTime.of(17, 5));

        AttendanceRecord mockRecord = new AttendanceRecord();
        mockRecord.setId(500L);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(employeeRepository.findById(approverId)).thenReturn(Optional.of(creatorHr));
        when(attendanceRecordRepository.findByEmployee_EmployeeIdAndDate(anyLong(), any()))
                .thenReturn(Optional.of(mockRecord));

        // Act
        applicationService.approveStep2(applicationId, approverId, request);

        // Assert & Verify
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(captor.capture());

        Application savedApp = captor.getValue();
        assertEquals(ApplicationStatus.HR_APPROVED, savedApp.getStatus());

        ApplicationApprovalStep processedStep2 = savedApp.getApprovalSteps().stream()
                .filter(s -> s.getStep() == 2).findFirst().orElse(null);

        assertNotNull(processedStep2);
        assertEquals(ApprovalStepStatus.APPROVED, processedStep2.getStatus());
        assertEquals(creatorHr, processedStep2.getApprover());

        // Kiểm tra logic cập nhật chấm công được gọi
        verify(attendanceRecordService, times(1)).updateCheckInOut(eq(mockRecord.getId()), any());
    }

    @Test
    @DisplayName("Test approveStep2 - Từ chối thành công")
    void approveStep2_Reject_Success() {
        // Arrange
        Long applicationId = 100L;
        Long approverId = 3L; // HR
        ApplicationApprovalRequestDTO request = new ApplicationApprovalRequestDTO(false, "HR từ chối");

        ApplicationApprovalStep step2 = new ApplicationApprovalStep();
        step2.setStep(2);
        step2.setStatus(ApprovalStepStatus.PENDING);
        application.getApprovalSteps().add(step2);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(employeeRepository.findById(approverId)).thenReturn(Optional.of(creatorHr));

        // Act
        applicationService.approveStep2(applicationId, approverId, request);

        // Assert & Verify
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(captor.capture());

        Application savedApp = captor.getValue();
        assertEquals(ApplicationStatus.HR_REJECTED, savedApp.getStatus());
        assertEquals("HR từ chối", savedApp.getRejectReason());

        // Đảm bảo không cập nhật chấm công khi từ chối
        verify(attendanceRecordService, never()).updateCheckInOut(anyLong(), any());
    }

    //================================================================================
    // Test cho phương thức getStep2Applications
    //================================================================================

    @Test
    @DisplayName("Test getStep2Applications - Lấy đơn theo trạng thái PENDING (từ MANAGER_APPROVED)")
    void getStep2Applications_WithManagerApprovedStatus() {
        // Arrange
        ApplicationStatus status = ApplicationStatus.MANAGER_APPROVED;
        PageRequest pageRequest = PageRequest.of(0, 10);

        ApplicationApprovalStep step = new ApplicationApprovalStep();
        step.setApplication(application);
        Page<ApplicationApprovalStep> stepPage = new PageImpl<>(List.of(step), pageRequest, 1);

        // Khi trạng thái là MANAGER_APPROVED, service sẽ tìm các step 2 có status PENDING
        when(approvalStepRepository.findByStepAndStatus(2, ApprovalStepStatus.PENDING, pageRequest))
                .thenReturn(stepPage);

        // Act
        Page<ApplicationApprovalListItemDTO> result = applicationService.getStep2Applications(status, pageRequest);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Đơn xin nghỉ phép", result.getContent().get(0).getTitle());

        // Verify
        verify(approvalStepRepository, times(1)).findByStepAndStatus(2, ApprovalStepStatus.PENDING, pageRequest);
    }

    @Test
    @DisplayName("Test getStep2Applications - Lấy tất cả đơn khi trạng thái là null")
    void getStep2Applications_WithNullStatus() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);

        ApplicationApprovalStep step = new ApplicationApprovalStep();
        step.setApplication(application);
        Page<ApplicationApprovalStep> stepPage = new PageImpl<>(List.of(step), pageRequest, 1);

        when(approvalStepRepository.findByStep(2, pageRequest)).thenReturn(stepPage);

        // Act
        Page<ApplicationApprovalListItemDTO> result = applicationService.getStep2Applications(null, pageRequest);

        // Assert
        assertEquals(1, result.getTotalElements());

        // Verify
        verify(approvalStepRepository, times(1)).findByStep(2, pageRequest);
        verify(approvalStepRepository, never()).findByStepAndStatus(anyInt(), any(), any());
    }

    

}
