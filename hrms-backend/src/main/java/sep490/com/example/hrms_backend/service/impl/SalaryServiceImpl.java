package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.SalaryBenefitDTO;
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
import sep490.com.example.hrms_backend.service.SalaryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final BenefitRegistrationRepository benefitRegistrationRepository;
    private final BenefitService benefitService;


    @Override
    public List<SalaryDTO> getSalariesByMonth(int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        List<Salary> salaries = salaryRepository.findBySalaryMonth(firstDay);
        List<Benefit> allBenefits = benefitService.getAllActive();
        return salaries.stream()
                .map(salary -> {
                    SalaryDTO dto = SalaryMapper.mapToSalaryDTO(salary);

                    List<SalaryBenefitDTO> fullBenefits = allBenefits.stream()
                            .map(benefit -> dto.getAppliedBenefits().stream()
                                    .filter(applied -> applied.getTitle().equals(benefit.getTitle()))
                                    .findFirst()
                                    .orElse(
                                            SalaryBenefitDTO.builder()
                                                    .title(benefit.getTitle())
                                                    .type(benefit.getBenefitType())
                                                    .amount(BigDecimal.ZERO)
                                                    .build()
                                    ))
                            .toList();

                    dto.setAppliedBenefits(fullBenefits);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<SalaryDTO> getEmpSalariesByMonth(Long employeeId, int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        List<Salary> salaries = salaryRepository.findBySalaryMonth(firstDay);
        List<Salary> results = new ArrayList<>();
        for (Salary salary : salaries) {
            if (salary.getEmployee().getEmployeeId().equals(employeeId)) {
                results.add(salary);
            }
        }
        return results.stream()
                .map(SalaryMapper::mapToSalaryDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void generateMonthlySalaries(int month, int year) {
        LocalDate salaryMonth = LocalDate.of(year, month, 1);
        List<Employee> employees = employeeRepository.findAllActive();

        for (Employee employee : employees) {
            List<AttendanceRecord> records = attendanceRecordRepository
                    .findByEmployee_EmployeeIdAndMonthAndYear(employee.getEmployeeId(), month, year);

            float totalDayHours = 0f;
            float otHours = 0f;
            float weekendHours = 0f;
            float holidayHours = 0f;

            for (AttendanceRecord r : records) {
                float day = parseHour(r.getDayShift());
                float ot = parseHour(r.getOtShift());
                float weekend = parseHour(r.getWeekendShift());
                float holiday = parseHour(r.getHolidayShift());

                totalDayHours += day;
                otHours += ot;
                weekendHours += weekend;
                holidayHours += holiday;
            }

            float totalWorkingHours = totalDayHours + otHours + weekendHours + holidayHours;
            float workingDays = totalDayHours / 8f;


            BigDecimal hourlyRate = employee.getBasicSalary()
                    .divide(BigDecimal.valueOf(26 * 8), 2, RoundingMode.HALF_UP);


            BigDecimal productionSalary = hourlyRate.multiply(BigDecimal.valueOf(totalDayHours));


            BigDecimal overtimeSalary = hourlyRate.multiply(
                    BigDecimal.valueOf(otHours*2 + weekendHours * 2 + holidayHours * 3)
            );

            List<SalaryBenefit> benefitItems = generateSalaryBenefits(employee, null);

            BigDecimal totalAllowance = benefitItems.stream()
                    .filter(b -> b.getBenefitType() == BenefitType.PHU_CAP)
                    .map(SalaryBenefit::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDeduction = benefitItems.stream()
                    .filter(b -> b.getBenefitType() == BenefitType.KHAU_TRU)
                    .map(SalaryBenefit::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal gross = totalAllowance
                    .add(productionSalary)
                    .add(overtimeSalary);

            BigDecimal net = gross.subtract(totalDeduction);
            Salary salary = Salary.builder()
                    .employee(employee)
                    .basicSalary(employee.getBasicSalary())
                    .workingDays(workingDays)
                    .productionSalary(productionSalary)
                    .overtimeHours(otHours + weekendHours + holidayHours)
                    .overtimeSalary(overtimeSalary)
                    .totalDeduction(totalDeduction)
                    .totalIncome(net)
                    .salaryMonth(salaryMonth)
                    .build();

            for (SalaryBenefit b : benefitItems) {
                b.setSalary(salary);
            }
            salary.setSalaryBenefits(benefitItems);
            salaryRepository.save(salary);

        }
    }


    private BigDecimal sum(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private float parseHour(String value) {
        if (value == null || value.isBlank()) return 0f;
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }


    @Override
    @Transactional
    public void regenerateMonthlySalaries(int month, int year) {


        LocalDate salaryMonth = LocalDate.of(year, month, 1);
        if (salaryRepository.existsBySalaryMonthAndLockedTrue(salaryMonth)) {
            throw new IllegalStateException("Bảng lương đã chốt, không thể cập nhật lại.");
        }

        salaryRepository.deleteBySalaryMonth(salaryMonth);


        generateMonthlySalaries(month, year);
    }

    @Override
    public List<LocalDate> getAvailableSalaryMonths() {
        List<Salary> salaries = salaryRepository.findAll();
        return salaries.stream()
                .map(Salary::getSalaryMonth)
                .distinct()
                .sorted() // sắp xếp tăng dần
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public void lockSalariesByMonth(int month, int year, boolean locked) {
        LocalDate salaryMonth = LocalDate.of(year, month, 1);
        List<Salary> salaries = salaryRepository.findBySalaryMonth(salaryMonth);

        for (Salary s : salaries) {
            s.setLocked(locked);
        }

        salaryRepository.saveAll(salaries);
    }
    @Override
    public Page<SalaryDTO> getSalariesByMonth(int month, int year, int page, int size, String search, Long departmentId, Long positionId, Long lineId) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        Pageable pageable = PageRequest.of(page, size);

        // Lấy tất cả các lương trong tháng
        List<Salary> allSalaries = salaryRepository.findBySalaryMonth(firstDay);

        // Lọc theo mã hoặc tên nhân viên nếu có search
        if (search != null && !search.trim().isEmpty()) {
            String lower = search.trim().toLowerCase();
            allSalaries = allSalaries.stream()
                    .filter(s -> s.getEmployee().getEmployeeCode().toLowerCase().contains(lower)
                            || s.getEmployee().getEmployeeName().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        // Lọc theo departmentId, positionId, lineId
        if (departmentId != null) {
            allSalaries = allSalaries.stream()
                    .filter(s -> s.getEmployee().getDepartment() != null &&
                            s.getEmployee().getDepartment().getDepartmentId().equals(departmentId))
                    .collect(Collectors.toList());
        }

        if (positionId != null) {
            allSalaries = allSalaries.stream()
                    .filter(s -> s.getEmployee().getPosition() != null &&
                            s.getEmployee().getPosition().getPositionId().equals(positionId))
                    .collect(Collectors.toList());
        }

        if (lineId != null) {
            allSalaries = allSalaries.stream()
                    .filter(s -> s.getEmployee().getLine() != null &&
                            s.getEmployee().getLine().getLineId().equals(lineId))
                    .collect(Collectors.toList());
        }

        // Lấy danh sách các benefit đang hoạt động
        List<Benefit> allBenefits = benefitService.getAllActive();

        // Map các đối tượng Salary thành SalaryDTO và áp dụng full benefits
        List<SalaryDTO> salaryDTOs = allSalaries.stream()
                .map(salary -> {
                    SalaryDTO dto = SalaryMapper.mapToSalaryDTO(salary);

                    List<SalaryBenefitDTO> fullBenefits = allBenefits.stream()
                            .map(benefit -> dto.getAppliedBenefits().stream()
                                    .filter(applied -> applied.getTitle().equals(benefit.getTitle()))
                                    .findFirst()
                                    .orElse(
                                            SalaryBenefitDTO.builder()
                                                    .title(benefit.getTitle())
                                                    .type(benefit.getBenefitType())
                                                    .amount(BigDecimal.ZERO)
                                                    .build()
                                    ))
                            .collect(Collectors.toList());

                    dto.setAppliedBenefits(fullBenefits);
                    return dto;
                })
                .collect(Collectors.toList());

        // Tính phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), salaryDTOs.size());

        List<SalaryDTO> pagedContent = start >= salaryDTOs.size() ? List.of() : salaryDTOs.subList(start, end);
        return new PageImpl<>(pagedContent, pageable, salaryDTOs.size());
    }


    private BigDecimal calculateAmount(BigDecimal basic, BigDecimal value, FormulaType type) {
        if (value == null || type == null) return BigDecimal.ZERO;
        return switch (type) {
            case AMOUNT -> value;
            case PERCENTAGE -> basic.multiply(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        };
    }
    private List<SalaryBenefit> generateSalaryBenefits(Employee employee, Salary salary) {
        List<BenefitRegistration> regs = benefitRegistrationRepository.findByEmployee(employee);
        List<SalaryBenefit> result = new ArrayList<>();

        for (BenefitRegistration reg : regs) {
            if (!Boolean.TRUE.equals(reg.getIsRegister())) continue;

            var bp = reg.getBenefitPosition();
            var benefit = bp.getBenefit();

            BigDecimal amount = calculateAmount(
                    employee.getBasicSalary(),
                    bp.getFormulaValue(),
                    bp.getFormulaType()
            );

            result.add(SalaryBenefit.builder()
                    .salary(salary)
                    .benefitType(benefit.getBenefitType())
                    .benefitTitle(benefit.getTitle())
                    .amount(amount)
                    .build());
        }

        return result;
    }

}
