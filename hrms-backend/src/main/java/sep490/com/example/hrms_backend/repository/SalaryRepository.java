package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sep490.com.example.hrms_backend.entity.Salary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    List<Salary> findBySalaryMonth(LocalDate salaryMonth);

    void deleteBySalaryMonth(LocalDate salaryMonth);

    boolean existsBySalaryMonthAndLockedTrue(LocalDate salaryMonth);

    List<Salary> findBySalaryMonthAndEmployee_EmployeeId(LocalDate firstDay, Long employeeId);
}
