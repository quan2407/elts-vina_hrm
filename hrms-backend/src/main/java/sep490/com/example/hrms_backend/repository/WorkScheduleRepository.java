package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sep490.com.example.hrms_backend.entity.WorkSchedule;
import sep490.com.example.hrms_backend.dto.WorkScheduleMonthDTO;
import java.util.List;
import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    Optional<WorkSchedule> findByLine_LineIdAndMonthAndYear(Long lineId, int month, int year);

    Optional<WorkSchedule> findByDepartment_DepartmentIdAndMonthAndYear(Long departmentId, Integer month, int year);

    boolean existsByDepartment_DepartmentIdAndLineIsNullAndMonthAndYear(Long departmentId, int month, int year);

    boolean existsByDepartment_DepartmentIdAndLine_LineIdAndMonthAndYear(Long departmentId, Long lineId, int month, int year);

    boolean existsByMonthAndYearAndIsDeletedFalse(int month, int year);
    @Query("SELECT DISTINCT new sep490.com.example.hrms_backend.dto.WorkScheduleMonthDTO(ws.month, ws.year) FROM WorkSchedule ws")
    List<WorkScheduleMonthDTO> findAllAvailableMonths();

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

    List<WorkSchedule> findByMonthAndYearAndIsSubmittedFalseAndIsDeletedFalse(int month, int year);
    List<WorkSchedule> findByMonthAndYearAndIsSubmittedTrueAndIsAcceptedFalseAndIsDeletedFalse(int month, int year);

    Optional<WorkSchedule> findByMonthAndYearAndLine_LineIdAndIsDeletedFalse(int month, int year, Long lineId);

    Optional<WorkSchedule> findByMonthAndYearAndDepartment_DepartmentIdAndLineIsNullAndIsDeletedFalse(int month, int year, Long deptId);

    List<WorkSchedule> findByMonthAndYearAndIsSubmittedTrueAndIsAcceptedFalse(int month, int year);

    List<WorkSchedule> findByMonthAndYearAndIsAcceptedTrue(int month, int year);
}
