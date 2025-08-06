package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.SalaryBenefitDTO;
import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.entity.Salary;
import sep490.com.example.hrms_backend.entity.SalaryBenefit;

import java.util.List;
import java.util.stream.Collectors;

public class SalaryMapper {

    public static SalaryDTO mapToSalaryDTO(Salary salary) {
        List<SalaryBenefitDTO> benefitDTOs = salary.getSalaryBenefits().stream()
                .map(SalaryMapper::mapToBenefitDTO)
                .collect(Collectors.toList());

        return SalaryDTO.builder()
                .employeeCode(salary.getEmployee().getEmployeeCode())
                .employeeName(salary.getEmployee().getEmployeeName())
                .positionName(salary.getEmployee().getPosition() != null
                        ? salary.getEmployee().getPosition().getPositionName()
                        : null)

                .basicSalary(salary.getBasicSalary())
                .workingDays(salary.getWorkingDays())
                .productionSalary(salary.getProductionSalary())
                .overtimeHours(salary.getOvertimeHours())
                .overtimeSalary(salary.getOvertimeSalary())
                .totalDeduction(salary.getTotalDeduction())
                .totalIncome(salary.getTotalIncome())
                .salaryMonth(salary.getSalaryMonth())
                .locked(salary.isLocked())

                .appliedBenefits(benefitDTOs)
                .build();
    }

    private static SalaryBenefitDTO mapToBenefitDTO(SalaryBenefit salaryBenefit) {
        return SalaryBenefitDTO.builder()
                .title(salaryBenefit.getBenefitTitle())
                .type(salaryBenefit.getBenefitType())
                .amount(salaryBenefit.getAmount())
                .build();
    }
}
