package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.SalaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface SalaryService {
    List<SalaryDTO> getSalariesByMonth(int month, int year);
    void generateMonthlySalaries(int month, int year);
    void regenerateMonthlySalaries(int month, int year);
    List<LocalDate> getAvailableSalaryMonths();

    List<SalaryDTO> getEmpSalariesByMonth(Long employeeId, int month, int year);

    void lockSalariesByMonth(int month, int year, boolean locked);
}
