package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sep490.com.example.hrms_backend.entity.Holiday;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Holiday h " +
            "WHERE h.startDate <= :dateWork AND h.endDate >= :dateWork AND h.isDeleted = false")
    boolean existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(@Param("dateWork") LocalDate dateWork);

    @Query("SELECT h FROM Holiday h WHERE h.isDeleted = false")
    List<Holiday> findAllNotDeleted();


    Optional<Holiday> findByIdAndIsDeletedFalse(Long id);


    boolean existsByIsDeletedFalseAndIsRecurringFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate newEnd, LocalDate newStart
    );

    boolean existsByIdNotAndIsDeletedFalseAndIsRecurringFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long id, LocalDate newEnd, LocalDate newStart
    );

    List<Holiday> findAllByIsDeletedFalseAndIsRecurringTrue();

    List<Holiday> findAllByIsDeletedFalseAndIsRecurringFalse();
}
