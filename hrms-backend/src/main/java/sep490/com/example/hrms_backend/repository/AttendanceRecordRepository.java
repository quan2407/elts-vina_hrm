package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Employee;

import java.time.LocalDate;
import java.util.List;

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

    @Query("SELECT ar FROM AttendanceRecord ar " +
            "JOIN FETCH ar.employee e " +
            "JOIN e.department d " +
            "WHERE ar.date = :date " +
            "AND d.departmentId = :departmentId " +
            "AND ar.dayShift IN ('CKH', 'KH', 'NT', 'P', 'P_2', 'NTS')")
    List<AttendanceRecord> findAbsentEmpByDateDepartment(@Param("date") LocalDate date,
                                                         @Param("departmentId") Long departmentId);

    @Query("SELECT ar FROM AttendanceRecord ar " +
            "JOIN FETCH ar.employee e " +
            "JOIN e.line l " +
            "WHERE ar.date = :date " +
            "AND l.lineId = :lineId " +
            "AND ar.dayShift IN ('CKH', 'KH', 'NT', 'P', 'P_2', 'NTS')")
    List<AttendanceRecord> findAbsentEmpByDateLine(@Param("date") LocalDate date,
                                                   @Param("lineId") Long lineId);

    @Query("SELECT ar FROM AttendanceRecord ar " +
            "JOIN FETCH ar.employee e " +
            "JOIN e.line l " +
            "WHERE ar.date = :date " +
            "AND l.lineId = :lineId " +
            "AND ar.dayShift IN ('KL')")
    List<AttendanceRecord> findAbsentEmpByDateLineKL(@Param("date") LocalDate date,
                                                     @Param("lineId") Long lineId);

    @Query("SELECT ar FROM AttendanceRecord ar " +
            "JOIN FETCH ar.employee e " +
            "JOIN e.department d " +
            "WHERE ar.date = :date " +
            "AND d.departmentId = :departmentId " +
            "AND ar.dayShift IN ('KL')")
    List<AttendanceRecord> findAbsentEmpByDateDepartmentKL(@Param("date") LocalDate date,
                                                           @Param("departmentId") Long departmentId);

}

