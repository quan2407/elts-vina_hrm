package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.enums.FormulaType;
import sep490.com.example.hrms_backend.mapper.SalaryMapper;
import sep490.com.example.hrms_backend.repository.AttendanceRecordRepository;
import sep490.com.example.hrms_backend.repository.BenefitRegistrationRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.SalaryRepository;
import sep490.com.example.hrms_backend.service.BenefitService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class SalaryServiceImplTest {
    // 1. Mock các dependency
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;
    @Mock
    private BenefitRegistrationRepository benefitRegistrationRepository;
    @Mock
    private BenefitService benefitService;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private SalaryServiceImpl salaryService;

    // 3. Khai báo dữ liệu mẫu
    private Employee employee;
    private Salary salary;
    private List<AttendanceRecord> attendanceRecords;
    private List<BenefitRegistration> benefitRegistrations;
    private Benefit benefitPhuCap, benefitKhauTru;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeName("Nhân viên A");
        employee.setBasicSalary(new BigDecimal("10000000")); // Lương cơ bản 10 triệu

        salary = new Salary();
        salary.setId(1L);
        salary.setEmployee(employee);
        salary.setSalaryMonth(LocalDate.of(2024, 8, 1));

        AttendanceRecord record = new AttendanceRecord();
        record.setDayShift("8"); // 8 giờ công
        record.setOtShift("2");   // 2 giờ OT
        attendanceRecords = List.of(record);

        benefitPhuCap = new Benefit();
        benefitPhuCap.setTitle("Phụ cấp ăn trưa");
        benefitPhuCap.setBenefitType(BenefitType.PHU_CAP);

        benefitKhauTru = new Benefit();
        benefitKhauTru.setTitle("Trừ tiền bảo hiểm");
        benefitKhauTru.setBenefitType(BenefitType.KHAU_TRU);

        BenefitPosition bpPhuCap = new BenefitPosition();
        bpPhuCap.setBenefit(benefitPhuCap);
        bpPhuCap.setFormulaType(FormulaType.AMOUNT);
        bpPhuCap.setFormulaValue(new BigDecimal("500000")); // 500k

        BenefitRegistration regPhuCap = new BenefitRegistration();
        regPhuCap.setIsRegister(true);
        regPhuCap.setBenefitPosition(bpPhuCap);

        benefitRegistrations = List.of(regPhuCap);
    }

    //================================================================================
    // Test cho phương thức getSalariesByMonth (phiên bản không phân trang)
    //================================================================================

    @Test
    @DisplayName("Test getSalariesByMonth - Lấy và bổ sung phúc lợi thành công")
    void getSalariesByMonth_Success() {
        // Arrange
        when(salaryRepository.findBySalaryMonth(any(LocalDate.class))).thenReturn(List.of(salary));
        // Giả sử có 2 phúc lợi active, nhưng nhân viên chỉ hưởng 1
        when(benefitService.getAllActive()).thenReturn(List.of(benefitPhuCap, benefitKhauTru));

        try (MockedStatic<SalaryMapper> mockedMapper = mockStatic(SalaryMapper.class)) {
            SalaryDTO mockDto = new SalaryDTO();
            mockDto.setAppliedBenefits(new ArrayList<>()); // Khởi tạo rỗng
            mockedMapper.when(() -> SalaryMapper.mapToSalaryDTO(any(Salary.class))).thenReturn(mockDto);

            // Act
            List<SalaryDTO> result = salaryService.getSalariesByMonth(8, 2024);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            // Phải có đủ 2 phúc lợi, 1 cái có giá trị, 1 cái là 0
            assertEquals(2, result.get(0).getAppliedBenefits().size());
        }
    }

    //================================================================================
    // Test cho phương thức generateMonthlySalaries
    //================================================================================

    @Test
    @DisplayName("Test generateMonthlySalaries - Tạo bảng lương tháng thành công")
    void generateMonthlySalaries_Success() {
        // Arrange
        when(employeeRepository.findAllActive()).thenReturn(List.of(employee));
        when(attendanceRecordRepository.findByEmployee_EmployeeIdAndMonthAndYear(1L, 8, 2024)).thenReturn(attendanceRecords);
        when(benefitRegistrationRepository.findByEmployee(employee)).thenReturn(benefitRegistrations);

        // Act
        salaryService.generateMonthlySalaries(8, 2024);

        // Assert & Verify
        ArgumentCaptor<Salary> captor = ArgumentCaptor.forClass(Salary.class);
        verify(salaryRepository, times(1)).save(captor.capture());

        Salary savedSalary = captor.getValue();
        assertNotNull(savedSalary);
        assertEquals(employee, savedSalary.getEmployee());

        // Kiểm tra tính toán lương
        BigDecimal hourlyRate = new BigDecimal("10000000").divide(BigDecimal.valueOf(208), 2, RoundingMode.HALF_UP); // 48076.92
        BigDecimal expectedProductionSalary = hourlyRate.multiply(BigDecimal.valueOf(8)); // 384615.36
        BigDecimal expectedOvertimeSalary = hourlyRate.multiply(BigDecimal.valueOf(2 * 2)); // 192307.68
        BigDecimal expectedAllowance = new BigDecimal("500000");
        BigDecimal expectedGross = expectedProductionSalary.add(expectedOvertimeSalary).add(expectedAllowance); // 1076923.04

        assertEquals(0, expectedProductionSalary.compareTo(savedSalary.getProductionSalary()));
        assertEquals(0, expectedOvertimeSalary.compareTo(savedSalary.getOvertimeSalary()));
        assertEquals(0, expectedGross.compareTo(savedSalary.getTotalIncome()));
        assertEquals(1, savedSalary.getSalaryBenefits().size());
    }

    //================================================================================
    // Test cho phương thức regenerateMonthlySalaries
    //================================================================================

    @Test
    @DisplayName("Test regenerateMonthlySalaries - Tái tạo bảng lương thành công")
    void regenerateMonthlySalaries_Success() {
        // Arrange
        LocalDate salaryMonth = LocalDate.of(2024, 8, 1);
        when(salaryRepository.existsBySalaryMonthAndLockedTrue(salaryMonth)).thenReturn(false);
        // Giả lập các hàm con được gọi bởi generateMonthlySalaries
        when(employeeRepository.findAllActive()).thenReturn(Collections.emptyList());

        // Act
        salaryService.regenerateMonthlySalaries(8, 2024);

        // Assert & Verify
        verify(salaryRepository, times(1)).existsBySalaryMonthAndLockedTrue(salaryMonth);
        verify(salaryRepository, times(1)).deleteBySalaryMonth(salaryMonth);
        verify(employeeRepository, times(1)).findAllActive(); // Xác nhận generateMonthlySalaries đã được gọi
    }

    @Test
    @DisplayName("Test regenerateMonthlySalaries - Ném ngoại lệ khi bảng lương đã khóa")
    void regenerateMonthlySalaries_ThrowsException_WhenLocked() {
        // Arrange
        LocalDate salaryMonth = LocalDate.of(2024, 8, 1);
        when(salaryRepository.existsBySalaryMonthAndLockedTrue(salaryMonth)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            salaryService.regenerateMonthlySalaries(8, 2024);
        });

        verify(salaryRepository, never()).deleteBySalaryMonth(any());
    }

    //================================================================================
    // Test cho phương thức lockSalariesByMonth
    //================================================================================

    @Test
    @DisplayName("Test lockSalariesByMonth - Khóa bảng lương thành công")
    void lockSalariesByMonth_Success() {
        // Arrange
        LocalDate salaryMonth = LocalDate.of(2024, 8, 1);
        salary.setLocked(false);
        when(salaryRepository.findBySalaryMonth(salaryMonth)).thenReturn(List.of(salary));

        // Act
        salaryService.lockSalariesByMonth(8, 2024, true);

        // Assert & Verify
        ArgumentCaptor<List<Salary>> captor = ArgumentCaptor.forClass(List.class);
        verify(salaryRepository, times(1)).saveAll(captor.capture());

        List<Salary> savedSalaries = captor.getValue();
        assertTrue(savedSalaries.get(0).isLocked());
    }
}
