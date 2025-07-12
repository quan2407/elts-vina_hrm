package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Salary;
import sep490.com.example.hrms_backend.mapper.SalaryMapper;
import sep490.com.example.hrms_backend.repository.AttendanceRecordRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.SalaryRepository;
import sep490.com.example.hrms_backend.service.SalaryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public List<SalaryDTO> getSalariesByMonth(int month, int year) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        List<Salary> salaries = salaryRepository.findBySalaryMonth(firstDay);

        return salaries.stream()
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
            float workingDays = totalWorkingHours / 8f;


            BigDecimal hourlyRate = employee.getBasicSalary()
                    .divide(BigDecimal.valueOf(26 * 8), 2, RoundingMode.HALF_UP);


            BigDecimal productionSalary = hourlyRate.multiply(BigDecimal.valueOf(totalDayHours));


            BigDecimal overtimeSalary = hourlyRate.multiply(
                    BigDecimal.valueOf(otHours + weekendHours * 2 + holidayHours * 3)
            );

            BigDecimal totalAllowance = sum(employee.getAllowancePhone())
                    .add(sum(employee.getAllowanceMeal()))
                    .add(sum(employee.getAllowanceAttendance()))
                    .add(sum(employee.getAllowanceTransport()));

            BigDecimal gross = totalAllowance
                    .add(productionSalary)
                    .add(overtimeSalary);

            BigDecimal bhxh = gross.multiply(BigDecimal.valueOf(0.08));
            BigDecimal bhyt = gross.multiply(BigDecimal.valueOf(0.015));
            BigDecimal bhtn = gross.multiply(BigDecimal.valueOf(0.01));
            BigDecimal unionFee = BigDecimal.valueOf(50);
            BigDecimal totalDeduct = bhxh.add(bhyt).add(bhtn).add(unionFee);

            BigDecimal net = gross.subtract(totalDeduct);

            Salary salary = Salary.builder()
                    .employee(employee)
                    .basicSalary(employee.getBasicSalary())
                    .allowancePhone(employee.getAllowancePhone())
                    .allowanceMeal(employee.getAllowanceMeal())
                    .allowanceAttendance(employee.getAllowanceAttendance())
                    .allowanceTransport(employee.getAllowanceTransport())
                    .workingDays(workingDays) // float
                    .productionSalary(productionSalary)
                    .overtimeHours(otHours + weekendHours + holidayHours)
                    .overtimeSalary(overtimeSalary)
                    .socialInsurance(bhxh)
                    .healthInsurance(bhyt)
                    .unemploymentInsurance(bhtn)
                    .unionFee(unionFee)
                    .totalDeduction(totalDeduct)
                    .totalIncome(net)
                    .salaryMonth(salaryMonth)
                    .build();

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

    private SalaryDTO mapToDTO(Salary s) {
        return SalaryDTO.builder()
                .employeeCode(s.getEmployee().getEmployeeCode())
                .employeeName(s.getEmployee().getEmployeeName())
                .basicSalary(s.getBasicSalary())
                .allowancePhone(s.getAllowancePhone())
                .allowanceMeal(s.getAllowanceMeal())
                .allowanceAttendance(s.getAllowanceAttendance())
                .allowanceTransport(s.getAllowanceTransport())
                .workingDays(s.getWorkingDays())
                .productionSalary(s.getProductionSalary())
                .overtimeHours(s.getOvertimeHours())
                .overtimeSalary(s.getOvertimeSalary())
                .socialInsurance(s.getSocialInsurance())
                .healthInsurance(s.getHealthInsurance())
                .unemploymentInsurance(s.getUnemploymentInsurance())
                .unionFee(s.getUnionFee())
                .totalDeduction(s.getTotalDeduction())
                .totalIncome(s.getTotalIncome())
                .salaryMonth(s.getSalaryMonth())
                .build();
    }
}
