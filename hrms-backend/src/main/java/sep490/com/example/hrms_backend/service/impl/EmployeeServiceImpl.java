package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.dto.benefit.UpdateOriginalSalaryDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.enums.FormulaType;
import sep490.com.example.hrms_backend.exception.DuplicateEntryException;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.EmployeeMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.AccountService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements sep490.com.example.hrms_backend.service.EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LineRepository lineRepository;
    private final PositionRepository positionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final BenefitRegistrationRepository benefitRegistrationRepository;
    private final AccountRequestRepository accountRequestRepository;
    private final RoleRepository roleRepository;


    @Override
    public Page<EmployeeResponseDTO> getAllEmployees(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage;

        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository
                    .findByIsDeletedFalseAndEmployeeCodeContainingIgnoreCaseOrEmployeeNameContainingIgnoreCase(
                            search, search, pageable);
        } else {
            employeePage = employeeRepository.findByIsDeletedFalse(pageable);
        }

        List<EmployeeResponseDTO> dtos = employeePage.getContent().stream()
                .map(EmployeeMapper::mapToEmployeeResponseDTO)
                .toList();

        return new PageImpl<>(dtos, pageable, employeePage.getTotalElements());
    }

    @Override
    public String getNextEmployeeCode() {
        long total = employeeRepository.count();
        long hrCount = employeeRepository.countByPosition_PositionNameIgnoreCase("HR");
        long hrManagerCount = employeeRepository.countByPosition_PositionNameIgnoreCase("Trưởng Phòng Nhân Sự");
        System.out.println(hrManagerCount);
       long count = total - hrCount - hrManagerCount;
        long next = count + 1;
        return "ELTSSX" + String.format("%04d", next);
    }

    @Override
    public String getNextEmployeeCodeByPosition(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));

        String positionName = position.getPositionName().toUpperCase();

        String prefix;
        long count;

        if (positionName.equals("HR") || positionName.equals("HR_MANAGER")) {
            prefix = "ELTSHC";
            count = employeeRepository.countByPosition_PositionNameIgnoreCase("HR")
                    + employeeRepository.countByPosition_PositionNameIgnoreCase("Trưởng Phòng Nhân Sự");
            System.out.println("Count for HR and Manager " + count);
        } else {
            // Count all except HR and HR_MANAGER
            long total = employeeRepository.count();
            long hrCount = employeeRepository.countByPosition_PositionNameIgnoreCase("HR");
            long hrManagerCount = employeeRepository.countByPosition_PositionNameIgnoreCase("HR_MANAGER");

            prefix = "ELTSSX";
            count = total - hrCount - hrManagerCount;
        }

        return prefix + String.format("%04d", count + 1);
    }


    @Override
    public List<EmployeeResponseDTO> getEmployeesNotInLine(Long lineId, String search) {
        List<Employee> employees;

        if (search == null || search.trim().isEmpty()) {
            employees = employeeRepository.findEmployeesNotInLine(lineId);
        } else {
            employees = employeeRepository.findEmployeesNotInLineWithSearch(lineId, "%" + search.trim().toLowerCase() + "%");
        }

        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<EmployeeResponseDTO> getEmployeeByLineId(Long id) {

        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line", "id", id));
        List<EmployeeResponseDTO> employees = line.getEmployees().stream()
                .map(EmployeeMapper::mapToEmployeeResponseDTO)
                .sorted((e1, e2) -> {

                    if ("Tổ Trưởng".equalsIgnoreCase(e1.getPositionName()) && !"Tổ Trưởng".equalsIgnoreCase(e2.getPositionName())) {
                        return -1;
                    } else if (!"Tổ Trưởng".equalsIgnoreCase(e1.getPositionName()) && "Tổ Trưởng".equalsIgnoreCase(e2.getPositionName())) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());


        return employees;
    }

    @Override
    @Transactional
    public void addEmployeesToLine(Long lineId, List<Long> employeeIds) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new ResourceNotFoundException("Line", "id", lineId));

        Position p = positionRepository.findByPositionName("Công Nhân");

        Role r = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyền ROLE_EMPLOYEE"));


        for (Long employeeId : employeeIds) {
            Employee e = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

            if (!e.getPosition().getPositionName().equalsIgnoreCase("Công Nhân")) {
                e.setPosition(p);
            }

            if (!e.getAccount().getRole().getRoleName().equalsIgnoreCase("ROLE_EMPLOYEE")) {
                Account a = e.getAccount();
                a.setRole(r);
                accountRepository.save(a);
            }
            Line oldLine = e.getLine();
            if (oldLine != null) {
                Employee currentLeader = oldLine.getLeader();
                if (currentLeader != null && currentLeader.getEmployeeId().equals(e.getEmployeeId())) {
                    oldLine.setLeader(null);
                    lineRepository.save(oldLine);
                }
            }

            e.setLine(line);
            employeeRepository.save(e);

        }
    }


    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        checkDuplicateFieldsForCreate(dto);
        checkPositionRequirements(dto.getDepartmentId(), dto.getPositionId());
        if (dto.getEmployeeCode() == null || dto.getEmployeeCode().isBlank()) {
            dto.setEmployeeCode(getNextEmployeeCodeByPosition(dto.getPositionId()));
        }


        if (!positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Chức vụ không thuộc phòng ban đã chọn");
        }

        Employee employee = EmployeeMapper.mapToEmployee(dto);
        employee.setDepartment(fetchDepartment(dto.getDepartmentId()));
        employee.setPosition(fetchPosition(dto.getPositionId()));
        employee = employeeRepository.save(employee);
        AccountRequest accountRequest = AccountRequest.builder()
                .employee(employee)
                .requestedAt(LocalDateTime.now())
                .approved(null)
                .build();

        accountRequestRepository.save(accountRequest);
        return EmployeeMapper.mapToEmployeeResponseDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        checkDuplicateFieldsForUpdate(dto, id, employee);
        checkPositionRequirements(dto.getDepartmentId(), dto.getPositionId());

        if (!positionRepository.existsDepartmentPositionMapping(dto.getDepartmentId(), dto.getPositionId())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Chức vụ không thuộc phòng ban đã chọn");
        }

        EmployeeMapper.updateEmployeeFromUpdateDTO(dto, employee);
        employee.setDepartment(fetchDepartment(dto.getDepartmentId()));
        employee.setPosition(fetchPosition(dto.getPositionId()));
        if (dto.getCccdFrontImage() != null) {
            employee.setCccdFrontImage(dto.getCccdFrontImage());
        }
        if (dto.getCccdBackImage() != null) {
            employee.setCccdBackImage(dto.getCccdBackImage());
        }


        employee = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeResponseDTO(employee);
    }

    @Override
    public EmployeeDetailDTO getEmployeeDetailById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return EmployeeMapper.mapToEmployeeDetailDTO(employee);
    }


    private void checkDuplicateFieldsForCreate(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            throw new DuplicateEntryException("Mã nhân viên đã tồn tại trong hệ thống");
        }
        if (employeeRepository.existsByCitizenId(dto.getCitizenId())) {
            throw new DuplicateEntryException("Số CMND/CCCD đã tồn tại trong hệ thống");
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntryException("Email đã tồn tại trong hệ thống");
        }
    }

    private void checkDuplicateFieldsForUpdate(EmployeeUpdateDTO dto, Long id, Employee employee) {
        if (!safeEquals(dto.getCitizenId(), employee.getCitizenId())) {
            if (employeeRepository.existsByCitizenIdAndEmployeeIdNot(dto.getCitizenId(), id)) {
                throw new DuplicateEntryException("Số CMND/CCCD đã tồn tại trong hệ thống");
            }
        }
        if (!safeEquals(dto.getEmail(), employee.getEmail())) {
            if (employeeRepository.existsByEmailAndEmployeeIdNot(dto.getEmail(), id)) {
                throw new DuplicateEntryException("Email đã tồn tại trong hệ thống");
            }
        }
    }

    private void checkPositionRequirements(Long departmentId, Long positionId) {
        List<Position> positions = positionRepository.findByDepartments_DepartmentId(departmentId);
        if (!positions.isEmpty() && positionId == null) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Vui lòng chọn chức vụ cho nhân viên");
        }
    }

    private boolean safeEquals(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }

    private Department fetchDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    private Position fetchPosition(Long id) {
        if (id == null) return null;
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", id));
    }

    @Override
    public EmployeeDetailDTO getOwnProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));

        Employee employee = account.getEmployee();
        if (employee == null) {
            throw new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ nhân viên");
        }

        return EmployeeMapper.mapToEmployeeDetailDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeDetailDTO updateOwnProfile(EmployeeOwnProfileUpdateDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByAccount_Username(username)
                .orElseThrow(() -> new HRMSAPIException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ nhân viên"));

        if (dto.getEmployeeName() != null) employee.setEmployeeName(dto.getEmployeeName());
        if (dto.getPhoneNumber() != null) employee.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if (dto.getAddress() != null) employee.setAddress(dto.getAddress());

        employee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDetailDTO(employee);
    }

    @Override
    public void softDeleteEmployee(Long id) {
        Employee employee = employeeRepository.findByEmployeeIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Employee not found or already deleted"));
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeeByDepartmentId(Long id) {
        Department department = departmentRepository.findById(id).orElse(null);

        List<EmployeeResponseDTO> employees = department.getEmployees().stream()
                .map(EmployeeMapper::mapToEmployeeResponseDTO)
                .collect(Collectors.toList());
        return employees;
    }

    @Override
    public ByteArrayInputStream exportEmployeesToExcel() {
        List<EmployeeDetailDTO> list = employeeRepository.findByIsDeletedFalse().stream()
                .map(EmployeeMapper::mapToEmployeeDetailDTO)
                .collect(Collectors.toList());

        try {
            return sep490.com.example.hrms_backend.utils.EmployeeExcelExporter.export(list);
        } catch (IOException e) {
            throw new HRMSAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi xuất file Excel");
        }
    }

    @Override
    public List<GenderDistributionDTO> getGenderDistribution(LocalDate startDate, LocalDate endDate) {
        List<Object[]> result = employeeRepository.findGenderDistributionByDateRange(startDate, endDate);
        return result.stream()
                .map(r -> new GenderDistributionDTO((String) r[0], (Long) r[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDistributionDTO> getDepartmentDistribution(LocalDate startDate, LocalDate endDate) {
        List<Object[]> result = employeeRepository.findDepartmentDistributionByDateRange(startDate, endDate);
        return result.stream()
                .map(r -> new DepartmentDistributionDTO((String) r[0], (Long) r[1]))
                .collect(Collectors.toList());
    }


}
