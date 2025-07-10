package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

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
    void testCreateEmployee_Success() {
        EmployeeRequestDTO dto = buildValidEmployeeDTO();

        Department department = new Department();
        department.setDepartmentId(dto.getDepartmentId());

        Position position = new Position();
        position.setPositionId(dto.getPositionId());

        Employee savedEmployee = new Employee();
        savedEmployee.setEmployeeCode(dto.getEmployeeCode());
        savedEmployee.setEmployeeName(dto.getEmployeeName());
        savedEmployee.setEmail(dto.getEmail());

        Mockito.when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(false);
        Mockito.when(employeeRepository.existsByCitizenId(dto.getCitizenId())).thenReturn(false);
        Mockito.when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(positionRepository.existsDepartmentPositionMapping(1L, 2L)).thenReturn(true);
        Mockito.when(positionRepository.findByDepartments_DepartmentId(1L)).thenReturn(List.of(position));
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        Mockito.when(positionRepository.findById(2L)).thenReturn(Optional.of(position));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponseDTO result = employeeService.createEmployee(dto);
        assertNotNull(result);
        assertEquals(dto.getEmployeeCode(), result.getEmployeeCode());
        assertEquals(dto.getEmployeeName(), result.getEmployeeName());
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
                .image("img.jpg")
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
