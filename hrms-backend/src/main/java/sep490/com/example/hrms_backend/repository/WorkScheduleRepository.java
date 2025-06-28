package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.WorkSchedule;

import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    Optional<WorkSchedule> findByLine_LineIdAndMonthAndYear(Long lineId, int month, int year);

    Optional<WorkSchedule> findByDepartment_DepartmentIdAndMonthAndYear(Long departmentId, Integer month, int year);

    boolean existsByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(Long departmentId, int month, int year);

    boolean existsByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(Long departmentId, Long lineId, int month, int year);

    boolean existsByMonthAndYearAndIsDeletedFalse(int month, int year);
}
