package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sep490.com.example.hrms_backend.dto.benefit.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.enums.FormulaType;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.BenefitPositionRepository;
import sep490.com.example.hrms_backend.repository.BenefitRegistrationRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BenefitRegistrationImplTest {

    // 1. Mock tất cả các dependency của BenefitRegistrationImpl
    @Mock
    private BenefitRegistrationRepository benefitRegistrationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private BenefitPositionRepository benefitPositionRepository;

    // 2. Tiêm các mock vào service cần test
    @InjectMocks
    private BenefitRegistrationImpl benefitRegistrationService;

    // 3. Khai báo các đối tượng dữ liệu mẫu
    private Employee employee;
    private Benefit benefit;
    private BenefitDTO benefitDTO;
    private Position position;
    private Department department;
    private BenefitPosition benefitPosition;
    private BenefitRegistration benefitRegistration;
    private BenefitRegistrationDTO benefitRegistrationDTO;


    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        // Tạo các đối tượng mẫu
        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("IT");

        position = new Position();
        position.setPositionId(10L);
        position.setPositionName("Công nhân");

        department.setPositions(List.of(position));

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeName("Nguyễn Văn A");
        employee.setEmail("user1@example.com");
        employee.setDepartment(department);

        benefit = new Benefit();
        benefit.setId(100L);
        benefit.setTitle("Du lịch công ty");
        benefit.setIsActive(true);
        benefit.setEndDate(LocalDate.now().plusDays(30)); // Phúc lợi còn hạn
        benefit.setMaxParticipants(50);
        benefit.setBenefitType(BenefitType.SU_KIEN);

        benefitDTO = new BenefitDTO();
        benefitDTO.setId(100L);
        benefitDTO.setTitle("Du lịch công ty");

        benefitPosition = new BenefitPosition();
        benefitPosition.setId(200L);
        benefitPosition.setBenefit(benefit);
        benefitPosition.setPosition(position);
        benefitPosition.setFormulaType(FormulaType.AMOUNT);
        benefitPosition.setFormulaValue(BigDecimal.TEN);

        benefitRegistration = new BenefitRegistration();
        benefitRegistration.setId(300L);
        benefitRegistration.setEmployee(employee);
        benefitRegistration.setBenefitPosition(benefitPosition);
        benefitRegistration.setRegisteredAt(LocalDateTime.now());

        benefitRegistrationDTO = new BenefitRegistrationDTO();
        benefitRegistrationDTO.setId(300L);
    }

    //================================================================================
    // Test cho phương thức searchBenefitByEmployee
    //================================================================================

    @Test
    @DisplayName("Test searchBenefitByEmployee - Thành công khi tìm thấy nhân viên và phúc lợi")
    void searchBenefitByEmployee_Success() {
        // Arrange
        Long employeeId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<Benefit> benefitList = List.of(benefit);
        Page<Benefit> benefitPage = new PageImpl<>(benefitList, pageable, 1);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(benefitRegistrationRepository.findByEmployeeOrderById(employee, pageable)).thenReturn(benefitPage);
        when(modelMapper.map(benefit, BenefitDTO.class)).thenReturn(benefitDTO);

        // Act
        BenefitResponse response = benefitRegistrationService.searchBenefitByEmployee(employeeId, 1, 10, "id", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(benefitDTO.getId(), response.getContent().get(0).getId());
        assertEquals(0, response.getPageNumber());

        // Verify
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(benefitRegistrationRepository, times(1)).findByEmployeeOrderById(employee, pageable);
        verify(modelMapper, times(1)).map(benefit, BenefitDTO.class);
    }

    @Test
    @DisplayName("Test searchBenefitByEmployee - Ném ngoại lệ khi không tìm thấy nhân viên")
    void searchBenefitByEmployee_ThrowsException_WhenEmployeeNotFound() {
        // Arrange
        Long nonExistentEmployeeId = 99L;
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            benefitRegistrationService.searchBenefitByEmployee(nonExistentEmployeeId, 1, 10, "id", "asc");
        });
        assertEquals("Employee not found with id: " + nonExistentEmployeeId, exception.getMessage());

        // Verify
        verify(benefitRegistrationRepository, never()).findByEmployeeOrderById(any(), any());
        verify(modelMapper, never()).map(any(), any());
    }


    //================================================================================
    // Test cho phương thức quickRegister
    //================================================================================

    @Test
    @DisplayName("Test quickRegister - Đăng ký nhanh thành công cho tất cả nhân viên")
    void quickRegister_Success_AllEmployeesRegistered() {
        // Arrange
        BenefitManualRegistrationRequest request = new BenefitManualRegistrationRequest();
        request.setBenefitId(100L);
        request.setPositionId(10L);
        request.setKeywords(List.of("Nguyễn Văn A", "jane.doe@example.com"));

        Employee jane = new Employee();
        jane.setEmployeeId(2L);
        jane.setEmployeeName("Jane Doe");
        jane.setEmail("jane.doe@example.com");
        jane.setDepartment(department);

        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(100L, 10L)).thenReturn(Optional.of(benefitPosition));
        when(employeeRepository.findByEmployeeNameIgnoreCaseOrEmailIgnoreCase("Nguyễn Văn A", "Nguyễn Văn A")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeNameIgnoreCaseOrEmailIgnoreCase("jane.doe@example.com", "jane.doe@example.com")).thenReturn(Optional.of(jane));
        when(benefitRegistrationRepository.existsByBenefitPositionAndEmployee(any(), any())).thenReturn(false);

        // Act & Assert
        // Đảm bảo không có ngoại lệ nào được ném ra
        assertDoesNotThrow(() -> benefitRegistrationService.quickRegister(request));

        // Verify
        // Phải lưu đăng ký cho cả 2 nhân viên
        verify(benefitRegistrationRepository, times(2)).save(any(BenefitRegistration.class));
    }

    @Test
    @DisplayName("Test quickRegister - Ném ngoại lệ khi có nhân viên đăng ký thất bại")
    void quickRegister_ThrowsException_WhenSomeEmployeesFail() {
        // Arrange
        BenefitManualRegistrationRequest request = new BenefitManualRegistrationRequest();
        request.setBenefitId(100L);
        request.setPositionId(10L);
        // Nguyễn Văn A (Thành công), Not Found (Thất bại), Jane Doe (Đã đăng ký)
        request.setKeywords(List.of("Nguyễn Văn A", "Not Found", "Jane Doe"));

        Employee jane = new Employee();
        jane.setEmployeeId(2L);
        jane.setEmployeeName("Jane Doe");
        jane.setDepartment(department);

        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(100L, 10L)).thenReturn(Optional.of(benefitPosition));
        // Nguyễn Văn A - Thành công
        when(employeeRepository.findByEmployeeNameIgnoreCaseOrEmailIgnoreCase("Nguyễn Văn A", "Nguyễn Văn A")).thenReturn(Optional.of(employee));
        when(benefitRegistrationRepository.existsByBenefitPositionAndEmployee(benefitPosition, employee)).thenReturn(false);
        // Not Found - Thất bại
        when(employeeRepository.findByEmployeeNameIgnoreCaseOrEmailIgnoreCase("Not Found", "Not Found")).thenReturn(Optional.empty());
        // Jane Doe - Đã đăng ký
        when(employeeRepository.findByEmployeeNameIgnoreCaseOrEmailIgnoreCase("Jane Doe", "Jane Doe")).thenReturn(Optional.of(jane));
        when(benefitRegistrationRepository.existsByBenefitPositionAndEmployee(benefitPosition, jane)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            benefitRegistrationService.quickRegister(request);
        });

        String expectedMessage = "Một số nhân viên không thể đăng ký: Not Found (không tìm thấy nhân viên), Jane Doe (đã đăng ký)";
        assertEquals(expectedMessage, exception.getMessage());

        // Verify
        // Chỉ lưu đăng ký cho Nguyễn Văn A (1 lần)
        verify(benefitRegistrationRepository, times(1)).save(any(BenefitRegistration.class));
    }

    //================================================================================
    // Test cho phương thức searchUnregisteredEmployees
    //================================================================================

    @Test
    @DisplayName("Test searchUnregisteredEmployees - Thành công với keyword")
    void searchUnregisteredEmployees_Success_WithKeyword() {
        // Arrange
        Long benefitId = 100L;
        Long positionId = 10L;
        String keyword = "john";

        Employee emp1 = new Employee();
        emp1.setEmployeeId(1L);
        emp1.setEmployeeName("Nguyễn Văn A");

        Employee emp2 = new Employee();
        emp2.setEmployeeId(2L);
        emp2.setEmployeeName("Johnny English");

        Employee emp3 = new Employee(); // Đã đăng ký
        emp3.setEmployeeId(3L);
        emp3.setEmployeeName("Big John");

        List<Employee> foundEmployees = List.of(emp1, emp2, emp3);
        List<Long> registeredIds = List.of(3L); // Big John đã đăng ký

        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(benefitId, positionId)).thenReturn(Optional.of(benefitPosition));
        when(employeeRepository.searchByPositionAndKeyword(positionId, "%" + keyword.toLowerCase() + "%")).thenReturn(foundEmployees);
        when(benefitRegistrationRepository.findRegisteredEmployeeIdsByBenefitPositionId(benefitPosition.getId())).thenReturn(registeredIds);

        // Mock a simple mapping
        when(modelMapper.map(any(Employee.class), eq(EmployeeBasicDetailResponse.class)))
                .thenAnswer(invocation -> {
                    Employee e = invocation.getArgument(0);
                    EmployeeBasicDetailResponse dto = new EmployeeBasicDetailResponse();
                    dto.setEmployeeId(e.getEmployeeId());
                    dto.setEmployeeName(e.getEmployeeName());
                    return dto;
                });

        // Act
        List<EmployeeBasicDetailResponse> result = benefitRegistrationService.searchUnregisteredEmployees(benefitId, positionId, keyword);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Chỉ còn Nguyễn Văn A và Johnny English
        assertTrue(result.stream().anyMatch(dto -> dto.getEmployeeName().equals("Nguyễn Văn A")));
        assertTrue(result.stream().anyMatch(dto -> dto.getEmployeeName().equals("Johnny English")));
        assertFalse(result.stream().anyMatch(dto -> dto.getEmployeeName().equals("Big John")));

        // Verify
        verify(employeeRepository, times(1)).searchByPositionAndKeyword(eq(positionId), anyString());
        verify(employeeRepository, never()).findByPosition_PositionId(anyLong());
    }

    @Test
    @DisplayName("Test searchUnregisteredEmployees - Thành công không có keyword")
    void searchUnregisteredEmployees_Success_WithoutKeyword() {
        // Arrange
        Long benefitId = 100L;
        Long positionId = 10L;

        Employee emp1 = new Employee(); // Chưa đăng ký
        emp1.setEmployeeId(1L);

        Employee emp2 = new Employee(); // Đã đăng ký
        emp2.setEmployeeId(2L);

        List<Employee> allEmployeesInPosition = List.of(emp1, emp2);
        List<Long> registeredIds = List.of(2L);

        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(benefitId, positionId)).thenReturn(Optional.of(benefitPosition));
        when(employeeRepository.findByPosition_PositionId(positionId)).thenReturn(allEmployeesInPosition);
        when(benefitRegistrationRepository.findRegisteredEmployeeIdsByBenefitPositionId(benefitPosition.getId())).thenReturn(registeredIds);
        when(modelMapper.map(emp1, EmployeeBasicDetailResponse.class)).thenReturn(new EmployeeBasicDetailResponse());

        // Act
        List<EmployeeBasicDetailResponse> result = benefitRegistrationService.searchUnregisteredEmployees(benefitId, positionId, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify
        verify(employeeRepository, never()).searchByPositionAndKeyword(anyLong(), anyString());
        verify(employeeRepository, times(1)).findByPosition_PositionId(positionId);
        verify(modelMapper, times(1)).map(emp1, EmployeeBasicDetailResponse.class);
    }

    @Test
    @DisplayName("Test searchUnregisteredEmployees - Ném ngoại lệ khi BenefitPosition không tồn tại")
    void searchUnregisteredEmployees_ThrowsException_WhenBenefitPositionNotFound() {
        // Arrange
        Long benefitId = 99L;
        Long positionId = 99L;
        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(benefitId, positionId)).thenReturn(Optional.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitRegistrationService.searchUnregisteredEmployees(benefitId, positionId, "test");
        });

        assertEquals("Không tìm thấy benefitPosition", exception.getMessage());

        // Verify
        verify(employeeRepository, never()).searchByPositionAndKeyword(anyLong(), anyString());
        verify(benefitRegistrationRepository, never()).findRegisteredEmployeeIdsByBenefitPositionId(anyLong());
    }

    //================================================================================
    // Test cho phương thức unRegister
    //================================================================================

    @Test
    @DisplayName("Test unRegister - Hủy đăng ký thành công")
    void unRegister_Success() {
        // Arrange
        Long benefitId = 100L;
        Long positionId = 10L;
        Long employeeId = 1L;
        Long benefitPositionId = 200L;

        // Giả lập rằng BenefitPosition được tìm thấy
        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(benefitId, positionId))
                .thenReturn(Optional.of(benefitPosition));

        // Phương thức delete không trả về gì, chúng ta sẽ verify nó được gọi
        doNothing().when(benefitRegistrationRepository).deleteByBenefitPositionIdAndEmployeeId(anyLong(), anyLong());

        // Act & Assert
        // Đảm bảo không có ngoại lệ nào được ném ra
        assertDoesNotThrow(() -> benefitRegistrationService.unRegister(benefitId, positionId, employeeId));

        // Verify
        // Kiểm tra xem phương thức delete có được gọi với đúng các ID không
        verify(benefitRegistrationRepository, times(1))
                .deleteByBenefitPositionIdAndEmployeeId(benefitPositionId, employeeId);
    }

    @Test
    @DisplayName("Test unRegister - Ném ngoại lệ khi BenefitPosition không tồn tại")
    void unRegister_ThrowsException_WhenBenefitPositionNotFound() {
        // Arrange
        Long benefitId = 99L;
        Long positionId = 99L;
        Long employeeId = 1L;

        // Giả lập rằng BenefitPosition không được tìm thấy
        when(benefitPositionRepository.findByBenefit_IdAndPosition_PositionId(benefitId, positionId))
                .thenReturn(Optional.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitRegistrationService.unRegister(benefitId, positionId, employeeId);
        });

        assertEquals("Không tìm thấy benefitPosition", exception.getMessage());

        // Verify
        // Đảm bảo phương thức delete không bao giờ được gọi
        verify(benefitRegistrationRepository, never()).deleteByBenefitPositionIdAndEmployeeId(anyLong(), anyLong());
    }


    //================================================================================
    // Test cho phương thức quickRegisterAll
    //================================================================================

    @Test
    @DisplayName("Test quickRegisterAll - Đăng ký thành công cho tất cả nhân viên ở nhiều vị trí")
    void quickRegisterAll_Success() {
        // Arrange
        Long benefitId = 100L;
        Long positionIdDev = 10L;
        Long positionIdTester = 20L;

        BenefitMultiPositionRequestDTO request = new BenefitMultiPositionRequestDTO();
        request.setBenefitId(benefitId);
        request.setPositionIds(List.of(positionIdDev, positionIdTester));

        // Dữ liệu mẫu cho vị trí Tester
        Position positionTester = new Position();
        positionTester.setPositionId(positionIdTester);
        BenefitPosition benefitPositionTester = new BenefitPosition();
        benefitPositionTester.setPosition(positionTester);

        Employee employeeTester = new Employee();
        employeeTester.setEmployeeId(2L);
        employeeTester.setEmployeeName("Jane Tester");

        List<BenefitPosition> foundBenefitPositions = List.of(benefitPosition, benefitPositionTester);

        // Mock repository calls
        when(benefitPositionRepository.findAllByBenefit_IdAndPosition_PositionIdIn(benefitId, request.getPositionIds()))
                .thenReturn(foundBenefitPositions);

        when(employeeRepository.findAllByDepartment_Positions_PositionId(positionIdDev)).thenReturn(List.of(employee));
        when(employeeRepository.findAllByDepartment_Positions_PositionId(positionIdTester)).thenReturn(List.of(employeeTester));

        // Giả lập chưa ai đăng ký
        when(benefitRegistrationRepository.existsByBenefitPositionAndEmployee(any(), any())).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> benefitRegistrationService.quickRegisterAll(request));

        // Verify
        // Phải lưu đăng ký cho cả 2 nhân viên
        verify(benefitRegistrationRepository, times(2)).save(any(BenefitRegistration.class));
    }

    @Test
    @DisplayName("Test quickRegisterAll - Ném ngoại lệ khi một số nhân viên đã đăng ký")
    void quickRegisterAll_ThrowsException_WhenSomeAreRegistered() {
        // Arrange
        Long benefitId = 100L;
        Long positionIdDev = 10L;

        BenefitMultiPositionRequestDTO request = new BenefitMultiPositionRequestDTO();
        request.setBenefitId(benefitId);
        request.setPositionIds(List.of(positionIdDev));

        when(benefitPositionRepository.findAllByBenefit_IdAndPosition_PositionIdIn(benefitId, request.getPositionIds()))
                .thenReturn(List.of(benefitPosition));
        when(employeeRepository.findAllByDepartment_Positions_PositionId(positionIdDev)).thenReturn(List.of(employee));

        // Giả lập nhân viên này đã đăng ký rồi
        when(benefitRegistrationRepository.existsByBenefitPositionAndEmployee(benefitPosition, employee)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            benefitRegistrationService.quickRegisterAll(request);
        });

        assertEquals("Một số nhân viên không thể đăng ký: Nguyễn Văn A (đã đăng ký)", exception.getMessage());

        // Verify
        // Không có lệnh save nào được gọi
        verify(benefitRegistrationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test quickRegisterAll - Ném ngoại lệ khi không tìm thấy BenefitPosition nào")
    void quickRegisterAll_ThrowsException_WhenNoBenefitPositionsFound() {
        // Arrange
        Long benefitId = 99L;
        BenefitMultiPositionRequestDTO request = new BenefitMultiPositionRequestDTO();
        request.setBenefitId(benefitId);
        request.setPositionIds(List.of(10L, 20L));

        // Giả lập không tìm thấy BenefitPosition nào
        when(benefitPositionRepository.findAllByBenefit_IdAndPosition_PositionIdIn(benefitId, request.getPositionIds()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            benefitRegistrationService.quickRegisterAll(request);
        });

        assertEquals("Không tìm thấy BenefitPosition phù hợp với benefitId và positionIds.", exception.getMessage());

        // Verify
        verify(employeeRepository, never()).findAllByDepartment_Positions_PositionId(anyLong());
        verify(benefitRegistrationRepository, never()).save(any());
    }
}
