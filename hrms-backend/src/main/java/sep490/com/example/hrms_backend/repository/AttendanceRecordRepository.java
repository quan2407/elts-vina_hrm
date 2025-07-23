package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
    @Query("SELECT ar FROM AttendanceRecord ar " +
            "JOIN FETCH ar.workSchedule ws " +
            "LEFT JOIN FETCH ws.workScheduleDetails " +
            "WHERE ar.month = :month AND ar.year = :year")
    List<AttendanceRecord> findByMonthAndYear(@Param("month") int month, @Param("year") int year);


    @Query("SELECT DISTINCT MONTH(ar.date), YEAR(ar.date) FROM AttendanceRecord ar ORDER BY YEAR(ar.date) DESC, MONTH(ar.date) DESC")
    List<Object[]> findDistinctMonthYear();

    List<AttendanceRecord> findByEmployee_EmployeeIdAndMonthAndYear(Long employeeId, int month, int year);

    @Query("SELECT ar FROM AttendanceRecord ar " +
            "JOIN FETCH ar.workSchedule ws " +
            "LEFT JOIN FETCH ws.workScheduleDetails " +
            "WHERE ar.month = :month AND ar.year = :year AND ar.employee.employeeId = :employeeId ")
    List<AttendanceRecord> findByEmpIdAndMonthAndYear(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    List<AttendanceRecord> findByDate(LocalDate date);

    boolean existsByEmployee_EmployeeIdAndDate(Long employeeId, LocalDate workDate);

    Optional<AttendanceRecord> findByEmployee_EmployeeIdAndDate(Long employeeId, LocalDate date);

}
