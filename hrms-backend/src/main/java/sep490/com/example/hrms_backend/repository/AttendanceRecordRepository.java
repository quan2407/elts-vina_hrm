package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Employee;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
    List<AttendanceRecord> findByMonthAndYear(int month, int year);

    @Query("SELECT DISTINCT MONTH(ar.date), YEAR(ar.date) FROM AttendanceRecord ar ORDER BY YEAR(ar.date) DESC, MONTH(ar.date) DESC")
    List<Object[]> findDistinctMonthYear();

}
