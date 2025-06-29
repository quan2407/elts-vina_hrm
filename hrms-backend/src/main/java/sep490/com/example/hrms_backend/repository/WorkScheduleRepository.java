package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sep490.com.example.hrms_backend.entity.WorkSchedule;

import java.util.List;
import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    Optional<WorkSchedule> findByLine_LineIdAndMonthAndYear(Long lineId, int month, int year);

    Optional<WorkSchedule> findByDepartment_DepartmentIdAndMonthAndYear(Long departmentId, Integer month, int year);

    boolean existsByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(Long departmentId, int month, int year);

    boolean existsByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(Long departmentId, Long lineId, int month, int year);

    boolean existsByMonthAndYearAndIsDeletedFalse(int month, int year);
    @Query("SELECT DISTINCT new com.example.hrms_backend.dto.WorkScheduleMonthDTO(ws.month, ws.year) FROM WorkSchedule ws")
    List<com.example.hrms_backend.dto.WorkScheduleMonthDTO> findAllAvailableMonths();

    Optional<WorkSchedule> findByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(
            Long departmentId,
            Long lineId,
            int month,
            int year
    );

    Optional<WorkSchedule> findByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(
            Long departmentId,
            int month,
            int year
    );
}
