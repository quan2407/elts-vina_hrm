package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.SalaryDTO;
import sep490.com.example.hrms_backend.entity.Salary;

public class SalaryMapper {

    public static SalaryDTO mapToSalaryDTO(Salary salary) {
        return SalaryDTO.builder()
                .employeeCode(salary.getEmployee().getEmployeeCode())
                .employeeName(salary.getEmployee().getEmployeeName())
                .positionName(salary.getEmployee().getPosition() != null
                        ? salary.getEmployee().getPosition().getPositionName()
                        : null)

                // ✅ [A] Phần cố định
                .basicSalary(salary.getBasicSalary())

                // ✅ [B] Phụ cấp
                .allowancePhone(salary.getAllowancePhone())
                .allowanceMeal(salary.getAllowanceMeal())
                .allowanceAttendance(salary.getAllowanceAttendance())
                .allowanceTransport(salary.getAllowanceTransport())

                // ✅ [C] Lương sản xuất
                .workingDays(salary.getWorkingDays())
                .productionSalary(salary.getProductionSalary())

                // ✅ [D] Lương thêm giờ
                .overtimeHours(salary.getOvertimeHours())
                .overtimeSalary(salary.getOvertimeSalary())

                // ✅ [E] Các khoản khấu trừ
                .socialInsurance(salary.getSocialInsurance())
                .healthInsurance(salary.getHealthInsurance())
                .unemploymentInsurance(salary.getUnemploymentInsurance())
                .unionFee(salary.getUnionFee())
                .totalDeduction(salary.getTotalDeduction())

                // ✅ [F] Tổng thu nhập
                .totalIncome(salary.getTotalIncome())

                // ✅ [G] Mốc thời gian
                .salaryMonth(salary.getSalaryMonth())
                .locked(salary.isLocked())
                .build();
    }
}
