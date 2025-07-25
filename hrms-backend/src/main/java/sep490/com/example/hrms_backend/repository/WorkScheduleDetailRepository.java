package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.WorkSchedule;
import sep490.com.example.hrms_backend.entity.WorkScheduleDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkScheduleDetailRepository extends JpaRepository<WorkScheduleDetail,Long> {
    List<WorkScheduleDetail> findByWorkSchedule_Id(Long workScheduleId);

    List<WorkScheduleDetail> findByWorkSchedule_MonthAndWorkSchedule_Year(int month, int year);

    List<WorkScheduleDetail> findByWorkSchedule(WorkSchedule workSchedule);

    boolean existsByWorkSchedule_IdAndDateWork(Long id, LocalDate currentDate);

    Optional<WorkScheduleDetail> findByWorkSchedule_IdAndDateWork(Long id, LocalDate currentDate);
}
