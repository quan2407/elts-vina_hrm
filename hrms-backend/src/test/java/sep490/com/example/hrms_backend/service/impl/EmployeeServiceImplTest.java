package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.enums.Gender;
import sep490.com.example.hrms_backend.exception.DuplicateEntryException;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;
import sep490.com.example.hrms_backend.service.AccountService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountService accountService;

    private void setAuthenticatedUsername(String username) {
        SecurityContextHolder.clearContext();
        var auth = new UsernamePasswordAuthenticationToken(username, "N/A", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // =========================
    // Tests cho updateOwnProfile
    // =========================

    @Test
    void testUpdateOwnProfile_Success_UpdateAllFields() {
        // Arrange
        String username = "johndoe";
        setAuthenticatedUsername(username);

        Employee existing = new Employee();
        existing.setEmployeeId(10L);
        existing.setEmployeeName("Old Name");
        existing.setPhoneNumber("0900000000");
        existing.setEmail("old@example.com");
        existing.setAddress("Old Address");

        when(employeeRepository.findByAccount_Username(username)).thenReturn(Optional.of(existing));
        // Trả về đúng entity đã cập nhật
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeOwnProfileUpdateDTO dto = EmployeeOwnProfileUpdateDTO.builder()
                .employeeName("New Name")
                .phoneNumber("0912345678")
                .email("new@example.com")
                .address("New Address")
                .build();

        // Act
        EmployeeDetailDTO result = employeeService.updateOwnProfile(dto);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getEmployeeName());
        assertEquals("0912345678", result.getPhoneNumber());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("New Address", result.getAddress());

        verify(employeeRepository).findByAccount_Username(username);
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        Employee saved = captor.getValue();
        assertEquals("New Name", saved.getEmployeeName());
        assertEquals("0912345678", saved.getPhoneNumber());
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("New Address", saved.getAddress());
    }

    @Test
    void testUpdateOwnProfile_Success_PartialUpdate_OnlyPhone() {
        // Arrange
        String username = "alice";
        setAuthenticatedUsername(username);

        Employee existing = new Employee();
        existing.setEmployeeId(11L);
        existing.setEmployeeName("Alice Old");
        existing.setPhoneNumber("0901111111");
        existing.setEmail("alice@old.com");
        existing.setAddress("Addr Old");

        when(employeeRepository.findByAccount_Username(username)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeOwnProfileUpdateDTO dto = EmployeeOwnProfileUpdateDTO.builder()
                .phoneNumber("0999999999") // chỉ update phone
                .build();

        // Act
        EmployeeDetailDTO result = employeeService.updateOwnProfile(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Alice Old", result.getEmployeeName()); // giữ nguyên
        assertEquals("0999999999", result.getPhoneNumber()); // đã đổi
        assertEquals("alice@old.com", result.getEmail());    // giữ nguyên
        assertEquals("Addr Old", result.getAddress());       // giữ nguyên

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        Employee saved = captor.getValue();
        assertEquals("Alice Old", saved.getEmployeeName());
        assertEquals("0999999999", saved.getPhoneNumber());
        assertEquals("alice@old.com", saved.getEmail());
        assertEquals("Addr Old", saved.getAddress());
    }

    @Test
    void testUpdateOwnProfile_Success_EmptyStringOverrides() {
        // Trường hợp dto có chuỗi rỗng (không null) -> service vẫn set (do chỉ check null)
        String username = "bob";
        setAuthenticatedUsername(username);

        Employee existing = new Employee();
        existing.setEmployeeId(12L);
        existing.setEmployeeName("Bob Old");
        existing.setPhoneNumber("0902222222");
        existing.setEmail("bob@old.com");
        existing.setAddress("Addr Old");

        when(employeeRepository.findByAccount_Username(username)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeOwnProfileUpdateDTO dto = EmployeeOwnProfileUpdateDTO.builder()
                .employeeName("")     // set rỗng
                .address("")          // set rỗng
                .email("bob@new.com") // set giá trị mới
                .build();

        EmployeeDetailDTO result = employeeService.updateOwnProfile(dto);

        assertNotNull(result);
        assertEquals("", result.getEmployeeName());
        assertEquals("", result.getAddress());
        assertEquals("bob@new.com", result.getEmail());
        assertEquals("0902222222", result.getPhoneNumber()); // không đổi
    }

    @Test
    void testUpdateOwnProfile_Fail_EmployeeNotFound() {
        // Arrange
        String username = "ghost";
        setAuthenticatedUsername(username);

        when(employeeRepository.findByAccount_Username(username)).thenReturn(Optional.empty());

        EmployeeOwnProfileUpdateDTO dto = EmployeeOwnProfileUpdateDTO.builder()
                .employeeName("Does Not Matter")
                .build();

        // Act + Assert
        HRMSAPIException ex = assertThrows(HRMSAPIException.class, () -> employeeService.updateOwnProfile(dto));
        assertEquals("Không tìm thấy hồ sơ nhân viên", ex.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateOwnProfile_Success_NoFieldsChanged_StillSaves() {
        // Trường hợp tất cả trường null -> service vẫn gọi save (giữ nguyên)
        String username = "nochange";
        setAuthenticatedUsername(username);

        Employee existing = new Employee();
        existing.setEmployeeId(13L);
        existing.setEmployeeName("Name");
        existing.setPhoneNumber("0903333333");
        existing.setEmail("nochange@ex.com");
        existing.setAddress("Addr");

        when(employeeRepository.findByAccount_Username(username)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeOwnProfileUpdateDTO dto = new EmployeeOwnProfileUpdateDTO(); // tất cả null

        EmployeeDetailDTO result = employeeService.updateOwnProfile(dto);

        assertNotNull(result);
        assertEquals("Name", result.getEmployeeName());
        assertEquals("0903333333", result.getPhoneNumber());
        assertEquals("nochange@ex.com", result.getEmail());
        assertEquals("Addr", result.getAddress());
        verify(employeeRepository).save(any(Employee.class));
    }

    private EmployeeRequestDTO buildValidEmployeeDTO() {
        return EmployeeRequestDTO.builder()
                .employeeCode("ELTSSX9999")
                .employeeName("Nguyen Van Test")
                .gender(Gender.NAM)
                .dob(LocalDate.of(1990, 1, 1))
                .placeOfBirth("Hanoi")
                .originPlace("Hanoi")
                .nationality("Vietnam")
                .citizenId("123456789")
                .citizenIssueDate(LocalDate.of(2010, 1, 1))
                .citizenExpiryDate(LocalDate.of(2030, 1, 1))
                .address("Address")
                .currentAddress("Current Address")
                .ethnicity("Kinh")
                .religion("Không")
                .educationLevel("12/12")
                .specializedLevel("Cử nhân")
                .trainingMajor("CNTT")
                .startWorkAt(LocalDate.of(2020, 1, 1))
                .phoneNumber("0901234567")
                .email("a@example.com")
                .departmentId(1L)
                .positionId(2L)
                .build();
    }





    @Test
    void testCreateEmployee_Fail_DuplicateEmployeeCode() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        Mockito.when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(true);

        Exception ex = assertThrows(DuplicateEntryException.class, () -> employeeService.createEmployee(dto));
        assertEquals("Mã nhân viên đã tồn tại trong hệ thống", ex.getMessage());
    }

    @Test
    void testCreateEmployee_Fail_DuplicateCitizenId() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        Mockito.when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(dto.getCitizenId())).thenReturn(true);

        Exception ex = assertThrows(DuplicateEntryException.class, () -> employeeService.createEmployee(dto));
        assertEquals("Số CMND/CCCD đã tồn tại trong hệ thống", ex.getMessage());
    }

    @Test
    void testCreateEmployee_Fail_DuplicateEmail() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        Mockito.when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(dto.getCitizenId())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        Exception ex = assertThrows(DuplicateEntryException.class, () -> employeeService.createEmployee(dto));
        assertEquals("Email đã tồn tại trong hệ thống", ex.getMessage());
    }

    @Test
    void testCreateEmployee_Fail_PositionNotInDepartment() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        Mockito.when(employeeRepository.existsByEmployeeCode(Mockito.any())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(Mockito.any())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(Mockito.any())).thenReturn(false);
        Mockito.when(positionRepository.existsDepartmentPositionMapping(1L, 2L)).thenReturn(false);

        Exception ex = assertThrows(HRMSAPIException.class, () -> employeeService.createEmployee(dto));
        assertEquals("Chức vụ không thuộc phòng ban đã chọn", ex.getMessage());
    }

    @Test
    void testCreateEmployee_Fail_DepartmentNotFound() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        dto.setDepartmentId(999L);

        Mockito.when(employeeRepository.existsByEmployeeCode(Mockito.any())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(Mockito.any())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(Mockito.any())).thenReturn(false);
        Mockito.when(positionRepository.existsDepartmentPositionMapping(999L, dto.getPositionId())).thenReturn(true);
        Mockito.when(positionRepository.findByDepartments_DepartmentId(999L)).thenReturn(List.of(new Position()));
        Mockito.when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> employeeService.createEmployee(dto));
        assertTrue(ex.getMessage().contains("Department"));
    }

    @Test
    void testCreateEmployee_Fail_PositionRequiredButMissing() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        dto.setPositionId(null);

        Mockito.when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(dto.getCitizenId())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(positionRepository.findByDepartments_DepartmentId(dto.getDepartmentId())).thenReturn(List.of(new Position()));

        Exception ex = assertThrows(HRMSAPIException.class, () -> employeeService.createEmployee(dto));
        assertEquals("Vui lòng chọn chức vụ cho nhân viên", ex.getMessage());
    }

    @Test
    void testCreateEmployee_Fail_PositionNotFound() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();
        dto.setPositionId(999L); // Không tồn tại

        Mockito.when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(dto.getCitizenId())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), 999L)).thenReturn(true);
        Mockito.when(positionRepository.findByDepartments_DepartmentId(dto.getDepartmentId())).thenReturn(List.of(new Position()));
        Mockito.when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(new Department()));
        Mockito.when(positionRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> employeeService.createEmployee(dto));
        assertTrue(ex.getMessage().contains("Position"));
    }
    private EmployeeUpdateDTO buildValidUpdateDTO() {
        return EmployeeUpdateDTO.builder()
                .employeeName("Nguyen Van Updated")
                .gender(Gender.NAM)
                .dob(LocalDate.of(1990, 1, 1))
                .placeOfBirth("HCM")
                .originPlace("HCM")
                .nationality("Vietnam")
                .citizenId("987654321")
                .citizenIssueDate(LocalDate.of(2011, 1, 1))
                .citizenExpiryDate(LocalDate.of(2031, 1, 1))
                .address("New address")
                .currentAddress("New current address")
                .ethnicity("Kinh")
                .religion("Không")
                .educationLevel("12/12")
                .specializedLevel("Kỹ sư")
                .trainingMajor("CNTT")
                .foreignLanguages("English")
                .trainingType("Chính quy")
                .startWorkAt(LocalDate.of(2020, 1, 1))
                .phoneNumber("0909988776")
                .email("updated@example.com")
                .departmentId(1L)
                .positionId(2L)
                .build();
    }

    @Test
    void testUpdateEmployee_Success() {
        Long id = 100L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();

        Employee employee = new Employee();
        employee.setEmployeeId(id);
        employee.setCitizenId("111222333");  // khác với dto
        employee.setEmail("old@example.com");

        Department department = new Department();
        department.setDepartmentId(1L);
        Position position = new Position();
        position.setPositionId(2L);

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.existsByCitizenIdAndEmployeeIdNot(dto.getCitizenId(), id)).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmailAndEmployeeIdNot(dto.getEmail(), id)).thenReturn(false);
        Mockito.when(positionRepository.findByDepartments_DepartmentId(dto.getDepartmentId())).thenReturn(List.of(position));
        Mockito.when(positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())).thenReturn(true);
        Mockito.when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(department));
        Mockito.when(positionRepository.findById(dto.getPositionId())).thenReturn(Optional.of(position));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO result = employeeService.updateEmployee(id, dto);

        assertNotNull(result);
        assertEquals(dto.getEmail(), employee.getEmail());
    }
    @Test
    void testUpdateEmployee_Fail_EmployeeNotFound() {
        Long id = 999L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(id, dto));
        assertTrue(ex.getMessage().contains("Employee"));
    }
    @Test
    void testUpdateEmployee_Fail_DuplicateCitizenId() {
        Long id = 1L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();

        Employee employee = new Employee();
        employee.setEmployeeId(id);
        employee.setCitizenId("original"); // khác với dto

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.existsByCitizenIdAndEmployeeIdNot(dto.getCitizenId(), id)).thenReturn(true);

        Exception ex = assertThrows(DuplicateEntryException.class, () -> employeeService.updateEmployee(id, dto));
        assertEquals("Số CMND/CCCD đã tồn tại trong hệ thống", ex.getMessage());
    }
    @Test
    void testUpdateEmployee_Fail_DuplicateEmail() {
        Long id = 1L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();

        Employee employee = new Employee();
        employee.setEmployeeId(id);
        employee.setCitizenId(dto.getCitizenId());
        employee.setEmail("old@example.com");
        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.existsByEmailAndEmployeeIdNot(dto.getEmail(), id)).thenReturn(true);

        Exception ex = assertThrows(DuplicateEntryException.class, () -> employeeService.updateEmployee(id, dto));
        assertEquals("Email đã tồn tại trong hệ thống", ex.getMessage());
    }
    @Test
    void testUpdateEmployee_Fail_PositionNotInDepartment() {
        Long id = 1L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();

        Employee employee = new Employee();
        employee.setEmployeeId(id);
        employee.setCitizenId(dto.getCitizenId());
        employee.setEmail(dto.getEmail());

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        Mockito.when(positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId()))
                .thenReturn(false);

        Exception ex = assertThrows(HRMSAPIException.class, () -> employeeService.updateEmployee(id, dto));
        assertEquals("Chức vụ không thuộc phòng ban đã chọn", ex.getMessage());
    }

    @Test
    void testUpdateEmployee_Fail_PositionRequiredButMissing() {
        Long id = 1L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();
        dto.setPositionId(null); // Không có chức vụ

        Employee employee = new Employee();
        employee.setEmployeeId(id);
        employee.setCitizenId(dto.getCitizenId());
        employee.setEmail(dto.getEmail());

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        Mockito.when(positionRepository.findByDepartments_DepartmentId(dto.getDepartmentId()))
                .thenReturn(List.of(new Position()));

        Exception ex = assertThrows(HRMSAPIException.class, () -> employeeService.updateEmployee(id, dto));
        assertEquals("Vui lòng chọn chức vụ cho nhân viên", ex.getMessage());
    }

    @Test
    void testUpdateEmployee_Fail_DepartmentNotFound() {
        Long id = 1L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();

        Employee employee = new Employee();
        employee.setEmployeeId(id);

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        Mockito.when(positionRepository.findById(dto.getPositionId())).thenReturn(Optional.of(new Position()));
        lenient().when(employeeRepository.existsByCitizenIdAndEmployeeIdNot(dto.getCitizenId(), id)).thenReturn(false);
        lenient().when(employeeRepository.existsByEmailAndEmployeeIdNot(dto.getEmail(), id)).thenReturn(false);
        lenient().when(positionRepository.findByDepartments_DepartmentId(dto.getDepartmentId())).thenReturn(List.of(new Position()));
        lenient().when(positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())).thenReturn(true);

        Mockito.when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(id, dto));
        assertTrue(ex.getMessage().contains("Department"));
    }

    @Test
    void testUpdateEmployee_Fail_PositionNotFound() {
        Long id = 1L;
        EmployeeUpdateDTO dto = buildValidUpdateDTO();
        dto.setPositionId(999L);

        // Mock bắt buộc
        Employee employee = new Employee();
        employee.setEmployeeId(id);

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        lenient().when(employeeRepository.existsByCitizenIdAndEmployeeIdNot(dto.getCitizenId(), id)).thenReturn(false);
        lenient().when(employeeRepository.existsByEmailAndEmployeeIdNot(dto.getEmail(), id)).thenReturn(false);
        lenient().when(positionRepository.findByDepartments_DepartmentId(dto.getDepartmentId())).thenReturn(List.of(new Position()));
        lenient().when(positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())).thenReturn(true);
        lenient().when(departmentRepository.findById(dto.getDepartmentId())).thenReturn(Optional.of(new Department())); // ✅ BỔ SUNG DÒNG NÀY


        Mockito.when(positionRepository.findById(dto.getPositionId())).thenReturn(Optional.empty());


        Exception ex = assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(id, dto));
        System.out.println(ex.getMessage());
        assertTrue(ex.getMessage().contains("Position"));
        assertTrue(ex.getMessage().contains("id"));
    }



}
