package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.mapper.LinePMCMapper;
import sep490.com.example.hrms_backend.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineServiceImplTest {

    @Mock private LineRepository lineRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PositionRepository positionRepository;

    private LineServiceImpl service;

    private static MockedStatic<LinePMCMapper> mapperMock;

    @BeforeAll
    static void beforeAll() {
        mapperMock = mockStatic(LinePMCMapper.class);
    }

    @AfterAll
    static void afterAll() {
        mapperMock.close();
    }

    @BeforeEach
    void setUp() {
        service = new LineServiceImpl(lineRepository, employeeRepository, accountRepository, roleRepository, positionRepository);
    }

    // ===== Helpers =====
    private Department mkDept(Long id, String name) {
        Department d = new Department();
        d.setDepartmentId(id);
        d.setDepartmentName(name);
        return d;
    }

    private Position mkPos(String name) {
        Position p = new Position();
        p.setPositionName(name);
        return p;
    }

    private Employee mkEmp(Long id, String name) {
        Employee e = new Employee();
        e.setEmployeeId(id);
        e.setEmployeeName(name);
        return e;
    }

    private Account mkAcc(Long id, Employee e, Role r) {
        Account a = new Account();
        a.setAccountId(id);
        a.setEmployee(e);
        a.setRole(r);
        return a;
    }

    private Role mkRole(String name) {
        Role r = new Role();
        r.setRoleName(name);
        return r;
    }

    private Line mkLine(Long id, String name, Department dept, Employee leader) {
        Line l = new Line();
        l.setLineId(id);
        l.setLineName(name);
        l.setDepartment(dept);
        l.setLeader(leader);
        return l;
    }

    // ===== getDepartmentByLineId =====
    @Test
    void getDepartmentByLineId_returnsDTO() {
        Department dept = mkDept(10L, "QA");
        Line line = mkLine(1L, "Line A", dept, null);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

        DepartmentDTO dto = service.getDepartmentByLineId(1L);

        assertEquals(10L, dto.getId());
        assertEquals("QA", dto.getName());
    }

    @Test
    void getDepartmentByLineId_notFound_throws() {
        when(lineRepository.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.getDepartmentByLineId(99L));
        assertTrue(ex.getMessage().contains("Không tìm thấy tổ"));
    }


    // ===== getLineByLineId =====
    @Test
    void getLineByLineId_noLeader_returnsIdAndNameOnly() {
        Line line = mkLine(1L, "Line A", mkDept(1L, "D1"), null);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

        LineDTO dto = service.getLineByLineId(1L);
        assertEquals(1L, dto.getId());
        assertEquals("Line1", dto.getName());
        assertNull(dto.getLeaderId());
    }

    @Test
    void getLineByLineId_withLeader_returnsLeaderId() {
        Employee leader = mkEmp(100L, "Leader");
        Line line = mkLine(5L, "Line2", mkDept(2L, "D2"), leader);
        when(lineRepository.findById(5L)).thenReturn(Optional.of(line));

        LineDTO dto = service.getLineByLineId(5L);
        assertEquals(5L, dto.getId());
        assertEquals("Line B", dto.getName());
        assertEquals(100L, dto.getLeaderId());
    }

    @Test
    void getLineByLineId_notFound_throws() {
        when(lineRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getLineByLineId(404L));
    }

    // ===== assignLeaderToLine =====
    @Test
    void assignLeaderToLine_whenNoOldLeader_setsRoleAndPositionForNewLeader_andSaves() {
        // line chưa có leader
        Line line = mkLine(7L, "Line 7", mkDept(7L, "D7"), null);
        when(lineRepository.findById(7L)).thenReturn(Optional.of(line));

        // new leader
        Employee newLeader = mkEmp(200L, "Alice");
        when(employeeRepository.findById(200L)).thenReturn(Optional.of(newLeader));

        Account newLeaderAcc = mkAcc(2000L, newLeader, null);
        when(accountRepository.findByEmployee_EmployeeId(200L)).thenReturn(Optional.of(newLeaderAcc));

        Role roleLeader = mkRole("ROLE_LINE_LEADER");
        when(roleRepository.findByRoleName("ROLE_LINE_LEADER")).thenReturn(Optional.of(roleLeader));

        Position posLeader = mkPos("Tổ Trưởng");
        when(positionRepository.findByPositionName("Tổ Trưởng")).thenReturn(posLeader);

        // act
        service.assignLeaderToLine(7L, 200L);

        // assert: cập nhật cho leader mới
        assertEquals(posLeader, newLeader.getPosition());
        assertEquals(roleLeader, newLeaderAcc.getRole());
        assertEquals(newLeader, line.getLeader());

        // verify save
        verify(accountRepository).save(newLeaderAcc);
        verify(lineRepository).save(line);
        verify(employeeRepository).save(newLeader);

        // không đụng tới ROLE_EMPLOYEE vì không có old leader
        verify(roleRepository, never()).findByRoleName("ROLE_EMPLOYEE");
    }

    @Test
    void assignLeaderToLine_whenHasOldLeader_downgradesOldLeader_andUpgradesNewLeader() {
        // old leader đang giữ line
        Employee oldLeader = mkEmp(111L, "Bob");
        Account oldAcc = mkAcc(1110L, oldLeader, mkRole("ROLE_LINE_LEADER"));

        Line line = mkLine(9L, "Line 9", mkDept(9L, "D9"), oldLeader);
        when(lineRepository.findById(9L)).thenReturn(Optional.of(line));

        when(accountRepository.findByEmployee_EmployeeId(111L)).thenReturn(Optional.of(oldAcc));
        Role roleEmployee = mkRole("ROLE_EMPLOYEE");
        when(roleRepository.findByRoleName("ROLE_EMPLOYEE")).thenReturn(Optional.of(roleEmployee));
        Position posWorker = mkPos("Công Nhân");
        when(positionRepository.findByPositionName("Công Nhân")).thenReturn(posWorker);

        // new leader
        Employee newLeader = mkEmp(222L, "Alice");
        when(employeeRepository.findById(222L)).thenReturn(Optional.of(newLeader));
        Account newAcc = mkAcc(2220L, newLeader, null);
        when(accountRepository.findByEmployee_EmployeeId(222L)).thenReturn(Optional.of(newAcc));
        Role roleLineLeader = mkRole("ROLE_LINE_LEADER");
        when(roleRepository.findByRoleName("ROLE_LINE_LEADER")).thenReturn(Optional.of(roleLineLeader));
        Position posLeader = mkPos("Tổ Trưởng");
        when(positionRepository.findByPositionName("Tổ Trưởng")).thenReturn(posLeader);

        // act
        service.assignLeaderToLine(9L, 222L);

        // old leader bị hạ vai trò & vị trí
        assertEquals(roleEmployee, oldAcc.getRole());
        assertEquals(posWorker, oldLeader.getPosition());
        verify(accountRepository).save(oldAcc);
        verify(employeeRepository).save(oldLeader);

        // new leader được gán quyền & vị trí
        assertEquals(roleLineLeader, newAcc.getRole());
        assertEquals(posLeader, newLeader.getPosition());
        assertEquals(newLeader, line.getLeader());
        verify(accountRepository).save(newAcc);
        verify(lineRepository).save(line);
        verify(employeeRepository).save(newLeader);
    }

    @Test
    void assignLeaderToLine_lineNotFound_throws() {
        when(lineRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.assignLeaderToLine(1L, 2L));
        assertTrue(ex.getMessage().contains("Không tìm thấy tổ"));
    }

    @Test
    void assignLeaderToLine_missingOldLeaderAccount_throws() {
        // line có old leader
        Employee oldLeader = mkEmp(10L, "Old");
        Line line = mkLine(5L, "Line", mkDept(1L, "D"), oldLeader);
        when(lineRepository.findById(5L)).thenReturn(Optional.of(line));

        when(accountRepository.findByEmployee_EmployeeId(10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.assignLeaderToLine(5L, 20L));
        assertTrue(ex.getMessage().contains("Không tìm thấy tài khoản cho nhân viên này"));
    }

    @Test
    void assignLeaderToLine_newLeaderOrRoleMissing_throws() {
        Line line = mkLine(6L, "Line", mkDept(1L, "D"), null);
        when(lineRepository.findById(6L)).thenReturn(Optional.of(line));

        // không tìm thấy employee mới
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.assignLeaderToLine(6L, 999L));

        // nếu tìm thấy employee nhưng không có account/role -> cũng ném lỗi
        Employee e = mkEmp(30L, "New");
        when(employeeRepository.findById(30L)).thenReturn(Optional.of(e));
        when(accountRepository.findByEmployee_EmployeeId(30L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.assignLeaderToLine(6L, 30L));

        when(accountRepository.findByEmployee_EmployeeId(30L)).thenReturn(Optional.of(mkAcc(3000L, e, null)));
        when(roleRepository.findByRoleName("ROLE_LINE_LEADER")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.assignLeaderToLine(6L, 30L));
    }
}
