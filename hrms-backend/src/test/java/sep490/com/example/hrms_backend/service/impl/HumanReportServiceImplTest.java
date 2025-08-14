package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.mapper.AttendenceReportMapper;
import sep490.com.example.hrms_backend.repository.AttendanceRecordRepository;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HumanReportServiceImplTest {
    // 1. Mock các dependency
    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private LineRepository lineRepository;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private HumanReportServiceImpl humanReportService;

    // 3. Khai báo dữ liệu mẫu
    private Department deptIT, deptHR, deptSanXuat;
    private Line lineA;
    private List<AttendanceRecord> attendanceRecords;
    private List<AttendanceMonthlyViewDTO> dtoList;
    private LocalDate testDate;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 8, 12);

        deptIT = new Department();
        deptIT.setDepartmentId(1L);
        deptIT.setDepartmentName("IT");

        deptHR = new Department();
        deptHR.setDepartmentId(2L);
        deptHR.setDepartmentName("Nhân sự");

        deptSanXuat = new Department();
        deptSanXuat.setDepartmentId(3L);
        deptSanXuat.setDepartmentName("Sản Xuất");

        lineA = new Line();
        lineA.setLineId(100L);
        lineA.setLineName("Chuyền A");

        attendanceRecords = List.of(new AttendanceRecord(), new AttendanceRecord());
        dtoList = List.of(new AttendanceMonthlyViewDTO(), new AttendanceMonthlyViewDTO());
    }

    //================================================================================
    // Test cho phương thức getFullEmp
    //================================================================================

    @Test
    @DisplayName("Test getFullEmp - Lấy thành công danh sách nhân viên đi làm đầy đủ")
    void getFullEmp_Success() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(List.of(deptIT, deptHR, deptSanXuat));
        when(departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất")).thenReturn(deptSanXuat);
        when(lineRepository.findAll()).thenReturn(List.of(lineA));

        // Giả lập kết quả cho các phòng ban (trừ Sản Xuất)
        when(attendanceRecordRepository.getEmployees(testDate, deptIT.getDepartmentId())).thenReturn(attendanceRecords);
        when(attendanceRecordRepository.getEmployees(testDate, deptHR.getDepartmentId())).thenReturn(Collections.emptyList());

        // Giả lập kết quả cho chuyền
        when(attendanceRecordRepository.findAllEmpByDateLine(testDate, lineA.getLineId())).thenReturn(attendanceRecords);

        // Mock static mapper
        try (MockedStatic<AttendenceReportMapper> mockedMapper = mockStatic(AttendenceReportMapper.class)) {
            mockedMapper.when(() -> AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(anyList())).thenReturn(dtoList);

            // Act
            Map<String, List<AttendanceMonthlyViewDTO>> result = humanReportService.getFullEmp(testDate);

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size(), "Phải có 3 key: IT, Nhân sự, Chuyền A");
            assertTrue(result.containsKey("IT"));
            assertTrue(result.containsKey("Nhân sự"));
            assertTrue(result.containsKey("Chuyền A"));
            assertEquals(2, result.get("IT").size());
            assertEquals(2, result.get("Nhân sự").size()); // Mapper trả về dtoList cố định
            assertEquals(2, result.get("Chuyền A").size());
        }
    }

    //================================================================================
    // Test cho phương thức getListEmpAbsent
    //================================================================================

    @Test
    @DisplayName("Test getListEmpAbsent - Lấy thành công danh sách nhân viên vắng mặt")
    void getListEmpAbsent_Success() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(List.of(deptIT, deptSanXuat));
        when(departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất")).thenReturn(deptSanXuat);
        when(lineRepository.findAll()).thenReturn(List.of(lineA));
        when(attendanceRecordRepository.findAbsentEmpByDateDepartment(testDate, deptIT.getDepartmentId())).thenReturn(attendanceRecords);
        when(attendanceRecordRepository.findAbsentEmpByDateLine(testDate, lineA.getLineId())).thenReturn(attendanceRecords);

        // Mock static mapper
        try (MockedStatic<AttendenceReportMapper> mockedMapper = mockStatic(AttendenceReportMapper.class)) {
            mockedMapper.when(() -> AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(anyList())).thenReturn(dtoList);

            // Act
            Map<String, List<AttendanceMonthlyViewDTO>> result = humanReportService.getListEmpAbsent(testDate);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.containsKey("IT"));
            assertTrue(result.containsKey("Chuyền A"));

            // Verify
            verify(attendanceRecordRepository, times(1)).findAbsentEmpByDateDepartment(testDate, deptIT.getDepartmentId());
            verify(attendanceRecordRepository, times(1)).findAbsentEmpByDateLine(testDate, lineA.getLineId());
        }
    }

    //================================================================================
    // Test cho phương thức getListEmpAbsentKL
    //================================================================================

    @Test
    @DisplayName("Test getListEmpAbsentKL - Lấy thành công danh sách nhân viên vắng không lý do")
    void getListEmpAbsentKL_Success() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(List.of(deptIT, deptSanXuat));
        when(departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất")).thenReturn(deptSanXuat);
        when(lineRepository.findAll()).thenReturn(List.of(lineA));
        when(attendanceRecordRepository.findAbsentEmpByDateDepartmentKL(testDate, deptIT.getDepartmentId())).thenReturn(attendanceRecords);
        when(attendanceRecordRepository.findAbsentEmpByDateLineKL(testDate, lineA.getLineId())).thenReturn(attendanceRecords);

        // Mock static mapper
        try (MockedStatic<AttendenceReportMapper> mockedMapper = mockStatic(AttendenceReportMapper.class)) {
            mockedMapper.when(() -> AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(anyList())).thenReturn(dtoList);

            // Act
            Map<String, List<AttendanceMonthlyViewDTO>> result = humanReportService.getListEmpAbsentKL(testDate);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());

            // Verify
            verify(attendanceRecordRepository, times(1)).findAbsentEmpByDateDepartmentKL(testDate, deptIT.getDepartmentId());
            verify(attendanceRecordRepository, times(1)).findAbsentEmpByDateLineKL(testDate, lineA.getLineId());
        }
    }
}
