package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.mapper.LinePMCMapper;
import sep490.com.example.hrms_backend.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceImplTest {
    // 1. Mock các dependency
    @Mock
    private LineRepository lineRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PositionRepository positionRepository;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private LineServiceImpl lineService;

    // 3. Khai báo dữ liệu mẫu
    private Department department;
    private Line line;
    private Employee oldLeader, newLeader;
    private Account oldLeaderAccount, newLeaderAccount;
    private Role roleEmployee, roleLineLeader;
    private Position positionCongNhan, positionToTruong;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Sản Xuất");

        oldLeader = new Employee();
        oldLeader.setEmployeeId(10L);
        oldLeader.setEmployeeName("Người cũ");

        newLeader = new Employee();
        newLeader.setEmployeeId(20L);
        newLeader.setEmployeeName("Người mới");

        line = new Line();
        line.setLineId(100L);
        line.setLineName("Chuyền A");
        line.setDepartment(department);
        line.setLeader(oldLeader); // Giả sử ban đầu có leader

        oldLeaderAccount = new Account();
        oldLeaderAccount.setEmployee(oldLeader);

        newLeaderAccount = new Account();
        newLeaderAccount.setEmployee(newLeader);

        roleEmployee = new Role();
        roleEmployee.setRoleName("ROLE_EMPLOYEE");

        roleLineLeader = new Role();
        roleLineLeader.setRoleName("ROLE_LINE_LEADER");

        positionCongNhan = new Position();
        positionCongNhan.setPositionName("Công Nhân");

        positionToTruong = new Position();
        positionToTruong.setPositionName("Tổ Trưởng");
    }

    //================================================================================
    // Test cho phương thức getDepartmentByLineId
    //================================================================================

    @Test
    @DisplayName("Test getDepartmentByLineId - Lấy phòng ban thành công")
    void getDepartmentByLineId_Success() {
        // Arrange
        when(lineRepository.findById(100L)).thenReturn(Optional.of(line));

        // Act
        DepartmentDTO result = lineService.getDepartmentByLineId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sản Xuất", result.getName());
    }

    //================================================================================
    // Test cho phương thức getAllLine
    //================================================================================

    @Test
    @DisplayName("Test getAllLine - Lấy tất cả chuyền khi không có tìm kiếm")
    void getAllLine_Success_NoSearch() {
        // Arrange
        when(lineRepository.findAll()).thenReturn(List.of(line));

        // Act
        try (MockedStatic<LinePMCMapper> mockedMapper = mockStatic(LinePMCMapper.class)) {
            mockedMapper.when(() -> LinePMCMapper.mapToLinePMCDto(any(Line.class)))
                    .thenReturn(new LinePMCDto());

            List<LinePMCDto> result = lineService.getAllLine(null);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(lineRepository, times(1)).findAll();
            verify(lineRepository, never()).findByLineNameContainingIgnoreCase(anyString());
        }
    }

    //================================================================================
    // Test cho phương thức getLineByLineId
    //================================================================================

    @Test
    @DisplayName("Test getLineByLineId - Lấy thành công chuyền có leader")
    void getLineByLineId_Success_WithLeader() {
        // Arrange
        when(lineRepository.findById(100L)).thenReturn(Optional.of(line));

        // Act
        LineDTO result = lineService.getLineByLineId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getLeaderId());
    }

    //================================================================================
    // Test cho phương thức assignLeaderToLine
    //================================================================================

    @Test
    @DisplayName("Test assignLeaderToLine - Gán leader mới thành công (có thay thế leader cũ)")
    void assignLeaderToLine_Success_ReplacingOldLeader() {
        // Arrange
        when(lineRepository.findById(100L)).thenReturn(Optional.of(line));
        when(accountRepository.findByEmployee_EmployeeId(10L)).thenReturn(Optional.of(oldLeaderAccount));
        when(roleRepository.findByRoleName("ROLE_EMPLOYEE")).thenReturn(Optional.of(roleEmployee));
        when(positionRepository.findByPositionName("Công Nhân")).thenReturn(positionCongNhan);

        when(employeeRepository.findById(20L)).thenReturn(Optional.of(newLeader));
        when(accountRepository.findByEmployee_EmployeeId(20L)).thenReturn(Optional.of(newLeaderAccount));
        when(roleRepository.findByRoleName("ROLE_LINE_LEADER")).thenReturn(Optional.of(roleLineLeader));
        when(positionRepository.findByPositionName("Tổ Trưởng")).thenReturn(positionToTruong);

        // Act
        lineService.assignLeaderToLine(100L, 20L, 2L);

        // Assert & Verify
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(2)).save(accountCaptor.capture());

        List<Account> savedAccounts = accountCaptor.getAllValues();

        // Kiểm tra việc "giáng chức" leader cũ (lần gọi đầu tiên)
        Account savedOldLeaderAccount = savedAccounts.get(0);
        assertEquals("ROLE_EMPLOYEE", savedOldLeaderAccount.getRole().getRoleName());

        // Kiểm tra việc "bổ nhiệm" leader mới (lần gọi thứ hai)
        Account savedNewLeaderAccount = savedAccounts.get(1);
        assertEquals("ROLE_LINE_LEADER", savedNewLeaderAccount.getRole().getRoleName());

        // Kiểm tra các lệnh save khác
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository, times(2)).save(employeeCaptor.capture());
        assertEquals("Công Nhân", employeeCaptor.getAllValues().get(0).getPosition().getPositionName());
        assertEquals("Tổ Trưởng", employeeCaptor.getAllValues().get(1).getPosition().getPositionName());

        ArgumentCaptor<Line> lineCaptor = ArgumentCaptor.forClass(Line.class);
        verify(lineRepository).save(lineCaptor.capture());
        assertEquals(20L, lineCaptor.getValue().getLeader().getEmployeeId());
    }

}
